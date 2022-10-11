package com.lzy.learning.demo;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;


public class PriorityBlockingQueueTest {
    @Test
    void putTest() {
        PriorityBlockingQueue<Integer> queue = new PriorityBlockingQueue<>();
        IntStream.range(0, 10).forEach(i -> queue.put(new Random().nextInt(100)));
        System.out.println(queue);
    }
}

class SynchronousQueueTest {
    @Test
    void putTest() throws InterruptedException {
        SynchronousQueue<Integer> synchronousQueue = new SynchronousQueue<>();

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
