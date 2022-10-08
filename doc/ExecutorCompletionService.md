# ExecutorCompletionService
## 作用
从提交的一批任务中得到最快返回的一个任务的结果

## 继承关系分析
### CompletionService
对于这个接口，代码中对其作用的解释如下：
> A service that decouples the production of new asynchronous tasks from the consumption of the results of completed 
> tasks. Producers submit tasks for execution. Consumers take completed tasks and process their results in the order 
> they complete. A CompletionService can for example be used to manage asynchronous I/O, in which tasks that perform 
> reads are submitted in one part of a program or system, and then acted upon in a different part of the program when 
> the reads complete, possibly in a different order than they were requested. 
> Typically, a CompletionService relies on a separate Executor to actually execute the tasks, in which case the 
> CompletionService only manages an internal completion queue. The ExecutorCompletionService class provides an 
> implementation of this approach.
> 
> 主要是对任务的生产之和任务结果的消费者之间的解偶。生产者提交任务执行，消费者得到完成的任务并且按照*完成的顺序*处理结果

```java
public interface CompletionService<V> {
    Future<V> submit(Callable<V> task);
    Future<V> submit(Runnable task, V result);
    Future<V> take() throws InterruptedException;
    Future<V> poll();
    Future<V> poll(long timeout, TimeUnit unit) throws InterruptedException;
}
```

## 源码分析
对于ExecutorCompletionService来说，主要功能分为两个部分：
1. 任务的提交（生产）
2. 任务的消费（消费）

通过分析ExecutorCompletionService的源码，其通过ExecutorService接口来完成任务的提交。通过阻塞队列来提供任务结果消费的接口
内部实现的主要逻辑集中于如何结合生产和消费。

ExecutorCompletionService主要是通过内部实现的BlockingQueue，来缓存实现的结果。
一旦任务产生结果，就把结果放到队列中，这时一直执行poll查询的线程就会从阻塞态返回，这样就可以进行结果的处理。

也就说说ExecutorCompletionService是一个利用阻塞队列来解偶生产者和消费者的例子。

但ExecutorCompletionService需要注意的一点是，不管有没有返回值，都需要在poll后执行一次take操作。
否则会造成执行线程后的结果长期保留在阻塞队列中不能回收，导致OOM的发生。

```java
public class ExecutorCompletionService<V> implements CompletionService<V> {
    private final Executor executor;
    private final AbstractExecutorService aes;
    private final BlockingQueue<Future<V>> completionQueue;

    private class QueueingFuture extends FutureTask<Void> {
        QueueingFuture(RunnableFuture<V> task) {
            super(task, null);
            this.task = task;
        }

        protected void done() {
            completionQueue.add(task);
        }

        private final Future<V> task;
    }

    public ExecutorCompletionService(Executor executor) {
        if (executor == null)
            throw new NullPointerException();
        this.executor = executor;
        this.aes = (executor instanceof AbstractExecutorService) ?
                (AbstractExecutorService) executor : null;
        this.completionQueue = new LinkedBlockingQueue<Future<V>>();
    }

    public Future<V> submit(Callable<V> task) {
        if (task == null) throw new NullPointerException();
        RunnableFuture<V> f = newTaskFor(task);
        executor.execute(new QueueingFuture(f));
        return f;
    }

    public Future<V> take() throws InterruptedException {
        return completionQueue.take();
    }

    public Future<V> poll() {
        return completionQueue.poll();
    }
}
```