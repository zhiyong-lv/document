# SynchronousQueue 源码分析

对SynchronousQueue进行写操作，一定要等到其他线程对其进行读操作才能返回。反之也是一样的。
由于这种特性，SynchronousQueue内部不需要保存任何元素，所以其内部容量为0。同样，SynchronousQueue也不能进行peak操作，
因为只有移除数据时，才能写入数据。SynchronousQueue不能使用iterator遍历，也是因为其内部容量为0，没有什么好遍历的。
队列的头部是第一个要想里面插入的数据。如果没有，那么就不能remove，并且poll操作会返回null。对于集合的其他操作，如contains，
SynchronousQueue都像一个空队列一样返回。SynchronousQueue不允许加入null值。

SynchronousQueue内部有公平和非公平两种实现方式。如果是公平的模式，那么他是一个FIFO的队列。

## 实现的思路

从SynchronousQueue的需求来看，对读写都要做限制，下面为一个利用Lock和Condition实现的版本。 但这个版本没有实现公平模式。

```java
class SynchronousQueueV2<E> {
    Lock lock = new ReentrantLock();
    Condition reading = lock.newCondition();
    Condition readDone = lock.newCondition();
    Condition writing = lock.newCondition();
    Condition writeDone = lock.newCondition();
    E temp;

    void put(E e) throws InterruptedException {
        lock.lock();
        try {
            while (true) {
                if (Objects.isNull(temp)) {
                    temp = e;
                    writeDone.signalAll();
                    readDone.await();
                    return;
                } else {
                    writing.signalAll();
                    reading.await();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    E take() throws InterruptedException {
        lock.lock();
        try {
            while (true) {
                if (Objects.nonNull(temp)) {
                    E e = temp;
                    temp = null;
                    readDone.signalAll();
                    writeDone.await();
                    return e;
                } else {
                    reading.signalAll();
                    writing.await();
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
```

## 源码分析
通过上面的这种实现可以看出来：

1. 简单，容易理解
2. 读写分别都加锁，吞吐量不高

那么，我们再看一下JDK源码中是如何实现的。

从更高的角度来看，读写操作都是对queue中元素的操作。而且，只有成对出现的时候才能够正常执行，其他的情况都只能被阻塞。
可以考虑将读写操作都抽象成为对queue的操作，然后每个操作都可以加入一个操作队列之中。
然后通过对队列中各个节点的处理，完成相应的操作。

当能够放到一个队列中后，就可以避免对同一个元素操作带来的锁竞争的问题。也可以重新自旋锁，进一步减少锁对吞吐量的影响。

### Transferer接口
为了表示SynchronousQueue这种成对的操作，开发者在其内部将这种成对出现的数据读写操作抽象为Transferer接口
作为其内部抽象类。
```java

/**
 * Shared internal API for dual stacks and queues.
 */
abstract static class Transferer<E> {
    /**
     * Performs a put or take.
     *
     * @param e if non-null, the item to be handed to a consumer;
     *          if null, requests that transfer return an item
     *          offered by producer.
     * @param timed if this operation should timeout
     * @param nanos the timeout, in nanoseconds
     * @return if non-null, the item provided or received; if null,
     *         the operation failed due to timeout or interrupt --
     *         the caller can distinguish which of these occurred
     *         by checking Thread.interrupted.
     */
    abstract E transfer(E e, boolean timed, long nanos);
}
```

Transferer的接口中，只有一个方法，就是transfer。而transfer方法同时可以表示两种操作。
也就是说通过transfer操作，可以将读写操作都转化为对队列的transfer操作。
这样就为将读写操作放入同一个操作队列中打下了基础。

### TransferStack
TransferStack是SynchronousQueue的stack实现

service:
    host:
        The total number of hosts that can be added or imported into this tool
        maxCount: 5000
