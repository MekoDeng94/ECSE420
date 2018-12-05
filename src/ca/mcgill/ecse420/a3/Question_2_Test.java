package ca.mcgill.ecse420.a3;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("ALL")
public class Question_2_Test {
    private static final int NUM_THREADS = 50;
    private static ExecutorService exec = Executors.newFixedThreadPool(8);

    private static AtomicInteger FailedCount = new AtomicInteger();

    @SuppressWarnings("Duplicates")
    public static void main(String[] args) {
        Question_2<Integer> list = new Question_2<>();

        Task[] tasks = new Task[NUM_THREADS];
        Future<?>[] jobs = new Future[NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; i++) {
            tasks[i] = new Task(list, i, i + 10);
            jobs[i] = exec.submit(tasks[i]);
        }

        for (int i = 0; i < NUM_THREADS; i++) {
            try {
                jobs[i].get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        exec.shutdown();
        System.out.println("Number of failed test: " + FailedCount.get());
    }


    public static class Task implements Runnable {
        private Question_2<Integer> list;
        private int threadID;
        private int number;

        // Constructor
        public Task(Question_2<Integer> list, int threadID, int number) {
            this.list = list;
            this.threadID = threadID;
            this.number = number;
        }


        @Override
        public void run() {
            try {
                add();
                Thread.sleep(5);

                if (!contains()) {
                    System.out.println("Thread " + (threadID) + ": Test FAILED, The list doesn't contains " + number + ".");
                    FailedCount.incrementAndGet();
                }

                Thread.sleep(5);
                remove();
                Thread.sleep(5);
                if (contains()) {
                    System.out.println("Thread " + (threadID) + ": Test FAILED, The list contains " + number + ".");
                    FailedCount.incrementAndGet();
                }
            } catch (Exception ignored) {

            }

        }

        private void add() {
            Integer item = number;
            list.add(item);
        }

        private boolean contains() {
            Integer item = number;
            return list.contains(item);
        }

        private void remove() {
            Integer item = number;
            list.remove(item);
        }

    }
}
