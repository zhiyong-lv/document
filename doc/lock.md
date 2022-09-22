# Lock
## Lock接口
### Lock接口与synchronized原语的区别
1. 使用synchronized原语的同步方法或语句的使用提供了对与每个对象关联的隐式监视器锁的访问，但强制所有锁的获取和释放以块结构的方式发生：
2. 当获取多个锁时，它们必须以相反的顺序释放，并且所有锁必须在获得它们的相同词法范围内释放
3. 使用Lock接口，可以使得加锁和解锁不在同一个代码块
4. 使用Lock接口，可以使加锁和解锁不一定按照synchronized那样的顺序进行
5. 但是当使用Lock接口在不同的代码块获取和释放锁时，一定要注意要确保Lock在任何分支的释放（try-catch or try-finally）
6. Lock相比于synchronized来说，提供了更多的功能，比如非阻塞的tryLock，带超时的lock和带interrupt操作的lock
7. Lock也可以比synchronized原语提供更多的行为和语义，如保证顺序行，不可重入，或者死锁探测等


## Condition接口
Condition接口与Lock接口结合起来，将原来在单个object里面的await，notify和notifyAll方法分解出来，分散到不同的对象之中。
从功能上来说，Condition接口等价与await，notify和notifyAll方法，而Lock接口等同于synchronized。

既然Condition接口的功能和Object里面await，notify和notifyAll的功能相似，为什么还要单独建立一套呢？
1. 内部实现机制不同，Condition必须和Lock配合使用，不能和synchronized配合使用。
2. synchronized和加锁的object对应，而只有加锁的object才能够进行await等操作。如果需要多个等待机制的时候，程序就会变得很复杂
3. Condition的创建方式是从Lock实例中调用工厂方法newCondition创建。也就是说，一个Lock实例可以对应多个Condition实例。当需要多个等待机制的时候，编程就会变得简单
4. 实现方式更加的灵活。Condition的实现中可以保证通知的顺序性，或者不需要持有锁就可以发送notify。当然，如果Condition的这个实现类实现了这些功能，需要特别标记出来

由于java虚拟机在不同操作系统自身的差异，Condition被唤醒的时候，可能是一个虚假的唤醒。这就要求具体的实现中，需要将Condition的await方法放到一个循环中，
一旦被唤醒，就去检查是否满足await的条件，否则继续阻塞，等待下一次的唤醒。

同样的，对于interrupt等操作，不能保证在不同平台上的java虚拟机都能精确的按照定义来实现。例如对于顺序的保证。更进一步来说，
不同的平台上不能保证完全真正的把一个线程悬挂起来。

### await
1. 当前的线程进入等待态，直到被唤醒或者被中断
2. 当线程调用await时，需要提前持有Condition实例对应的Lock实例。
3. 一旦await，那么就会释放Condition对应的Lock，且同时将当前线程设置为等待态
4. 下面四个操作会将当前的线程从等待态唤醒
   1. 同一个Condition实例notify
   2. 同一个Condition实例notifyAll
   3. await的线程被interrupt
   4. spurious wakeup（虚假唤醒）
5. 唤醒以后的线程需要持有Condition对应的锁

### awaitUninterruptibly
基本上与await相同，只是不能被interrupt唤醒

### awaitNanos
基本上与await相同，区别在于：
1. 等待时间到了以后，也会自动结束等待态
2. awaitNanos的返回值是一个估计剩余等待时间的纳秒值
3. 所以被唤醒后，需要根据awaitNanos的值来判断是什么引起的唤醒，如果是超时导致的，需要做特殊的处理

### signal & singalAll
唤醒await的线程。被await的线程在被唤醒之前，需要先抢到锁。
一般signal也在持有锁的线程里，也就是说先发送完signal之后，其他的线程不会马上得到锁，只有当发送signal的线程释放锁以后
其他的线程才会真正的去竞争锁，竞争得到锁的线程才会被唤醒。

## ReadWriteLock
### 读写锁相互关系

|     | 无锁      | 读锁  | 写锁    |
|-----|:--------|-----|-------|
| 加读锁 | Success | Success    | Block |
| 加写锁 | Success        | Block    |   Block    |

## AbstractQueuedSynchronizer
提供了一种框架，实现了以来与先入先出队列实现的阻塞锁和相关的同步器，如信号量和事件等。
这个类为大多数使用int值来表示状态的同步器提供了基本的支持

提供了排它锁和共享锁两种模式，而这两种模式的实现都依赖于先入先出队列。具体采用那种模式，由子类决定。

