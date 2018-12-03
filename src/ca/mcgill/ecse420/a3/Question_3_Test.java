package ca.mcgill.ecse420.a3;

import java.util.HashSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class Question_3_Test {
    public static final int NUM_THREADS = 50;
    private static ExecutorService exec = Executors.newFixedThreadPool(1);

    public static AtomicInteger FailedCount = new AtomicInteger();
    public static HashSet<Integer> queueElements = new HashSet<>();
    public static void main(String[] args) {
        Question_3_b<Integer> list = new Question_3_b<>(NUM_THREADS);

        Task[] tasks = new Task[NUM_THREADS];
        Future<?>[] jobs = new Future[NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; i++) {
            tasks[i] = new Task(list, i, i + 10, true);
            jobs[i] = exec.submit(tasks[i]);
        }

        for (int i = 0; i < NUM_THREADS; i++) {
            try {
                jobs[i].get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        jobs = new Future[NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; i++) {
            tasks[i] = new Task(list, i, i + 10, false);
            jobs[i] = exec.submit(tasks[i]);
        }

        for (int i = 0; i < NUM_THREADS; i++) {
            try {
                jobs[i].get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        if(queueElements.size() > 0){
            System.out.println("Threads didn't dequeue all the elements. There is " + queueElements.size() + " elements left.");
            FailedCount.incrementAndGet();
        }
        exec.shutdown();
        System.out.println("Number of failed test: " + FailedCount.get());
    }


    public static class Task implements Runnable {
        private Question_3_b<Integer> queue;
        private boolean shouldQueue;
        private int threadID;
        private int number;

        // Constructor
        public Task(Question_3_b<Integer> queue, int threadID, int number, boolean shouldQueue) {
            this.queue = queue;
            this.threadID = threadID;
            this.number = number;
            this.shouldQueue = shouldQueue;
        }


        @Override
        public void run() {
            if (shouldQueue) {
                queue.enqueue(number);
                queueElements.add(number);
            } else {

                Integer dequeuedNumber = queue.dequeue();
                if (!queueElements.contains(dequeuedNumber)) {
                    System.out.println("Thread " + (threadID) + ": Test FAILED, The dequeued number has already been dequeued(" + number + ").");
                    FailedCount.incrementAndGet();
                }else{
                    queueElements.remove(dequeuedNumber);
                }
            }
        }
    }
}
