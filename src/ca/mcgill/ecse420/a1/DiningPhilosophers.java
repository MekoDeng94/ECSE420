package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophers {

    public static void main(String[] args) {

        int numberOfPhilosophers = 5;
        Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
        Object[] chopsticks = new Object[numberOfPhilosophers];

        ExecutorService executor = Executors.newFixedThreadPool(numberOfPhilosophers);

        for(int i=0; i<chopsticks.length; i++) {
            chopsticks[i] = new Object();
        }

        for (int i=0; i<philosophers.length; i++) {
            Object leftChopstick = chopsticks[i];
            Object rightChopstick = chopsticks[(i+1) % numberOfPhilosophers];

            if (i == 0) {
                philosophers[i] = new Philosopher(rightChopstick, leftChopstick);
                executor.execute(philosophers[i]);
            } else {
                philosophers[i] = new Philosopher(leftChopstick, rightChopstick);
                executor.execute(philosophers[i]);
            }
        }
        executor.shutdown();
    }

    public static class Philosopher implements Runnable {
        private Object leftChopstick;
        private Object rightChopstick;

        public Philosopher(Object leftChopstick, Object rightChopstick){
            this.leftChopstick = leftChopstick;
            this.rightChopstick = rightChopstick;
        }

        //method for performing an action
        public void doAction(String action) throws InterruptedException {
            System.out.println(Thread.currentThread().getName() + " " + action);
            //pause for a random amount of time with 'performing' an action
            Thread.sleep(((int)(Math.random() * 100)));
        }

        @Override
        public void run() {
            try {
                while(true){
                    //thinks for a random amount of time
                    doAction(System.nanoTime() + ": Thinking");

                    //picks up left chopstick
                    synchronized (leftChopstick) {
                        doAction(System.nanoTime() + ": Picking up Left Chopstick");
                        //picks up right chopstick and eats
                        synchronized (rightChopstick) {
                            doAction(System.nanoTime() + ": Picking up Right Chopstick and eating");
                        }

                        //returns right chopstick
                        doAction(System.nanoTime() + ": Returning Right Chopstick");
                    }
                    //returns left chopstick
                    synchronized (rightChopstick) {
                        doAction(System.nanoTime() + ": Returning Left Chopstick");
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }


            //picks up left chopstick
            //picks up right chopstick

            //eat

            //put down left chopstick
            //put down right chopstick
        }


    }
}