如果要继承这个类实现一个最基本的同步器，需要实现下面的几个函数：
1. tryAcquire
2. tryRelease
3. tryAcquireShared
4. tryReleaseShared
5. isHeldExclusively

# 自选锁原理
## 排队式自选锁思想启蒙
AbstractQueueSynchronizer简称AQS，是CLH锁的一个变体。

### CLH问题的提出
一个进程有两种等待锁的方式：
1. 消极式等待：等待时，让渡cpu的控制权，进入waiting状态，直到得到锁
2. 积极式等待：保持CPU的控制权，不断检查直至可以执行。也称为自旋锁（spinLock）

其中一种自旋锁为排队式自选锁，主要为了解决：
1. 减少多个CPU之间的开销。多个等待获取锁的进程，不需要自旋监控或者尝试访问同一个内存位置
2. 使用队列来控制，就可以实现有序性

自选锁的基础为test and set指令，也就是常说的cas（compare and swap）。
基于这条指令，经常使用的自选锁的设计方式为：
```aidl
lock = CLEAR
while(result==true) {
    result = compareAndSet(lock, CLEAR, LOCK)
}
lock = CLEAR
```

这种简单的自选锁会造成两方面的问题
1. 大量线程访问同一个锁的内存的时候，造成大量的直接读操作，不能利用缓存。降低了速度
2. 解锁的set操作和获取锁的compareAndSet操作都对同一个内存区域进行访问，都需要进行排队。造成了解锁的减慢
3. 大量直接读操作占用了内存总线的带宽

为了解决简单自选锁的问题，队列式自选锁利用队列的方式，避免多个线程对同一个内存地址的访问。
下面式伪代码
```aidl
init:
    lock = int[CPU_COUNT]
    lock[0 -> CPU_COUNT-1] = [hasLock, mustWait, ... , mustWait]
    current = 0;
lock:
    index = getAndIncrease(current)
    while (lock[indxe % mod] == mustWait) {}
unlock:
    lock[index % mod] = mustWait
    lock[index % mod] = hashLock
```
1. 每一个线程进来以后，都会根据current的位置拿到自己要访问的队列节点的位置
2. 每个线程自旋访问对一个节点的时候，都只会对独立的一块内存进行访问，大大减少的竞争
3. 由于排队式自旋锁在线程竞争较少的时候，需要做比较多的判断，导致竞争比较少情况下性能不如简单自旋锁。
4. 只有竞争情况比较多的情况下，排队式自选锁的性能优势才会显示出来

### Ticket锁
这种锁利用了餐馆排号的思想。每个要排队的人取一个号，比对当前生效的号，然后判断是否取得了锁。
伪代码如下：
```java
class lock {
    // init:
    int nextTicket = 0;
    int currentValidTicket = 0;
    
    void getTicket() {
        return getAndIncrease(nextTicket);
    }
    
    void acquire(int ticket) {
        while(ticket - currentValidTicket > 0){
            ;
        }
    }
    
    void release(int ticket) {
        if (ticket == currentValidTicket) {
            getAndIncrease(currentValidTicket);
        }
    }
}
```

从上面的代码可以看出：
1. 加锁操作相当于getTicket和acquire两个操作的结合
2. 由于对currentValidTicket访问频繁，大量竞争且该值变化频繁情况下，缓存利用比较低

### MCS锁
MCS锁和之前的排队式自旋锁不同，它使用了链表代替了队列。
其伪代码如下：
```java
class Node {
   Lock next = null;
   boolean locked = false;
}

class Lock {
    Node cur = null;
    
    Node acquire(Node node) {
        node.locked = true;
        Node predecessor = fetchAndStore(this.cur, node);
        if (null != predecessor) {
           predecessor.next = node;
           while (predecessor.locked) ;
        }
        return node;
    }
    
    void release(Node node) {
        // successor
        if (node.next == null) {
            if (compareAndSet(this.cur, node, null)) {
                return;
            }
            while(node.next == null);
        }
        node.next.locked = false;
    }
}
```

原理如下：
1. 每个线程都会持有自己的一个Node，用来申请锁和释放锁
2. 各个线程的Node会依照申请锁的顺序，形成一个链表
3. Node里面包含了2个属性，next和locked。其中next用来指向Node链表的下一个Node，locked用来表示当前的线程是否申请到了这个锁。
4. locked为true，表示当前线程被阻塞，没有申请到锁；为false，表示已经申请到锁，当前线程正在处理被锁保护的资源；释放了锁之后，持有Node的线程就会把这个Node销毁掉，或者重新申请锁做下一步处理。
5. Lock对象本身使用了一个cur的指针，指向当前线程列表的末端
6. Lock对象的初始化状态：
   1. Lock对象初始状态时，cur为null，说明目前没有线程申请这个锁，那么只要有申请的线程，就会成功的占有这个锁。
   2. Lock对象cur为null也是一种懒加载的措施，直到有申请锁的线程提交请求后，才对cur进行真正的赋值。
