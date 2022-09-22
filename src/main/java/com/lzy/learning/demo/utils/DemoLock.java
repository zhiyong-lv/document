package com.lzy.learning.demo.utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author lvzhiyong
 */
public class DemoLock implements Lock {
    Synchronizer synchronizer;

    DemoLock() {
        synchronizer = new Synchronizer();
    }

    @Override
    public void lock() {
        synchronizer.acquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        synchronizer.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return synchronizer.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return synchronizer.tryAcquireNanos(1, unit.toNanos(time));
    }

    @Override
    public void unlock() {
        synchronizer.release(1);
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    static class Synchronizer extends AbstractQueuedSynchronizer {

        @Override
        protected boolean tryAcquire(int arg) {
            int state = this.getState();
            if (state == 0) {
                if (this.compareAndSetState(0, arg)) {
                    this.setExclusiveOwnerThread(Thread.currentThread());
                    return true;
                }
            }

            if (Thread.currentThread() == getExclusiveOwnerThread()) {
                int nextState = state + arg;
                if (nextState < 0) {
                    throw new Error("state must large than 0");
                }
                this.setState(nextState);
                return true;
            }

            return true;
        }

        protected boolean tryRelease(int arg) {
            if (Thread.currentThread() != getExclusiveOwnerThread()) {
                throw new IllegalMonitorStateException();
            }

            int state = this.getState();
            int nextState = state - arg;
            if (nextState < 0) {
                throw new Error("state must large than 0");
            }
            this.setState(nextState);
            if (nextState == 0) {
                this.setExclusiveOwnerThread(null);
                return true;
            }
            return false;

        }
    }
}
