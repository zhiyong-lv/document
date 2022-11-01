# ForkJoinPool

## Fork and Join 背景知识

背景知识主要参考Doug Lea的论文[A Java Fork/Join Framework](https://gee.cs.oswego.edu/dl/papers/fj.pdf)。
[这里](https://blog.csdn.net/dhaibo1986/article/details/108727249)是这篇论文的中文翻译。

### 什么是Fork and Join

Fork Join是一种编程思想。主要是将大的任务分解成可以一次完成的线性小任务，然后将所有小的任务的结果都汇总在一起，返回最终的结果。 这种思想非常类似于Map Reduce的思想。

### Java 普通线程对 Fork and Join 任务的支持及限制

Java使用的是操作系统的原生线程。而使用原生的线程，对Fork and Join 任务的支持并不好

- 线程中对资源的消耗和对性能的监控，对于Fork和Join类型的任务来说是多余的。比如join时，需要让运行的任务等待子任务执行完成，这就造成了这个Thread资源的浪费。
- 由于Fork and Join任务要分解为需要小的任务，用来创建和调度这些任务的时间可能比实际执行这些任务的时间还要长。

### ForkJoin 对 Fork and Join 任务的支持

- 仍然使用Java的原生线程作为任务执行的载体
- ForkJoin任务的类型仍然是Java的Runnable任务，但这种runnable不在Thread中直接调用，而是使用特殊的ForkJoin机制来调用。避免Join时等待。
-

### 任务调度

使用了任务队列的方式实现了任务的调度

- 每个线程一个任务队列，这个任务队列为一个双端队列
- 线程执行的Fork and Join任务，当fork出新的子任务的时候，将这些任务加入到本线程对应的任务队列中
- 线程执行到join任务的时候，从该线程对应的队列中取一个任务出来执行
- 当线程执行到join的时候，等待的子任务没有完成，且该线程对应的任务队列中没有等待执行的任务，那么就会从其他的有任务任务队列中取任务出来执行
- 如果所有的任务都没有任务执行的时候，则线程等待外部的操作。

## ForkJoinPool

### 代码原理介绍

ForkJoinPool主要基于任务偷取来进行任务的调度工作。ForkJoinPool内部会有一个线程池，所有的fork join task及在运行中的子任务都会使用这个
线程池来执行。如果一个线程发现，它本身的任务队列中，没有未执行的任务，那么它就会从其他线程的任务队列中拿去任务继续执行。但是，如果这时其他的 线程对应的任务队列中也没有未执行的任务，那么该线程就会被阻塞。
即使是当ForkJoin任务产生大量的子任务的同时，外部又有大量小任务同时提交到pool中，ForkJoinPool也可以非常高效的进行处理。
特别的是，把asyncMode设置为true的ForkJoinPool也非常合适处理没有join操作的事件类型任务。 所有的work都设置为deamon模式

ForkJoinPool的默认线程池是commonPool。如果调用ForkJoinPool的时候没有特别的制定，都会使用这个pool。而使用这个pool会减少资源的消耗。
它会在没有使用的时候缓慢的释放资源，在连续的使用后，也会缓慢的增加资源。

ForkJoinPool支持在初始化的时候设置并行级别，这样就可以制定自定义的线程池。而默认的并行级别是处理器的数量。这个线程池通过动态的增加，挂起
或者唤醒线程来保证有足够的活动线程，即使是当一些任务在等待其他任务join的时候。然而，当有阻塞IO和未纳入管理的同步器的时候，就不能保证了。

下面为执行ForkJoinTask的方法

|                                | Call from non-fork/join clients | Call from within fork/join computations       |
|--------------------------------|:-------------------------------:|-----------------------------------------------|
| Arrange async execution        |      execute(ForkJoinTask)      | ForkJoinTask.fork                             |
| Await and obtain result        |      invoke(ForkJoinTask)       | ForkJoinTask.invoke                           |
| Arrange exec and obtain Future |      submit(ForkJoinTask)       | ForkJoinTask.fork (ForkJoinTasks are Futures) |

The parameters used to construct the common pool may be controlled by setting the following system properties:

- java.util.concurrent.ForkJoinPool.common.parallelism - 并行级别，非负整数
- java.util.concurrent.ForkJoinPool.common.threadFactory - ForkJoinPool.ForkJoinWorkerThreadFactory类型的class.
  使用系统类加载器来加载这个类.
- java.util.concurrent.ForkJoinPool.common.exceptionHandler - Thread.UncaughtExceptionHandler类型的类. 使用系统类加载器来加载这个类.
- java.util.concurrent.ForkJoinPool.common.maximumSpares - 为了维持设置的并行度的最大的额外线程数量 (default 256).

#### 实现

ForkJoinPool和它的内部类主要实现了管理一组工作线程的的机制。 non-FJ线程将任务提交到任务队列中，然后workers会从队列中将这些任务取出来，一般来说，之后会将这写任务分解成多个子任务。
workers可以互相偷取相互队列中的未执行的任务。 对任务的偷取通常都是随机扫描后获得的，这样会使总体的吞吐量更好。因为如果让producer来将任务分配给idle的worker，这个唤醒的时间是不确定的，
不如让已经在运行的线程直接自己扫描得到更快。 当worker从其自身的队列中取出任务的时候，可以是FIFO，也可以是LIFO（根据配置）， 当worker从其其他workers的队列中取出任务的时候，顺序是FIFO。
该框架最初是实现工作窃取来支持树形并行性工具的。随着时间的流逝，其可伸缩性优势导致了扩展和更改。以更好的支持更多不同使用的上下文。

##### WorkQueues

WorkQueues是一个特别的双端队列。ForkJoinPool的大多数操作都在WorkQueues中。 WorkQueues目前只支持三种类型的操作：

- pop 出栈，一般只在本身的线程调用
- push 入栈，一般只在本身的线程调用
- poll (steal) 偷取最早入栈的元素，非拥有该任务的线程调用。

这个双端队列的算法主要是依据Chase and Lev的论文"Dynamic Circular Work-Stealing Deque"来实现的。
但为了让Java的GC更好的工作，WorkQueues中的实现尽可能早的将取出来的任务对应的槽位设置为null。 这样，即使面对大量的拆分后的子任务，WorkQueues也可以很好的工作。
而为了实现这种变化，WorkQueues的用来进行CAS的表示是poll还是pop的标志被放到了每一个slot上，而不是原来的heap top上。

使用经典的循环数组操作，来执行将任务加入的操作。
> q.array[q.top++ % length] = task;

当进行pop操作的时候，其伪代码如下：
> if ((the task at top slot is not null) and (CAS slot to null))
> decrement top and return task;

当进行poll操作的时候，其伪代码如下：
> if ((the task at base slot is not null) and (CAS slot to null))
> increment base and return task;

由于indices和slot的内容不能时时保持一致，使得基于`base == top`来判断队列是否为空的方法有的时候会出现误判。 如当push，pop或者poll没有完全执行完的时候，会导致返回的结果是非空的队列。
或者是当对top的写入操作还没有显示时，返回为空队列。 正因为如此，当一个worker去其他worker的队列中取出任务的时候，有可能出现误判。不过，当前的实现还是保证了基本的非阻塞执行。
如果出现偷取失败，那么就会再次选取下一个worker的队列进行再次的偷取。

对于任务提交，WorkQueues也是用了类似的机制来处理。对于这些提交的任务，一般不会放到worker使用的相同的队列中。而是通过
hash的方式，将这些任务放入到不同的队列中。从原理上将，提交任务的线程运行起来和worker的工作方式是一样的，但会限制这些被提交的任务不
在他们所提交的任务中执行。任务提交的时候只是用了一个自旋锁。当遇到阻塞的时候，提交任务会选择其他的队列或者再建立一个新的队列。

##### Management

高吞吐量的优势主要来源于对WorkQueues的去中心化的管理。worker从自身或者其他worker的队列中取出任务进行执行。并且，其速度可以达到每秒10亿次。
线程池只基于最少的中心化的信息，就可以实现对于线程创建、激活、阻塞、销毁、deactive等操作。 而这些信息被打包放到一个很少的变量中，并且没有使用加锁的方式来进行维护。

- ctl有64位。它里面保存了用于原子方式添加，入队，出队和删除worker操作的信息。为了将所有的信息都存入crl中，我们把最大的并发限制在(1<<15)-1，这样就可以把ids, counts和门限值等都放入到16位子域里面
- mode这个field用来保存pool的状态信息。只能原子单调设置为SHUTDOWN, STOP,和 TERMINATED.
- workQueues保存了对workQueue的引用。只有当创建worker或者销毁worker的时候，才需要对其加锁进行访问。其他的时候都可以并发读取。

对于workQueues的访问，我们同样需要保证在调整大小时不会出现问题。这就需要通过在多个读操作之间用获取权限的操作进行间隔。
同样的，为了简化对index操作，workQueues的大小都是2的指数。同时，所有的读取操作，都要对slot为null的情况进行判断。
worker在奇数分片，而submitter在偶数分片，兵器提交者的数量最大为64个。这就能再需要更多的worker时对大小进行限制。 通过这种方式把他们组织在一起，能够简化并且加快对任务的扫描速度。

所有worker都是按需创建的。当提交任务，替换终止的worker，或对阻塞的worker进行补偿时，都会触发。
然而，所有其他的支持的代码都设置成与其他的策略一起工作。为了保证这一点，我们没有保存worker的引用，因为这样会阻碍GC的运行。
所有对workQueues的访问都通过队列上的分片进行的。简而言之，workQueues队列将向一个弱引用的机制。

当入队空闲的work时，我们并没有让worker在扫描不到的时候无限自旋，另外，除非出现很明显的有效的任务，我们不能启动或者回复worker。
另一方面，我们必须在新的任务提交或者产生时，很快的运行这些任务。在需要使用情况下，启动阶段都是影响总体性能的主要因素。 一般来说，与程序启动绑定在一起的是JIT编译和分配。所以我们尽可能的提升这部分的性能。

ctl成员原子低维护了总的worker和释放的worker的数量，另外加上可用工作队列的头节点。释放的workers一般来说都是要运行的任务。
这些workers。而没有释放的worker都记录到crl的stack中。对ctl的入队操作时，会使能这些worker。 为了避免来自wait和signal设计上的信号丢失问题，这些有效的worker会在入队操作后再次扫描任务。
通常来说，会在同时更新释放状态，但是释放的worker在crl中的数量有可能会低估了活跃线程的数量。 再一次失败的rescan之后，有效worker会被阻塞知道有信号到达。
栈顶状态同时持有worker的phase属性，这个phase是它的index和status，再加上一个version计数器。

###### 创建workers

要创建一个worker，我们首先将数量增加，然后尝试使用工厂方法构造一个ForkJoinWrokerThread。
构建的时候，新线程会调用registerWorker，这个方法会创建一个WorkQueue然后再workQueues数组中分配一个index。
然后，这个线程就被启动起来。如果在这些步骤中发生异常，或者构造工厂返回null，deresigerWorker会调整相应的数量和记录。 如果使用工厂方法创建的时候返回null，虽然这时线程的数量和target值不同，但线程池还是会继续运行。
如果发生异常，这个异常会被抛出给外部调用方。 worker index的分配避免在扫描的时候进行。如果entries在workQueues数组的前部开始顺序打包的时候。 这个数组是一个2的指数大小的数组，并根据需要进行扩容。
seedIndex的增加保证了大部分情况下冲突不会发生。但如果在resize的时候，或者worker被deregistered或者替换的时候，有可能会发生冲突。 然而这些冲突的可能性也会被限制的比较低。

WorkQueue的phase field由workers和pool共同使用，以管理和跟踪worker的UNSIGNALLED状态。
当worker入队后，它对应的field也会被设置。需要注意的是，这个phase如果为负数，也不能保证worker就是有效的。 当入队后，低16位一定要持有它所在线程池的index。

## Source code

当从外部线程提交一个ForkJoinTask到ForkJoinPool中的时候，会执行下面的函数。通过对代码的分析可以知道：
1. 退出的条件有两种：
   1. 线程池已经shutdown，或者workQueue本身的状态有问题的时候会抛出异常退出
   2. 成功加入并开始运行ForkJoinTask后，函数退出
2. 利用了`ThreadLocalRandom.getProbe()`和`ThreadLocalRandom.advanceProbe(r)`函数来处理hash碰撞
3. 对于workQueues数组来说，专门有一部分是用来存放外部提交的任务workQueue的。
   1. 如果通过hash发现数组已经存在了，那么就直接执行
   2. 如果数组不存在，那么就进行创建

```java
class ForkJoinPool {
    final void externalPush(ForkJoinTask<?> task) {
        int r;                                // initialize caller's probe
        if ((r = ThreadLocalRandom.getProbe()) == 0) {
            ThreadLocalRandom.localInit();
            r = ThreadLocalRandom.getProbe();
        }
        for (; ; ) {
            WorkQueue q;
            int md = mode, n;
            WorkQueue[] ws = workQueues;
            if ((md & SHUTDOWN) != 0 || ws == null || (n = ws.length) <= 0)
                throw new RejectedExecutionException();
            else if ((q = ws[(n - 1) & r & SQMASK]) == null) { // add queue
                int qid = (r | QUIET) & ~(FIFO | OWNED);
                Object lock = workerNamePrefix;
                ForkJoinTask<?>[] qa =
                        new ForkJoinTask<?>[INITIAL_QUEUE_CAPACITY];
                q = new WorkQueue(this, null);
                q.array = qa;
                q.id = qid;
                q.source = QUIET;
                if (lock != null) {     // unless disabled, lock pool to install
                    synchronized (lock) {
                        WorkQueue[] vs;
                        int i, vn;
                        if ((vs = workQueues) != null && (vn = vs.length) > 0 &&
                                vs[i = qid & (vn - 1) & SQMASK] == null)
                            vs[i] = q;  // else another thread already installed
                    }
                }
            } else if (!q.tryLockPhase()) // move if busy
                r = ThreadLocalRandom.advanceProbe(r);
            else {
                if (q.lockedPush(task))
                    signalWork();
                return;
            }
        }
    }
}
```
