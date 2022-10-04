# ThreadPoolExecutor
ThreadPoolExecutor是java提供的线程池，可以根据用户的设置，将线程提前创建好。当需要使用线程的时候，就可以直接使用线程池中空闲线程，
从而省去了线程创建的时间。

## 继承关系分析
### Executor
Executor接口是对于提交任务的抽象。Java通过这个接口，将任务提交与任务管理结偶。这样用户就不需要关系任务创建，调度，执行的具体细节，而只需要
提交自己的任务就可以了，其他的事情可以交给Executor的实现类来完成。
```java
public interface Executor {

    /**
     * Executes the given command at some time in the future.  The command
     * may execute in a new thread, in a pooled thread, or in the calling
     * thread, at the discretion of the {@code Executor} implementation.
     *
     * @param command the runnable task
     * @throws RejectedExecutionException if this task cannot be
     * accepted for execution
     * @throws NullPointerException if command is null
     */
    void execute(Runnable command);
}
```

### ExecutorService
ExecutorService是Executor的子接口。那么也就是说它也是一个任务的执行器。这个任务执行器除了提供了任务提交的方法之外，也提供了对Executor
本身管理的一些接口，方便了用户的使用。同时，为了满足对单个任务的控制，ExecutorService拓展了任务提交的方式。ExecutorService的submit接口
正是用于这种目的的拓展任务提交方法。这个方法与execute不同的地方在于，submit会返回一个Future接口。这样用户可以通过Future接口对已经提交给
任务执行器的任务进行管理和监控。

总的来说，ExecutorService主要有以下几个作用：
1. 继承了Executor，也是一个任务执行器
2. 拓展了任务执行器本身的管理功能，可以让用户关闭整个任务执行器
3. 拓展了任务提交的方式。如通过submit提交后，得到返回的Future对象用来对已经提交的任务进行监控和控制。如批量提交和调用接口，能够方便的同时提交大量的任务。
4. Executors是ExecutorService的工具类。主要用来创建各种类型的ExecutorService

```java
public interface ExecutorService extends Executor {
    void shutdown();
    List<Runnable> shutdownNow();
    boolean isShutdown();
    boolean isTerminated();
    boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException;
    <T> Future<T> submit(Callable<T> task);
    <T> Future<T> submit(Runnable task, T result);
    Future<?> submit(Runnable task);
    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException;
    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException;
    <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException;
    <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;
}
```

### AbstractExecutorService
AbstractExecutorService继承自ExecutorService，是其默认的实现类。主要提供了各种任务提交相关的默认方法，方便子类在其基础上进行扩展。
AbstractExecutorService通过FutureTask作为任务的包装类，用来实现对单个任务的控制。**其子类需要实现execute方法，用于对线程池的管理**

## ThreadPoolExecutor实现
### 实现原理推测
下面是源码中关于ThreadPoolExecutor作用的一段解释
> An ExecutorService that executes each submitted task using one of possibly several pooled threads, 
> normally configured using Executors factory methods. 
> Thread pools address two different problems: they usually provide improved performance when executing 
> large numbers of asynchronous tasks, due to reduced per-task invocation overhead, and they provide a 
> means of bounding and managing the resources, including threads, consumed when executing a collection 
> of tasks. Each ThreadPoolExecutor also maintains some basic statistics, such as the number of completed 
> tasks.

首先，ThreadPoolExecutor继承了AbstractExecutorService。而AbstractExecutorService提供了底层由FutureTask实现的任务提交功能。
ThreadPoolExecutor只需要实现execute接口来实际分配资源执行提交的任务。并且实现如何关闭或者终止执行器。
通过上面的介绍可以看出，ThreadPoolExecutor主要需要解决几个问题：
1. 减少每个线程调度开销
2. 管理资源

由于ThreadPoolExecutor继承了抽象实现类AbstractExecutorService，那么其execute就是将提交的FutureTask执行的入口。
ThreadPoolExecutor是一种线程池方式的任务执行器。那么其本身需要建立一个线程池，提交的时候将FutureTask交给线程池执行，
执行完成后，将线程返回给线程池。

这里有几方面需要实现：
1. 如何利用已经创建好的线程执行提交的FutureTask任务
2. 如何知道线程池中的线程的状态是正在执行任务还是空闲
3. FutureTask执行过程中发生的异常如何处理
4. 提交的任务过多，超过了线程池上限时如何处理

所以，这就要求ThreadPoolExecutor
1. 需要有一个对现有的线程的包装类，且能查询处该类的运行状态。
2. 对这个包装类的管理策略
3. 对超出范围提交过来的FutureTask的处理策略

### 源码分析
#### 线程池状态
除了线程池中各个线程状态的跟踪之外，还需要考虑线程池整体的状态。
线程池的状态可以分为两个部分：
1. 线程池状态
2. 线程池中线程的数量