7. Lock对象acquire操作：
   1. Lock对象为初始化状态时，cur为null，这时只要申请的node不为null，那么就可以获得锁
   2. Lock的cur已经指向了node，这时申请锁的线程需要将本线程持有的Node加入到队列中，并使得Lock的cur指向这个Node，
   3. 同时这个node的next设置为null，locked设置为true
   4. 为了达到阻塞的目的，这时程序进入死循环，直到locked为false
   5. 特别注意的是，为了达到多线程下的线程安全性，将cur值设为新传入的node时，需要执行一个原子操作fetchAndStore，这样就保证了cur值设为新的node值时，原来的cur值被放入了prodecessor变量中
   6. prodecessor如果不为null，说明这时的Lock对象中，已经有多个线程在排队了。为了成功的将node加入，需要先设置node的locked值为true，确保不会被其他的线程覆盖，然后再将prodecessor的next指向node
8. Lock对象的release操作：
   1. 如果需要进行release操作，说明该线程必然已经申请到了锁，那么其持有的node的locked值为false
   2. 如果要release锁的线程，持有的node的next指针不为null，那么释放锁操作只需要将node指向的链表中下一个node的locked设置为false即可，获取锁第四步进入阻塞的线程就会成功的得到锁
      1. 对node的next指针指向的node的locked进行操作不会造成线程冲突的问题
      2. 这时因为对locked设置值为true，是在将该node挂入到node链表之前的操作
      3. 一旦node挂入了弄的链表，其locked值不会被主动改变，除非在release的时候
   3. 如果要release锁的线程，持有的node的next指针为null，那么有两种可能
      1. 一种是这个node为lock的cur指向的最后的一个node节点
      2. 另外一种是这个node已经不是lock的cur指向的最后一个node节点，但该node的next应该指向的node还没有挂入到node链表中
   4. 所以，为了保证原子性，需要做一次compareAndSwap的操作，如果lock的cur值为node的情况下，将cur值修改为null
      1. 对cur值的修改，还有一个地方就是在获取锁的时候。
      2. 但获取锁和释放锁时，修改cur值都使用了原子性的操作
   5. 如果CAS操作成功了，那么说明node确实为最后一个节点，这时就可以直接返回，说明release已经成功了
   6. 如果CAS操作没有成功，说明node已经不是最后一个几点，那么它的next可能已经被赋值，也可能暂时没有赋值，这时需要增加一个循环进行等待，直到next被赋值
   7. next不为空后，对next所指向的node的locked值设置为false，使得阻塞在检查locked状态的线程能够继续执行。

MCS锁有什么问题呢：
1. 每一个线程在阻塞时，都会进入死循环状态，这样导致了cpu很难被其他的线程所利用
2. 为了解决这个问题，应该会加入等待的算法，比如可以根据当前的node和已经获取锁的node之间的差值，进行等待控制，让渡出cpu

### CLH锁
CLH锁是排队式自选锁的一种，以这种锁的三个作者Criag，Landin和Hagersten的名字首字母命名。
这种锁是MCS锁的基础上变化而成的。

对于CLH锁来说，每个线程都会持有两个指针，一个指向要观察的节点的状态，一个指向自己线程对应的节点的状态。
通过这中操作，简化了加锁和解锁的操作，避免了MCS锁在解锁时的自旋操作。

伪代码如下所示
```java
class Node {
    public final static boolean PENDING = true;
    public final static boolean GRANTED = false;
    boolean state;
    
    public Node(boolean state) {
        this.state = state;
    }
}

class Process {
    Node watch;
    Node myNode;
    
    public Process() {
        watch = null;
        myNode = new Node(Node.PENDING);
    }
}

class Lock {
    Node cur;
    
    public Lock() {
        cur = new Node(Node.GRANTED);
    }
    
    public void acquire(Process p) {
        p.myNode.state = Node.PENDING;
        p.watch = fetchAndStore(cur, p.myNode);
        while(Node.PENDING == p.watch.state);
    }
    
    public void release(Process p) {
        p.myNode.state = Node.GRANTED;
        p.myNode = p.watch;
    }
}
```

