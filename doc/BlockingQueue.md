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





