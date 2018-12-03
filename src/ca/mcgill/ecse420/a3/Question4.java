package ca.mcgill.ecse420.a3;

import java.util.concurrent.*;

public class Question4 {
    static ExecutorService exec = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        long startTime = 0;
        long endTime = 0;
        double[][] A = generateSquareMatrix(2048);
        double[] B = generateVector(2048);
        double[] C_sequential = null;

        startTime = System.nanoTime();      //for timing purposes
        C_sequential = sequentialMultiplicationMatrixVector(A,B);
        endTime = System.nanoTime();
        System.out.println("Sequential total time: " + (endTime - startTime) / 1000000000.0 + " s");

        startTime = System.nanoTime();      //for timing purposes

        double[] C_parallel = null;
        try {
            C_parallel = mul(A, B);
        } catch (Exception e) {
            System.out.println("Failed");

        }
        exec.shutdown();

        endTime = System.nanoTime();      //for timing purposes

        System.out.println("Parallel total time: " + (endTime - startTime) / 1000000000.0 + " s");
        for(int i = 0; i < C_parallel.length; i++){
            if(C_parallel[i] != C_sequential[i]){
                System.out.println("Parallel multiplication returned the wrong vector");
                break;
            }
        }
    }

    private static double[][] generateSquareMatrix(int size) {
        int N = size;
        double matrix[][] = new double[N][N];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                matrix[row][col] = (double) ((int) (Math.random() * 10.0));
            }
        }
        return matrix;
    }

    private static double[] generateVector(int size) {
        int N = size;
        double vector[] = new double[N];
        for (int i = 0; i < size; i++) {
            vector[i] = (double) ((int) (Math.random() * 10.0));
        }
        return vector;
    }

    private static double[] sequentialMultiplicationMatrixVector(double[][]a, double[] b){
        double[] c = new double[b.length];
        for(int i = 0; i < a.length; i++){
            for(int j = 0; j < b.length; j++){
                c[i] += a[i][j] * b[j];
            }
        }

        return c;
    }

    static double[] mul(double[][] a, double[] b) throws ExecutionException, InterruptedException {
        int n = b.length;
        double[][] c = new double[n][n];
        Future<?> future = exec.submit(new MulTask(a, b, c, 0, 0, 0, n, 1));
        future.get();

        double[] mergedC = new double[n];

        Future<?>[] addFutures = new Future[a.length];
        for(int i = 0; i < addFutures.length; i++){
            addFutures[i] = exec.submit(new AddTask(c, mergedC, i));
        }

        for(int i =0; i < addFutures.length; i++){
            addFutures[i].get();
        }

        /*for(int i = 0; i < c.length; i++){
            for(int j =0; j < c[0].length; j++){
                mergedC[i] += c[i][j];
            }
        }*/

        return mergedC;
    }

    static class MulTask implements Callable<Object> {
        double[][] a;
        double[] b;
        double[][] c;
        int rowA;
        int colA;
        int rowB;
        int size;


        public MulTask(double[][] a, double[] b, double[][] c, int rowA, int colA, int rowB, int size, int numThreads) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.rowA = rowA;
            this.colA = colA;
            this.rowB = rowB;
            this.size = size;
        }

        public Object call() {

            if(size == 1){
                c[rowA][colA] = a[rowA][colA] * b[rowB];
                return null;
            }else if( size <= 64){
                int newSize = size/2;
                MulTask m1 = new MulTask(a,b,c, rowA, colA, rowB, newSize, 1);
                MulTask m2 = new MulTask(a,b,c, rowA, colA+newSize, rowB+newSize, newSize, 1);
                MulTask m3 = new MulTask(a,b,c, rowA+newSize, colA, rowB,  size - newSize, 1);
                MulTask m4 = new MulTask(a,b,c, rowA+newSize, colA+newSize, rowB+newSize, size - newSize, 1);

                m1.call();
                m2.call();
                m3.call();
                m4.call();
                return null;
            }else{
                int newSize = size/2;
                Future<?>[] futures = new Future[4];
                futures[0] = exec.submit(new MulTask(a,b,c, rowA, colA, rowB, newSize, 1));
                futures[1] = exec.submit(new MulTask(a,b,c, rowA, colA+newSize, rowB+newSize, newSize, 1));
                futures[2] = exec.submit(new MulTask(a,b,c, rowA+newSize, colA, rowB, size - newSize, 1));
                futures[3] = exec.submit(new MulTask(a,b,c, rowA+newSize, colA+newSize, rowB+newSize, size - newSize, 1));

                for(int i =0; i < futures.length; i++){
                    try {
                        futures[i].get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        }
    }

    static class AddTask implements Callable<Object> {
        double[][] c;
        double[] finalC;
        int row;


        public AddTask(double[][] c, double[] finalC, int row) {
            this.c = c;
            this.finalC = finalC;
            this.row = row;
        }

        public Object call() {
            for(int i = 0; i < finalC.length; i++){
                finalC[row] += c[row][i];
            }

            return null;
        }
    }
}