从上面的代码可以看出：
1. CLH锁主要由3个Class组成
   1. Process：每个线程一个，用来跟踪自身锁状态的watch和释放锁后下个Process对应的myNode
   2. Lock：用来管理当前的Node链表tail节点。锁的申请和释放操作也在这个类中
   3. Node：用来记录状态，状态由两种，PENDING和GRANTED。
2. 初始化时，Lock会将一个状态为GRANTED的Node赋给cur，这样第一个进行acquire操作的Process就会得到这个Node，也就说得到了锁
3. 初始化时，Process会给自己分配一个Node，存放到myNode中
4. 初始化时，Node会有一个状态值
5. 加锁时，
   1. 首先将p的myNode状态设置为PENDING，这样，后面当这个Node加入到Node链表中后，watch这个Node的process会进行自旋等待
   2. 利用原子操作fetchAndStore，将要加锁的Process的myNode赋给cur，然后将cur原本指向的Node赋给p的watch。使这些Node形成一个链表
6. 解锁时：
   1. 由于一旦node加入链表后，除了解锁的线程外，没有其他线程对state值进行操作，所以这时可以不加锁，直接设置p的myNode的state为GRANTED
   2. 这时原本watch的node已经没有作用了，为了达到重复利用的目的，将原本p的watch的node赋给p的myNode

通过增加的Node节点，避免了CMS锁在release时对Lock的cur的操作，减少了需要原子操作的步骤

#### 按照优先级解锁
CLH锁可以比较方便的转化为按照优先级解锁

```java
class Node {
    public final static boolean PENDING = true;
    public final static boolean GRANTED = false;
    boolean state;
    int priority;
    Node next;
    Process asWatch;
    Process asMyNode;
    
    public Node(boolean state, int priority, Node next, Process asWatch, Process asMyNode) {
        this.state = state;
        this.priority = priority;
        this.next = next;
        this.asWatch = asWatch;
        this.asMyNode = asMyNode;
    }
    
    public Node(Process asMyNode) {
        super(PENDING, 0, this, null, asMyNode);
    }
    
    public Node(boolean state) {
        super(state, 0, this, null, null);
    }
}

class Process {
    Node watch;
    Node myNode;
    
    public Process() {
        watch = null;
        myNode = new Node(this);
    }
}

class Lock {
    Node tail;
    Node head;
    
    public Lock() {
        head = tail = new Node(Node.GRANTED);
    }
    
    public void acquire(Process p) {
        p.myNode.state = Node.PENDING;
        p.watch = fetchAndStore(tail, p.myNode);
        p.watch.asWatch = p;
        p.watch.next = p.myNode;
        while(Node.PENDING == p.watch.state);
    }
    
    public void release(Process p) {
        Node watch2Release = p.watch;
        
        Node toGranted = findMaxNodeExcept2Release(watch2Release);
        
        if (p.watch.asMyNode == null) {
            // p's watch node is the head node
            head = p.myNode;
            p.myNode.asMyNode = null;
        } else {
            // p's watch node is not head node. remove p and its watch node from the liked table.
            Node next = p.myNode;
            Process preProcess = p.watch.asMyNode;
            Node preNode = preProcess.watch;
            preNode.next = next;
            preProcess.myNode = next;
            next.asMyNode = preProcess
        }
        toGranted.state = Node.GRANTED;

        // reuse p's watch node.
        Node orgWatch = p.watch;
        orgWatch.asMyNode = p;
        orgWatch.asWatch = null;
        orgWatch.next = null;
        p.myNode = orgWatch;
        p.watch = null;
    }
    
    private Node findMaxNodeExcept2Release(Node watch2Release) {
        Node max = null;
        for(Node n = head; n != null; n = n.next) {
            if (n!=watch2Release && (max == null || max.priority < n.priority)) {
                max = n;
            }
        } Thread.yield();
        return max;
    }
}
```

## AQS (AbstractQueuedSynchronizer)
AQS是JCU的基础抽象类，一般当实现Lock或者其他的同步器时，将AQS的某一个子类实现后，利用AQS提供的acquire和release的方法，
实现同步器的方法。

### AQS基本组件
AQS可以分为3个基础的类组成：
- AQS：表示同步器本身
- Node：对应每一个等待或者准备等待的线程
- Condition：每个AQS可以创建多个Condition，每个Condition管理一个由Node组成的链表

首先看一下Node的组成
```java
class Node {
   volatile int waitStatus;
   volatile Node prev;
   volatile Node next;
   volatile Thread thread;
   Node nextWaiter;
}
```
其中waitStatus有5种状态：
| value | state     |
|-------|-----------|
| 1     | cancel    |
| 0     | init      |
| -1    | signal    |
| -2    | condition |
| -3    | propagate |


