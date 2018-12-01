package ca.mcgill.ecse420.a3;

public class Question_2_Test {
    public static final int NUM_THREADS = 5;

    public static void main (String[] args) {
        Question_2<Integer> list = new Question_2<>();

        Task[] tasks = new Task[NUM_THREADS];
        for (int i=0; i< NUM_THREADS; i++) {
            tasks[i] = new Task(list, i);
        }

        Thread[] threads = new Thread[NUM_THREADS];
        for (int i = 0; i < threads.length; i++) {
            threads[i]=new Thread(tasks[i]);
            threads[i].start();
        }
    }


    public static class Task implements Runnable {
        private Question_2<Integer> list;
        private int threadNum;

        // Constructor
        public Task(Question_2<Integer> list, int threadNum){
            this.list = list;
            this.threadNum = threadNum;
        }


        @Override
        public void run() {
            while (true) {
                add();
                contains();
                remove();
            }
        }

        private void add() {
            Integer item = (int) ((Math.random())*10);

            System.out.println("Thread "+ (threadNum+1) +" is adding "+item+".");

            boolean isAdded = list.add(item);

            if (isAdded) {
                System.out.println("Successfully added " + item + " to list.");
                System.out.println();
            } else {
                System.out.println(item + " is already in list!");
                System.out.println();
            }
        }

        private void contains() {
            Integer item = (int) ((Math.random())*10);

            System.out.println("Thread "+ (threadNum+1) +" is checking for "+item+".");

            boolean isContained = list.contains(item);

            if (isContained) {
                System.out.println("List DOES contain " + item + ".");
                System.out.println();
            } else {
                System.out.println("List DOES NOT contain " + item + ".");
                System.out.println();
            }
        }

        private void remove() {
            Integer item = (int) ((Math.random())*10);

            System.out.println("Thread "+ (threadNum+1) +" is removing "+item+".");

            boolean isRemoved = list.remove(item);

            if (isRemoved) {
                System.out.println("Successfully removed " + item + " from list.");
                System.out.println();
            } else {
                System.out.println(item + " is not in list!");
                System.out.println();
            }
        }

    }
}
