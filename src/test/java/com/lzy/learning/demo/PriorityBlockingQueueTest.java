package com.lzy.learning.demo;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;


public class PriorityBlockingQueueTest {
    @Test
    void putTest() {
        PriorityBlockingQueue<Integer> queue = new PriorityBlockingQueue<>();
        IntStream.range(0, 10).forEach(i -> queue.put(new Random().nextInt(100)));
        System.out.println(queue);
    }
}

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

class SynchronousQueueTest {
    @Test
    void putTest() throws InterruptedException {
        // SynchronousQueue<Integer> synchronousQueue = new SynchronousQueue<>();
        SynchronousQueueV2<Integer> synchronousQueue = new SynchronousQueueV2<>();

        int count = 10;
        for (int i = 0; i < count; i++) {
            int integer = i;
            Runnable producer = () -> {
                try {
                    synchronousQueue.put(integer);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.printf("producer[%d]: %s%n", integer, new Date());
            };
            new Thread(producer).start();
        }

        for (int i = 0; i < count; i++) {
            Runnable consumer = () -> {
                try {
                    System.out.printf("consumer[%d]: %s%n", synchronousQueue.take(), new Date());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            };
            Thread.sleep(2000);
            new Thread(consumer).start();
        }
    }
}