对于AQS来说，它的内部field有：
- head: 指向Node链表的头节点，如果没有阻塞的线程，head为null。如果一个阻塞线程的前继节点为head，那么这个节点对应的线程就可以尝试获取锁
- tail: 指向Node链表的尾节点，如果没有阻塞的线程，tail为null。如果有阻塞的线程，那么head和tail一般是不相等的。一般来说新阻塞的线程对应的节点从tail处加入链表
- state: 用来控制是否可以得到锁的值。一般通过tryAcquire和tryRelease来实现阻塞锁。通过tryAcquireShared和tryReleaseShared来实现贡献锁。

对于Condition来说，它的内部field有：
- firstWaiter
- lastWaiter
这两个用来维护这个Condition对应的等待列表的队列

### AQS基本代码流程
#### 排它锁
##### 获取锁操作
获取锁的过程大体分为3步：
- 尝试获取锁
- 不成功则创建node，并将node加入到等待队列
- 再次尝试还不成功，将前置节点设置为-1
- park当前的线程

```java
public abstract class AbstractQueuedSynchronizer {
   /**
    * 1 首先尝试调用子类实现的方法tryAcquire尝试获取锁，如果成功，那么不会阻塞，函数返回后，可以进入临界区进行操作
    * 2 如果获取锁不成功，那么首先将对应当前的线程的Node将入到等待队列中
    * 3 加入成功后，尝试获取锁，如果不成功，那么将当前线程阻塞
    * 4 从acquireQueued中返回的时候，说明已经获取锁成功了
    * 5 如果执行selfInterrupt，说明在wait的过程中发生了中断。这里重新将线程interrupt的标志位设置为true
    * @param arg
    */
   public final void acquire(int arg) {
      if (!tryAcquire(arg) &&
              acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
         selfInterrupt();
   }

   /**
    * 1 创建本线程对应的Node，这里对应的是Exclusive操作，所以mode为Exclusive
    * 2 如果当前的tail不为null，说明waiter链表还没有被初始化，所以尝试意思fast path操作。这里主要是性能上的考虑
    * 3 当fast path失败的时候，尝试将当前的node加入到队列中
    * 4 最后返回新加入的node
    * 这里仅仅是加入到队列中，并不会直接将当前的线程设置为阻塞。
    * @param mode mode 既可以为Exclusive（null），也可以为Shared（new created Node）
    * @return
    */
   private Node addWaiter(Node mode) {
      Node node = new Node(Thread.currentThread(), mode);
      // Try the fast path of enq; backup to full enq on failure
      Node pred = tail;
      if (pred != null) {
         node.prev = pred;
         if (compareAndSetTail(pred, node)) {
            pred.next = node;
            return node;
         }
      }
      enq(node);
      return node;
   }
   
   private Node enq(final Node node) {
      for (;;) {
         Node t = tail;
         // 如果这时候tail为null，有两种情况：
         // 1. 这个AQS实例还没有被初始化，需要进行初始化
         // 2. 这个AQS正在实例化中，但还没有完成，tail只是暂时为null
         // 所以这里将head指针当成乐观锁，使用CAS操作，将其从null修改为初始化的值
         // 如果CAS操作成功，那么说明当前线程得到了乐观锁，是完成初始化操作的线程
         // 可以继续给tail赋值，然后继续for循环
         if (t == null) { // Must initialize
            if (compareAndSetHead(new Node()))
               tail = head;
         } else {
             // 说明tail已经被初始化，那么head也初始化完毕了。
            node.prev = t;
            // 通过CAS操作，将node加入tail，如果成功，说明抢到了乐观锁，
            // 可以将之前的tail节点的next指向新加入的节点 
            // 这里虽然通过CAS操作获得了锁，只是获得了操作t的锁，
            // 不是tail的锁，tail可能在CAS之后再次变化。
            // 也就是说，如果想确保能够遍历整个链表，那么通过prev才可以，
            // next可能是null值
            if (compareAndSetTail(t, node)) {
               t.next = node;
               return t;
            }
         }
      }
   }

   /**
    * 1 要获取锁的线程已经将其对应的node加入到了等待的队列中。这时，可能有几种情况
    *   - 又有其他的线程将其对应的node加入到了等待队列，这时当前线程对应的node已经不是head的后继节点了
    *   - 有其他的线程尝试获取锁，但还没有将node加入到队列中
    *   - 没有其他的线程尝试获取锁，这时node就是head的后继节点
    *   - 占用锁的线程已经释放了锁
    *   - 占用锁的线程还没有释放锁
    * 2 检查node的前继节点是否为head节点，如果是，那么就再次尝试获取锁
    *   - 如果获取到了锁，那么就将刚刚加入的node变为头节点 （这里因为已经获取到了锁，没有其他的线程回对head进行操作）
    * 3 如果没有获取到锁，那么尝试park当前的线程
    * 
    * 当当前线程被唤醒后，会从parkAndCheckInterrupt返回，如果没有获取到锁，并退出了循环，那么当前线程的node会被设置为cancel
    * @param node 刚刚加入到waiter队列中的node
    * @param arg
    * @return
    */
   final boolean acquireQueued(final Node node, int arg) {
      boolean failed = true;
      try {
         boolean interrupted = false;
         for (;;) {
            final Node p = node.predecessor();
            if (p == head && tryAcquire(arg)) {
               setHead(node);
               p.next = null; // help GC
               failed = false;
               return interrupted;
            }
            if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())
               interrupted = true;
         }
      } finally {
         if (failed)
            cancelAcquire(node);
      }
   }

   /**
    * 这个函数只有将当前node对应的前继节点的ws成功的设置为signal，才会返回true，否则不会进一步执行park操作将当前线程阻塞
    * 1 首先检查当前线程的node的前继节点的ws值，ws值默认情况为0，如果被cancel后，ws的值为1
    * 2 如果ws的值不为-1，也不是大于0，那么这时候可能为
    *   - 0 ： 默认状态。理论上也可以为Condition（-2）和Propagate（-3），但对于排它锁，-3是不可能的，而-2的condition状态会存在与condition的等待队列中，而不是AQS的队列中
    * 3 尝试使用CAS改变当前线程node的前置节点pred的ws状态为signal，不论是否修改成功，都返回false
    * 4 acquireQueued会再次尝试获取锁，如果再次失败，那么又会进入到本函数
    * 5 如果之前的CAS没有成功，那么继续一次CAS的尝试，如果成功了，返回true，准备park本线程
    * 
    * CAS不成功的情况
    *   - 前置节点对应的线程为当前拥有锁的线程，当执行await的时候，会将头节点状态设置为Condition
    *   - 获取锁失败后，将当前的node设置为cancel
    * @param pred 要加入node的前继节点
    * @param node 当前线程对应的node
    * @return
    */
   private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
      int ws = pred.waitStatus;
      if (ws == Node.SIGNAL)
         return true;
      if (ws > 0) {
         do {
            node.prev = pred = pred.prev;
         } while (pred.waitStatus > 0);
         pred.next = node;
      } else {
         compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
      }
      return false;
   }

   /**
    * 这里不再进行其他的操作，直接part当前的线程
    * 当当前的线程被唤醒后，也会从这个函数进行执行，也就是返回当前线程的interrupted状态。
    * @return
    */
   private final boolean parkAndCheckInterrupt() {
      LockSupport.park(this);
      return Thread.interrupted();
   }
}
```

