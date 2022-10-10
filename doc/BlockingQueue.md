# BlockingQueue 
阻塞队列，可以用于生产者消费者模型

## 继承关系
### Collection
Collection是抽象的集合概念，有几类功能：
1. 集合可以添加、删除元素
2. 可以获取集合的大小，判断是否为空
3. 可以判断是否包含元素
4. 可以遍历整个集合
5. 转化为其他的类型，如stream和array

### Queue
Queue集成了Collection。除了具备集合的特点之外，还有自身的特点。一般来说Queue是一个有序的队列。
* FIFO：先进先出
* Priority Queue：优先级队列
* LIFO：后进先出

Queue按照对元素操作种类来分，有三种：
1. 加入 insertion
2. 抽取 extraction
3. 探查 inspection

按照返回类型来分，有两种：
1. 失败返回异常
2. 失败返回特定值

| Actions    | Throws exception | Returns special value        |
|------------|------------------|------------------------------|
| Insert     | add(e)           | offer(e)                     |
| Remove     | remove(e)        | poll(e)                      |
| Examine    | element(e)       | peak(e)                      |


### BlockingQueue
BlockingQueue在Queue的基础上增加了阻塞式操作队列的操作。
* 当队列为空时，取出元素的操作会被阻塞
* 当队列为满时，增加元素的操作会被阻塞

BlockingQueue的主要操作如下：

| Actions    | Throws exception | Returns special value        | Blocks  | Times out            |
|------------|------------------|------------------------------|---------|----------------------|
| Insert     | add(e)           | offer(e)                     | put(e)  | offer(e, time, unit) |
| Remove     | remove(e)        | poll(e)                      | take(e) | poll(time, unit)     |
| Examine    | element(e)       | peak(e)                      | -       | -                    |


对于BlockingQueue来说，不能插入null；而Queue只是一般来说不建议插入null。
并且对于BlockingQueue来说，虽然继承了Queue接口和Collection接口，但一般不建议使用阻塞接口以外的接口。
BlockingQueue还提供了查询剩余可以插入的元素的数量，即剩余容量的查询接口。如果为无限容量，name就返回Integer.MAX_VALUE

### TransferQueue
TransferQueue是BlockingQueue的子接口，主要是为了确保发送的信息能被消费者消费到而增加来一些接口
> A BlockingQueue in which producers may wait for consumers to receive elements. A TransferQueue may be useful for example in message passing applications in which producers sometimes (using method transfer) await receipt of elements by consumers invoking take or poll, while at other times enqueue elements (via method put) without waiting for receipt.

```java
public interface TransferQueue<E> extends BlockingQueue<E> {
    boolean tryTransfer(E e);
    void transfer(E e) throws InterruptedException;
    boolean tryTransfer(E e, long timeout, TimeUnit unit) throws InterruptedException;
    boolean hasWaitingConsumer();
    int getWaitingConsumerCount();
}
```

## 实现类
### LinkedBlockingQueue
LinkedBlockingQueue是BlockingQueue的一个FIFO实现类。
* FIFO
* 如果没有制定长度，那么最大长度是Integer.MAX_VALUE；制定后为指定值
* 吞吐量比较大，但在大多数的并发场景下，性能可预测性比较差

对于LinkedBlockingQueue来说，它通过分别的读写锁来实现。而元素的数量则使用一个AtomicInteger来表示，从而避免对数值变化是，同时获取两把锁。
这样可以保证多个并发读操作或者多个并发写操作当原子性。
那么读写之间的原子行如何保证呢：
1. 首先我们看LinkedBlockingQueue内部数据结构的时间。其内部所有的元素都在一个链表上。头节点为element为null的节点。当链表没有任何元素时，头节点的element为null，其next也为null。
2. 如果count为0时，对LinkedBlockingQueue进行读写操作，所有的take操作都会被阻塞，而put操作不会被阻塞。也就是说所有对空的头节点的读操作都会被阻塞，不能执行，对头节点只能进行写操作。很显然，这时候读写操作不会同时操作。
3. 当执行写操作时，首先将要写入的node加入到链表上，然后再改变count的值。由于读操作阻塞条件时监控count的值，即使有一个瞬间，新的node被加入到链表上，而数量还没有变，读操作也感知不到。
4. 通过对count的监控，这样就保证了读写操作不会同时操作头节点的next指针。
5. 当count不为0，且小于最大容量时，这时读写操作都可以进行。由于有读操作操作的时头指针，而写操作则操作的时尾指针，读写操作分别操作链表的不同node，所以读写操作不会产生冲突
6. 当count达到了最大容量时，这时只能进行读操作。所以也不会读写冲突。
7. 当对LinkedBlockingQueue遍历时，所有读写锁同时都会锁住。

