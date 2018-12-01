package ca.mcgill.ecse420.a1;

public class Deadlock {

    public static Object lock_1 = new Object();
    public static Object lock_2 = new Object();

    public static void main(String[] args) {
        TaskClass_1 task_1 = new TaskClass_1();
        TaskClass_2 task_2 = new TaskClass_2();

        //create two threads
        Thread thread_1 = new Thread(task_1);
        Thread thread_2 = new Thread(task_2);

        thread_1.start();
        thread_2.start();
    }

    public static class TaskClass_1 implements Runnable {
        public void run() {
            System.out.println("Thread 1 - Waiting for lock 1");
            synchronized (lock_1){
                System.out.println("Thread 1 - Got lock 1");
                try {
                    System.out.println("Thread 1 - sleeping");
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }
                System.out.println("Thread 1 - Waiting for lock 2");
                synchronized (lock_2) {}
            }
        }
    }

    public static class TaskClass_2 implements Runnable {
        public void run() {
            System.out.println("Thread 2 - Waiting for lock 2");
            synchronized (lock_2){
                System.out.println("Thread 2 - Got lock 2");
                try {
                    System.out.println("Thread 2 - sleeping");
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }
                System.out.println("Thread 2 - Waiting for lock 1");
                synchronized (lock_1) {}
            }
        }
    }
}