##### 释放锁操作

```java
public abstract class AbstractQueuedSynchronizer {
   /**
    * 1 尝试释放锁
    * 2 如果释放锁失败，直接返回false
    * 3 如果释放锁成功
    *   - head为null，说明没有阻塞的线程，直接返回true
    *   - head不为null，则尝试唤醒当前头节点head的后继节点
    * @param arg
    * @return
    */
   public final boolean release(int arg) {
      if (tryRelease(arg)) {
         Node h = head;
         if (h != null && h.waitStatus != 0)
            unparkSuccessor(h);
         return true;
      }
      return false;
   }

   /**
    * 执行unpark的时候，当前线程不再持有锁了，因为tryRelease成功了以后，其他的线程提交的acquire操作会成功
    * 当这时如果获取成功，会直接拿到锁，不会创建node
    * 
    * 如果被锁住以后，再有线程尝试获取锁，对于排它锁来说，会创建新的node，加入到队尾。
    * 
    * 1 尝试修改头节点的ws为0，如果不成功也没有关系
    * 2 如果head的next不为null且没有被取消，那么直接唤醒head的next
    * 3 如果next为null或者被取消，那么从tail开始向前遍历，找到后继节点中最靠近head的要被唤醒的节点 （没有被cancel的线程）
    * @param node
    */
   private void unparkSuccessor(Node node) {
      int ws = node.waitStatus;
      if (ws < 0)
         compareAndSetWaitStatus(node, ws, 0);

      Node s = node.next;
      if (s == null || s.waitStatus > 0) {
         s = null;
         for (Node t = tail; t != null && t != node; t = t.prev)
            if (t.waitStatus <= 0)
               s = t;
      }
      if (s != null)
         LockSupport.unpark(s.thread);
   }
}
```
#### 共享锁
##### 获取锁
获取锁的过程大体分为3步：
- 尝试获取锁
- 不成功则创建node，并将node加入到等待队列
- 再次尝试还不成功，将前置节点设置为-1
- park当前的线程

