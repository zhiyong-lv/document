# FutureTask
## FutureTask的父接口
FutureTask继承自RunnableFuture，而RunnableFuture继承了两个接口，Runnable和Future

首先看Runnable接口，从接口可以看出，Runnable只有一个函数run，表示继承了该类的子类或者接口可以执行。
```java
@FunctionalInterface
public interface Runnable {
    public abstract void run();
}
```

然后看Future接口。从字面意义可以分析得出，这个接口定义了一个运行中的任务对外提供的可以进行操作的接口，自身就是面向多线程的。
比如get函数为阻塞式获取运行结果的。调用这两个函数的线程肯定是一个与任务线程不同的线程，否则就不是阻塞式的等待结果返回，而是直接函数调用返回。
而isCancelled和isDone都是查询任务状态的方法。cancel则是直接终止该任务的运行。
```java
public interface Future<V> {
    boolean cancel(boolean mayInterruptIfRunning);
    boolean isCancelled();
    boolean isDone();
    V get() throws InterruptedException, ExecutionException;
    V get(long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException;
}
```

RunnableFuture继承了这两个接口。它本身没有新增方法，表明它是两个接口的结合体。说明它的子类或者子接口是一个可以运行的任务，并且提供了一组
可以在运行中被查询和获取结果的方法。

因此，作为RunnableFuture的实现类的FutureTask，就是一个可以运行并且在运行中可以被查询和取消的任务的一种实现。下面列出了一些使用例子
1. 取消一个正在运行的FutureTask
2. 查询运行的FutureTask是否已经运行完成
3. 查询运行的FutureTask是否已经取消
4. 阻塞式获取FutureTask的结果
5. FutureTask是可以作为一个线程单独运行的

## FutureTask的实现原理
### 猜想
在没有看FutureTask源码之前，首先来分析正常运行的情况，进而猜测一下源码是如何实现的。
FutureTask是一个具有返回值的任务，并且可以把运行中抛出的异常，都在get的时候再次抛出。
那么FutureTask应该有一个可以添加具体任务的方法，用来执行实际的任务。而这个方法极有可能是FutureTask的构造方法，通过实例化FutureTask时
传入的参数，让FutureTask知道实际运行的内容。而FutureTask应该会对用户传入的实际执行的任务程序进行包装，从而捕获到执行返回的结果，
或者抛出的异常。这里关键的问题是get怎么实现阻塞的？Java中阻塞可以有多种实现方式，比如通过await，notify的配合使用，达到阻塞的目的。
也可以使用AQS的锁机制，当FutureTask的任务开始运行时，将state设置为1，而其他线程调用get时，会由于tryAcquire发现state已经为1了，导致执行失败，
这时这个线程会将自身加入到等待队列中。 只有当FutureTask执行完成后，才会将state调整为0，并唤醒队列中等待的线程。这样调用get方法的线程就从阻塞中恢复了。

### 源码分析
下面我们看一下真正的代码中如何实现的，是否和猜想的相同.
下面为初始化，可以看出，这部分和猜想的一样，确实是通过构造函数传入了Callable来实现对实际执行任务的调用。
```java
public FutureTask(Callable<V> callable) {
    if (callable == null)
        throw new NullPointerException();
    this.callable = callable;
    this.state = NEW;       // ensure visibility of callable
}
public FutureTask(Runnable runnable, V result) {
    this.callable = Executors.callable(runnable, result);
    this.state = NEW;       // ensure visibility of callable
}
```

然后再来看运行部分。这部分可以发现，基本和猜想的相同，通过对传入callable对象的执行，捕获其结果或者异常。
```java
public void run() {
    if (state != NEW ||
        !UNSAFE.compareAndSwapObject(this, runnerOffset,
                                     null, Thread.currentThread()))
        return;
    try {
        Callable<V> c = callable;
        if (c != null && state == NEW) {
            V result;
            boolean ran;
            try {
                result = c.call();
                ran = true;
            } catch (Throwable ex) {
                result = null;
                ran = false;
                setException(ex);
            }
            if (ran)
                set(result);
        }
    } finally {
        // runner must be non-null until state is settled to
        // prevent concurrent calls to run()
        runner = null;
        // state must be re-read after nulling runner to prevent
        // leaked interrupts
        int s = state;
        if (s >= INTERRUPTING)
            handlePossibleCancellationInterrupt(s);
    }
}
```

当执行完传入的Callable之后，再使用通过set对result或者Exception进行设置。
设置的同时，会检查当前等待链表是否有等待的任务，如果有等待的任务，就会进行唤醒。


然后再来看get如何实现阻塞的。这部分和猜想的不完全一样。FutureTask使用了和AQS同样原理来实现，但根据自身特点，与AQS又有不同。
关于这部分，在java代码中有响应的解释。
```java
/*
 * Revision notes: This differs from previous versions of this
 * class that relied on AbstractQueuedSynchronizer, mainly to
 * avoid surprising users about retaining interrupt status during
 * cancellation races. Sync control in the current design relies
 * on a "state" field updated via CAS to track completion, along
 * with a simple Treiber stack to hold waiting threads.
 *
 * Style note: As usual, we bypass overhead of using
 * AtomicXFieldUpdaters and instead directly use Unsafe intrinsics.
 */
```

get的实现分为有超时时间和无超时时间的两个版本，具体实现细节比较类似。下面为get的实现方式。
可以看出来，只有当state状态小于competing的时候，才会执行awaitDone方法。这个方法就会
将当前执行get方法的线程使用头插法加入到等待队列中。然后自身睡眠。被唤醒后也是在这个函数中
重新检查state状态，是否可以读取返回结果或者异常。
```java
public V get() throws InterruptedException, ExecutionException {
    int s = state;
    if (s <= COMPLETING)
        s = awaitDone(false, 0L);
    return report(s);
}
```

## FutureTask兄弟类SwingWorker
SwingWorker和FutureTask都是继承自RunnableFuture。SwingWorker的目的是，使得耗时任务能够不影响EDT线程运行。
SwingWorker主要有几组接口：
1. doBackground和done （这部分和多线程相关）
2. publish和process （process也是在EDT线程中运行）
3. properties相关

SwingWorker内部实现了一个继承FutureTask，相当于对FutureTask的一种装饰模式，通过FutureTask实现了基本的RunnableFuture操作。