具体的读写操作时，链表的变化可以参考[LinkedBlockingQueue.drawio](LinkedBlockingQueue.drawio)

### ArrayBlockingQueue
ArrayBlockingQueue也是一个BlockingQueue的FIFO实现类。与LinkedBlockingQueue的区别在于，它的底层存储结构使用的是数组而非链表。
并且，ArrayBlockingQueue还支持一种公平的FIFO模式。这种公平的FIFO模式会降低变化避免某些线程长时间得不到响应，但是也会降低队列总的性能。

首先分析ArrayBlockingQueue的内部数据结构。

ArrayBlockingQueue内部有
1. 一个数组用来存放数据； 
2. 一个putIndex和一个takeIndex分别用来表示当前put和take的位置，初始化为0；
3. count表示当前元素的数量，初始化为0
4. lock为全局读写锁
5. notEmpty和notFull都是从lock中生成的Condition变量


通过分析源码可以知道
1. ArrayBlockingQueue初始化时，会根据输入的capacity大小建立一个数组
2. 读写操作时，首先要加全局锁
3. put直接将要写入的数据写入到putIndex指向的位置，然后putIndex自增1，一旦回绕，则从数组头0开始
4. take直接读取takeIndex指向位置的数据，然后putIndex也自增1，一旦回绕，则从数组头0开始
5. 保证线程按照请求顺序访问，主要是靠将lock初始化为一个公平锁。公平锁每次尝试获取lock之前，都会查看是否有其他线程在等待，而非公平锁不会，如果能马上得到锁，则会立即执行。

### PriorityBlockingQueue
PriorityBlockingQueue底层使用一个基于数组的二叉堆来实现的。二叉堆满足二个特性：
1．父结点的键值总是大于或等于（小于或等于）任何一个子节点的键值。
2．每个结点的左子树和右子树都是一个二叉堆（都是最大堆或最小堆）。

二叉堆上，queue[n]的子节点为queue[2*n+1]和queue[2(n+1)]。
PriorityBlockingQueue使用的堆是一个最小堆。

数组实现的最小二叉堆上，插入一个新堆元素的操作如下：
1. 检查是否需要扩容，并在需要扩容时完成扩容
2. 尝试将新元素加入队列中，首先放到队列末端（array中实际数据存放的末端）
3. 调整二叉堆，以满足二叉堆的条件
   * 父结点的键值总是小于或等于任何一个子节点的键值
   * 每个结点的左子树和右子树都是一个最小堆
4. 如果要满足二叉堆的条件，首先要看加入的新的元素，有可能会破坏哪些条件的
   1. 新元素小于其父节点
   2. 新元素所在的子树不是一个最小堆
5. 调整的原则为，找到当前新插入的元素的父节点，然后比较大小，如果父节点比新的节点小，则进行交换，直到完成堆顶元素的替换
6. 这样调整后，将原有比新节点大的父节点和当前节点调换位置后，原父节点的父节点本来有的两个节点，其中一个未变，肯定是小于该节点的。新换过的节点比原来的更小，肯定也是小于该节点的。换过后的子树由于每次替换都保证整个子树为一个最小树，所以肯定也是最小树。
7. 时间复杂度为1->logN

移除堆顶元素的操作如下：
1. 说明移除原理之前，首先再次看一下二叉堆要满足的条件：
   1. 父结点的键值总是小于或等于任何一个子节点的键值
   2. 每个结点的左子树和右子树都是一个最小堆
2. 那么说明移除以后，仍然要满足这两个条件。
3. 分析移除了头节点后的二叉堆，其最小值可能在哪里？
   1. array[1] 左子树
   2. array[2] 右子树
4. 那么究竟在左子树还是右子树上呢？可以将最后一个元素取出，放入到头节点的位置上，然后分别和左右子树比较，看那个小就和哪个交换，直到交换到最尾端。
5. 时间复杂度为logN


对这个堆所有的操作都由一个全局锁来保护。
然而，当调整堆大小的时候，会使用一个简单的自选锁来允许并发的读操作。

PriorityBlockingQueue的内部数据结构：
* queue：用于存放平衡二叉树的队列。。
* lock：全局锁
* comparator：比较器，用来比较元素的大小
* notEmpty：condition，当为空时，会进行阻塞








