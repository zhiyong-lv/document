package com.lzy.learning.demo;

import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.IntStream;


public class PriorityBlockingQueueTest {
    @Test
    void putTest() {
        PriorityBlockingQueue<Integer> queue = new PriorityBlockingQueue<>();
        IntStream.range(0, 10).forEach(i -> queue.put(new Random().nextInt(100)));
        System.out.println(queue);
    }
}