为了更好的管理这两种属性，ThreadPoolExecutor将它们放到了同一个AtomicInteger里面。
```java
public class ThreadPoolExecutor extends AbstractExecutorService {
    /**
     * The main pool control state, ctl, is an atomic integer packing
     * two conceptual fields
     *   workerCount, indicating the effective number of threads
     *   runState,    indicating whether running, shutting down etc
     *
     * In order to pack them into one int, we limit workerCount to
     * (2^29)-1 (about 500 million) threads rather than (2^31)-1 (2
     * billion) otherwise representable. If this is ever an issue in
     * the future, the variable can be changed to be an AtomicLong,
     * and the shift/mask constants below adjusted. But until the need
     * arises, this code is a bit faster and simpler using an int.
     *
     * The workerCount is the number of workers that have been
     * permitted to start and not permitted to stop.  The value may be
     * transiently different from the actual number of live threads,
     * for example when a ThreadFactory fails to create a thread when
     * asked, and when exiting threads are still performing
     * bookkeeping before terminating. The user-visible pool size is
     * reported as the current size of the workers set.
     *
     * The runState provides the main lifecycle control, taking on values:
     *
     *   RUNNING:  Accept new tasks and process queued tasks
     *   SHUTDOWN: Don't accept new tasks, but process queued tasks
     *   STOP:     Don't accept new tasks, don't process queued tasks,
     *             and interrupt in-progress tasks
     *   TIDYING:  All tasks have terminated, workerCount is zero,
     *             the thread transitioning to state TIDYING
     *             will run the terminated() hook method
     *   TERMINATED: terminated() has completed
     *
     * The numerical order among these values matters, to allow
     * ordered comparisons. The runState monotonically increases over
     * time, but need not hit each state. The transitions are:
     *
     * RUNNING -> SHUTDOWN
     *    On invocation of shutdown(), perhaps implicitly in finalize()
     * (RUNNING or SHUTDOWN) -> STOP
     *    On invocation of shutdownNow()
     * SHUTDOWN -> TIDYING
     *    When both queue and pool are empty
     * STOP -> TIDYING
     *    When pool is empty
     * TIDYING -> TERMINATED
     *    When the terminated() hook method has completed
     *
     * Threads waiting in awaitTermination() will return when the
     * state reaches TERMINATED.
     *
     * Detecting the transition from SHUTDOWN to TIDYING is less
     * straightforward than you'd like because the queue may become
     * empty after non-empty and vice versa during SHUTDOWN state, but
     * we can only terminate if, after seeing that it is empty, we see
     * that workerCount is 0 (which sometimes entails a recheck -- see
     * below).
     */
    private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
}
```

#### 线程Worker
线程池创建线程，并使用这些线程执行FutureTask。执行完成后，回收线程到空闲池中等待再次分派。
下面是Worker的源码。通过对这部分源码对分析，可以得出Worker对主要功能有
1. Worker与运行它对Thread是一一对应的。
2. 为了实现对Thread的管理，Worker中也实现了AQS的锁管理。这样，当worker运行时，线程池就不能再次进行这个worker了
```java
private final class Worker
        extends AbstractQueuedSynchronizer
        implements Runnable
{
    /**
     * This class will never be serialized, but we provide a
     * serialVersionUID to suppress a javac warning.
     */
    private static final long serialVersionUID = 6138294804551838833L;

    /** Thread this worker is running in.  Null if factory fails. */
    final Thread thread;
    /** Initial task to run.  Possibly null. */
    Runnable firstTask;
    /** Per-thread task counter */
    volatile long completedTasks;

    /**
     * Creates with given first task and thread from ThreadFactory.
     * @param firstTask the first task (null if none)
     */
    Worker(Runnable firstTask) {
        setState(-1); // inhibit interrupts until runWorker
        this.firstTask = firstTask;
        this.thread = getThreadFactory().newThread(this);
    }

    /** Delegates main run loop to outer runWorker  */
    public void run() {
        runWorker(this);
    }

    // Lock methods
    //
    // The value 0 represents the unlocked state.
    // The value 1 represents the locked state.

    protected boolean isHeldExclusively() {
        return getState() != 0;
    }

    protected boolean tryAcquire(int unused) {
        if (compareAndSetState(0, 1)) {
            setExclusiveOwnerThread(Thread.currentThread());
            return true;
        }
        return false;
    }

    protected boolean tryRelease(int unused) {
        setExclusiveOwnerThread(null);
        setState(0);
        return true;
    }

    public void lock()        { acquire(1); }
    public boolean tryLock()  { return tryAcquire(1); }
    public void unlock()      { release(1); }
    public boolean isLocked() { return isHeldExclusively(); }

    void interruptIfStarted() {
        Thread t;
        if (getState() >= 0 && (t = thread) != null && !t.isInterrupted()) {
            try {
                t.interrupt();
            } catch (SecurityException ignore) {
            }
        }
    }
}
```

runWorker部分的源码如下：
```java
class ThreadPoolExecutor {
    final void runWorker(Worker w) {
        Thread wt = Thread.currentThread();
        Runnable task = w.firstTask;
        w.firstTask = null;
        w.unlock(); // allow interrupts
        boolean completedAbruptly = true;
        try {
            while (task != null || (task = getTask()) != null) {
                w.lock();
                // If pool is stopping, ensure thread is interrupted;
                // if not, ensure thread is not interrupted.  This
                // requires a recheck in second case to deal with
                // shutdownNow race while clearing interrupt
                if ((runStateAtLeast(ctl.get(), STOP) ||
                        (Thread.interrupted() &&
                                runStateAtLeast(ctl.get(), STOP))) &&
                        !wt.isInterrupted())
                    wt.interrupt();
                try {
                    beforeExecute(wt, task);
                    Throwable thrown = null;
                    try {
                        task.run();
                    } catch (RuntimeException x) {
                        thrown = x; throw x;
                    } catch (Error x) {
                        thrown = x; throw x;
                    } catch (Throwable x) {
                        thrown = x; throw new Error(x);
                    } finally {
                        afterExecute(task, thrown);
                    }
                } finally {
                    task = null;
                    w.completedTasks++;
                    w.unlock();
                }
            }
            completedAbruptly = false;
        } finally {
            processWorkerExit(w, completedAbruptly);
        }
    }
}
```