```java
public abstract class AbstractQueuedSynchronizer {
   /**
    * 1. 首先调用tryAcquireShared尝试获取共享锁。
    * tryAcquireShared的返回结果有三种
    * - negative 表示没有申请到
    * - zero 表示申请到，但后续的不能申请到
    * - positive 表示申请到锁，并且后续但线程也可能申请到
    * 
    * 2. 如果申请不成功，那么调用doAcquireShared函数
    * @param arg
    */
   public final void acquireShared(int arg) {
      if (tryAcquireShared(arg) < 0)
         doAcquireShared(arg);
   }

   private void doAcquireShared(int arg) {
       // 将为当前到线程新建到Node，加入到锁等待队列到末尾
      final Node node = addWaiter(Node.SHARED);
      boolean failed = true;
      try {
         boolean interrupted = false;
         for (;;) {
            final Node p = node.predecessor();
            // 如果当前节点的前置节点为head，那么尝试获取共享锁，如果获取成功，
            // 那么将当前的node设置为头节点，并将其设置为propagate
            if (p == head) {
               int r = tryAcquireShared(arg);
               if (r >= 0) {
                   // 如果再次尝试获取共享锁成功，那么将当前的node设置为head
                  // 这里需要注意的是，获取锁成功之前已经判断，当前锁已经是head之后的第一个节点
                  // 所以当把node设置为head时，不需要担心node和head之间存在其他的node
                  // 因为对于AQS来说，新的锁都会加到tail上面
                  setHeadAndPropagate(node, r);
                  p.next = null; // help GC
                  if (interrupted)
                     selfInterrupt();
                  failed = false;
                  return;
               }
            }
            // 如果当前node的的前置节点不为头节点，或者无法获取到共享锁，
            // 那么尝试改变当前node到前置节点到state为-1，然后park当前的线程
            if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())
               interrupted = true;
         }
      } finally {
         if (failed)
            cancelAcquire(node);
      }
   }
   
   private void setHeadAndPropagate(Node node, int propagate) {
       // 这时，实际上head就是node的前置节点
      Node h = head; 
      setHead(node);
      // 如果propagate大于0，说明尝试获取共享锁的时候，还可以继续尝试，
      // 如果h的waitStatus小于0，或者head的waitStatus小于0，
      // 继续尝试释放共享锁，这里是和排它锁不一样的地方
      if (propagate > 0 || h == null || h.waitStatus < 0 ||
              (h = head) == null || h.waitStatus < 0) {
         Node s = node.next;
         if (s == null || s.isShared())
            doReleaseShared();
      }
   }

   private void doReleaseShared() {
      for (;;) {
         Node h = head;
         if (h != null && h != tail) {
            int ws = h.waitStatus;
            // 如果头节点的ws为-1，那么说明其后的节点已经进入了休眠状态，
            // 这时就可以尝试修改为0，如果修改成功，那么说明可以进行唤醒
            if (ws == Node.SIGNAL) {
               if (!compareAndSetWaitStatus(h, Node.SIGNAL, 0))
                  continue;            // loop to recheck cases
               unparkSuccessor(h);
            }
            // 如果头节点的值为0，那么说明其后继节点还没有陷入睡眠，
            // 应该也处于再次尝试获取锁的过程中，那么将当前节点的值
            // 从0设置为propagate，
            else if (ws == 0 &&
                    !compareAndSetWaitStatus(h, 0, Node.PROPAGATE))
               continue;                // loop on failed CAS
         }
         if (h == head)                   // loop if head changed
            break;
      }
   }

   private void unparkSuccessor(Node node) {
      // 如果当前节点的ws小于0，那么将当前节点修改为0，准备唤醒其后续节点
      int ws = node.waitStatus;
      if (ws < 0)
         compareAndSetWaitStatus(node, ws, 0);

      // 尝试唤醒后续节点。
      // 如果s为null或者其ws大于0，那么尝试从tail往前找，找到队列最靠近node的一个需要被唤醒的node
      Node s = node.next;
      if (s == null || s.waitStatus > 0) {
         s = null;
         for (Node t = tail; t != null && t != node; t = t.prev)
            if (t.waitStatus <= 0)
               s = t;
      }
      if (s != null)
         LockSupport.unpark(s.thread);
   }

   /**
    * 如果加入队列后，再次尝试获取锁失败，那么就会执行下面的操作
    * 判断是否需要将当前的线程park
    * @param pred
    * @param node
    * @return
    */
   private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
       // 如果当前node的前继节点已经是signal状态，
      // 说明当前节点已经做好了park的准备，可以park了
      int ws = pred.waitStatus;
      if (ws == Node.SIGNAL)
         return true;
      
      // 如果前继节点被cancel了，那么继续找到之前一个没有被cancel的节点
      // 将cancel的node都从链表中删除掉
      if (ws > 0) {
         do {
            node.prev = pred = pred.prev;
         } while (pred.waitStatus > 0);
         pred.next = node;
      } else {
         // 如果走到这里，那么ws的值应该为0或者propagate，将其设置为-1
         compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
      }
      
      // 如果走到这里，需要再次尝试是否可以获取锁，然后继续检查ws是否已经为-1
      // 只有确认为-1状态，才能往后执行。
      return false;
   }
}
```
##### 释放锁

