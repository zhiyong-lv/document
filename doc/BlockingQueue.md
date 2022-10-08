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

### 





