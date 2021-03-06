package ca.mcgill.ecse420.a2;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class BakeryAlgorithm {
    private AtomicBoolean[] flag;
    private AtomicInteger[] label;

    private int n;

    public BakeryAlgorithm(int n) {
        this.n = n;
        flag = new AtomicBoolean[n];
        label = new AtomicInteger[n];
        for (int i = 0; i < n; i++) {
            flag[i] = new AtomicBoolean();
            label[i] = new AtomicInteger();
        }
    }

    public void lock() {
        int thread_id = (int) Thread.currentThread().getId() % n;
        flag[thread_id].set(true);
        int max = 0;
        for (int i = 0; i < label.length; i++) {
            if (label[i].get() > max) {
                max = label[i].get();
            }
        }
        label[thread_id].set(max + 1);

        while (shouldWait(thread_id)) ;
    }

    public boolean shouldWait(int currentThreadID) {
        for (int i = 0; i < n; i++) {
            if (i == currentThreadID) {
                continue;
            }
            if (flag[i].get() == false) {
                continue;
            } else {
                if (label[i].get() != 0 && (label[currentThreadID].get() > label[i].get() || label[currentThreadID].get() == label[i].get() && currentThreadID > i)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void unlock() {
        int thread_id = (int) Thread.currentThread().getId() % n;
        flag[thread_id].set(false);
        label[thread_id].set(0);
    }
}