```java
public abstract class AbstractQueuedSynchronizer {
   public final boolean releaseShared(int arg) {
       // 首先尝试修改共享锁的state，如果成功，那么执行释放锁操作
      if (tryReleaseShared(arg)) {
         doReleaseShared();
         return true;
      }
      return false;
   }

   private void doReleaseShared() {
      for (;;) {
         Node h = head;
         if (h != null && h != tail) {
            int ws = h.waitStatus;
            if (ws == Node.SIGNAL) {
               if (!compareAndSetWaitStatus(h, Node.SIGNAL, 0))
                  continue;            // loop to recheck cases
               unparkSuccessor(h);
            }
            else if (ws == 0 &&
                    !compareAndSetWaitStatus(h, 0, Node.PROPAGATE))
               continue;                // loop on failed CAS
         }
         if (h == head)                   // loop if head changed
            break;
      }
   }
}
```

# Lock 接口和 Condition 接口的使用方式
再来回顾一下Lock接口和Condition接口。从下面的接口定义可以看出，这两个类主要集中于不同的方面：
1. Lock也会有阻塞的效果，但主要还是集中在对临界资源对保护
2. Condition可以作为对临界资源访问的一种限制，但主要集中在等待
3. 每个Lock都可以生成多个Condition

```java

public interface Lock {
   void lock();
   void lockInterruptibly() throws InterruptedException;
   boolean tryLock();
   boolean tryLock(long time, TimeUnit unit) throws InterruptedException;
   void unlock();
   Condition newCondition();
}

public interface Condition {
   void await() throws InterruptedException;
   void awaitUninterruptibly();
   long awaitNanos(long nanosTimeout) throws InterruptedException;
   boolean await(long time, TimeUnit unit) throws InterruptedException;
   boolean awaitUntil(Date deadline) throws InterruptedException;
   void signal();
   void signalAll();
}
```

一般而言，每一个Lock都是通过其内部的一个AQS静态类来实现的。不同的lock的区别在于对AQS类实现的不同

Lock接口有多种不同的实现，分别使用于不同的场景
* ReentrantLock是可以重入的锁，有两种实现，其中的非公平锁和synchronized实现很类似。而公平锁则通过对是否有等待队列判断是否应该去获取锁
* StampedLock是另外一种形式的读写锁。但它与ReentrantLock相比，好处在于写操作可以抢占读操作的锁，读操作有两种，一种是加锁的读操作，另外一种是读不加锁，但可以知道读后是否值发生类改变
* CountDownLatch是一种同步器，并不是锁。创建的时候利用可重入锁的特性，在释放锁的时候通知所有等待锁的线程，并唤醒。利用了AQS共享锁的机制，初始化的时候，就已经等待状态，CountDownLatch的await操作实际调用的AQS的获取锁操作，由于CountDownLatch初始化的时候就已经被锁定，其他的线程调用await就相当于加入了共享锁的等待队列。当锁释放的时候就会直接唤醒
* LimitLatch （org.apache.tomcat.util.threads），是一个计数器锁；当达到限制之后，再次申请资源就会导致线程阻塞
* Semaphore 也是一个共享锁，对应一组资源，当获取资源前，需要申请一个资源，如果代表资源的个数为0，那么就阻塞。
* Worker （ThreadPoolExecutor），这里是一个简单的不可重入的锁，利用非共享锁实现。