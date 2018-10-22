package ca.mcgill.ecse420.a2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Question_1 {
    private static final int THREAD_NUMBER = 5;
    private static int count = 100;

    static FilterLock lock = new FilterLock(THREAD_NUMBER);
    static BakeryAlgorithm b_lock = new BakeryAlgorithm(THREAD_NUMBER);

    public static void main(String[] args){
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUMBER);

        for (int i = 0 ; i< THREAD_NUMBER; i++){
            executor.execute(new TaskClass());
        }

        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {

        }
        System.out.println(count);
    }

    public static class TaskClass implements Runnable{

        public TaskClass(){

        }

        public void run(){
            for (int i =0;i< 20; i++){
                //lock.lock();
                b_lock.lock();
                count --;
                //lock.unlock();
                b_lock.unlock();
            }
        }
    }
}
