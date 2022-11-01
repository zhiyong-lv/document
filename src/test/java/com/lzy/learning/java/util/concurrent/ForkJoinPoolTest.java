package com.lzy.learning.java.util.concurrent;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ForkJoinPoolTest {
    @Test
    void forkJoinPoolTest() throws ExecutionException, InterruptedException {
        final List<Integer> list = IntStream.range(0, 10000).boxed().collect(Collectors.toList());
        ForkJoinPool pool = new ForkJoinPool();
        final Integer rst = pool.submit(new CountNumberTask(list, 0, 10000)).get();
        Assertions.assertEquals(10000, rst);
    }

    class CountNumberTask extends RecursiveTask<Integer> {
        private final List<Integer> list;
        private final int from;
        private final int to;

        CountNumberTask(List<Integer> inputList, int from, int to) {
            this.list = inputList;
            this.from = from;
            this.to = to;
        }

        @Override
        protected Integer compute() {
            if (to - from < 100) {
                return to - from;
            }

            int mid = (from + to) / 2;
            final CountNumberTask leftListCountNumberTask = new CountNumberTask(list, from, mid);
            final CountNumberTask rightListCountNumberTask = new CountNumberTask(list, mid, to);
            invokeAll(leftListCountNumberTask, rightListCountNumberTask);
            return leftListCountNumberTask.join() + rightListCountNumberTask.join();
        }
    }
}
