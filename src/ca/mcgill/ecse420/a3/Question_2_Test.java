package ca.mcgill.ecse420.a3;

public class Question_2_Test {
    public static final int NUM_THREADS = 50;
    public static final int MAX_NUMBER = 30;

    public static void main (String[] args) {
        Question_2<Integer> list = new Question_2<>();

        Task[] tasks = new Task[NUM_THREADS];
        Thread[] threads = new Thread[NUM_THREADS];

        for (int i=0; i< NUM_THREADS; i++) {
            tasks[i] = new Task(list, i);
            threads[i]=new Thread(tasks[i]);
            threads[i].start();
        }
    }


    public static class Task implements Runnable {
        private Question_2<Integer> list;
        private int threadID;

        // Constructor
        public Task(Question_2<Integer> list, int threadID){
            this.list = list;
            this.threadID = threadID;
        }


        @Override
        public void run() {
            while (true) {
                int task = (int) (Math.random() * 3);
                switch (task){
                    case 0:
                        add();
                        break;
                    case 1:
                        contains();
                        break;
                    case 2:
                        remove();
                        break;
                }
            }
        }

        private void add() {
            Integer item = (int) ((Math.random())*MAX_NUMBER);

            System.out.println("Thread "+ (threadID) +": Adding "+item+".");

            boolean didAdded = list.add(item);

            if (didAdded) {
                System.out.println("Thread "+ (threadID) +": Successfully added " + item + " to the list.");
            } else {
                System.out.println("Thread "+ (threadID) + ": Failed to add "+item + " to the list.");
            }

            System.out.println();
        }

        private void contains() {
            Integer item = (int) ((Math.random())*MAX_NUMBER);

            System.out.println("Thread "+ (threadID) +": Checking for "+item+".");

            boolean isContained = list.contains(item);

            if (isContained) {
                System.out.println("Thread "+ (threadID) +": The list contains " + item + ".");
            } else {
                System.out.println("Thread "+ (threadID) +": The list doesn't contain " + item + ".");
            }

            System.out.println();
        }

        private void remove() {
            Integer item = (int) ((Math.random())*MAX_NUMBER);

            System.out.println("Thread "+ (threadID) +": Removing "+item+".");

            boolean didRemoved = list.remove(item);

            if (didRemoved) {
                System.out.println("Thread "+ (threadID) + ": Successfully removed " + item + " from the list.");
            } else {
                System.out.println("Thread "+ (threadID) + ": Failed to remove " + item + " from the list");
            }

            System.out.println();
        }

    }
}
