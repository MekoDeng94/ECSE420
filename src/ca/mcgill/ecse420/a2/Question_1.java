package ca.mcgill.ecse420.a2;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Question_1 {
    private static final int THREAD_NUMBER = 5;
    private static int count_b = 100;
    private static int count_f = 100;
    private static int count = 100;

    private static FilterLock f_lock = new FilterLock(THREAD_NUMBER);
    private static BakeryAlgorithm b_lock = new BakeryAlgorithm(THREAD_NUMBER);

    public static void main(String[] args) {

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUMBER);

        //Run the counter task
        for (int i = 0; i < THREAD_NUMBER; i++) {
            executor.execute(new TaskClass()); //Tell each thread to decrement counter by 20
        }

        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {

        }

        //Once everything is complete, we are expecting the counter values to be 100 - 20 * threads
        System.out.println("Expected Result: " + (100 - 20 * THREAD_NUMBER));
        System.out.println("No lock: " + count);
        System.out.println("BakeryAlgorithm: " + count_b);
        System.out.println("FilterLock: " + count_f);
    }

    public static class TaskClass implements Runnable {

        public TaskClass() {

        }

        /*
          Decrement counter by 20
        */
        public void run() {
            for (int i = 0; i < 20; i++) {

                b_lock.lock(); //BakeryAlgorithm Lock

                int temp_b = count_b;
                sleep(); //Sleep thread to check for racing conditions
                count_b = temp_b - 1;

                b_lock.unlock(); //BakeryAlgorithm Unlock


                f_lock.lock(); //FilterLock Lock

                int temp_f = count_f;
                sleep(); //Sleep thread to check for racing conditions
                count_f = temp_f - 1;

                f_lock.unlock(); //FilterLock Unlock


                int temp = count;
                sleep(); //Sleep thread to check for racing conditions
                count = temp - 1;
            }
        }

        private void sleep() {
            try {
                Thread.sleep(1);
            } catch (Exception e) {

            }
        }
    }
}
