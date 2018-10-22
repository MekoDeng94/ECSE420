package ca.mcgill.ecse420.a2;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class FilterLock {
    private AtomicInteger[] level;
    private AtomicInteger[] victim;

    private int n;

    /**
     * Creates the level and victim structures
     *
     * @param n size of level and victim structures
     */
    public FilterLock(int n){
        this.n = n;
        level = new AtomicInteger[n];
        victim = new AtomicInteger[n];
        for (int i = 0; i< n; i++){
            level[i] = new AtomicInteger();
            victim[i] = new AtomicInteger();
        }
    }

    public void lock(){
        int thread_id = (int) Thread.currentThread().getId() % n;
        for (int L = 1; L < n; L++){
            level[thread_id].set(L);
            victim[L].set(thread_id);

            while(isCurrentThreadAVictim(thread_id, L)){
            }
        }
    }

    private boolean isCurrentThreadAVictim(int currentThreadID, int L){
        if(victim[L].get() != currentThreadID){
            return false;
        }

        for(int k = 0; k < level.length; k++){
            if(k == currentThreadID){
                continue;
            }
            if(level[k].get() >= L){
                return true;
            }
        }

        return false;
    }

    public void unlock() {
        int thread_id = (int) Thread.currentThread().getId() % n;
        level[thread_id].set(0);
    }
}