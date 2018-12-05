package ca.mcgill.ecse420.a3;

import java.util.concurrent.*;

public class Question4 {
    private static ExecutorService exec = Executors.newFixedThreadPool(8);
    private static Object[] C_locks;
    private static final int MATRIX_SIZE = 2000;

    public static void main(String[] args) {
        long startTime;
        long endTime;
        double[][] A = generateSquareMatrix(MATRIX_SIZE);
        double[] B = generateVector(MATRIX_SIZE);
        double[] C_sequential;

        startTime = System.nanoTime();      //for timing purposes
        C_sequential = sequentialMultiplicationMatrixVector(A, B);
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
        for (int i = 0; i < C_parallel.length; i++) {
            if (C_parallel[i] != C_sequential[i]) {
                System.out.println("Parallel multiplication returned the wrong vector");
                break;
            }
        }
    }

    private static double[][] generateSquareMatrix(int size) {
        double matrix[][] = new double[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                matrix[row][col] = (double) ((int) (Math.random() * 10.0));
            }
        }
        return matrix;
    }

    private static double[] generateVector(int size) {
        double vector[] = new double[size];
        for (int i = 0; i < size; i++) {
            vector[i] = (double) ((int) (Math.random() * 10.0));
        }
        return vector;
    }

    private static double[] sequentialMultiplicationMatrixVector(double[][] a, double[] b) {
        double[] c = new double[b.length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b.length; j++) {
                c[i] += a[i][j] * b[j];
            }
        }

        return c;
    }

    private static double[] mul(double[][] a, double[] b) throws ExecutionException, InterruptedException {
        int n = b.length;
        double[] c = new double[n];
        C_locks = new Object[n];
        for (int i = 0; i < C_locks.length; i++) {
            C_locks[i] = new Object();
        }
        Future<?> future = exec.submit(new MulTask(a, b, c, 0, 0, 0, n));
        future.get();

        return c;
    }

    static class MulTask implements Callable<Object> {
        double[][] a;
        double[] b;
        double[] c;
        int rowA;
        int colA;
        int rowB;
        int size;


        MulTask(double[][] a, double[] b, double[] c, int rowA, int colA, int rowB, int size) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.rowA = rowA;
            this.colA = colA;
            this.rowB = rowB;
            this.size = size;
        }

        public Object call() {

            if (size <= MATRIX_SIZE / 4) {

                for (int i = rowA; i < rowA + size; i++) {
                    synchronized (C_locks[i]) {
                        for (int j = rowB; j < rowB + size; j++) {
                            c[i] += a[i][j] * b[j];
                        }
                    }
                }
                return null;
            } else {
                try {
                    int newSize = size / 2;
                    Future<?>[] futures = new Future[4];
                    futures[0] = exec.submit(new MulTask(a, b, c, rowA, colA, rowB, newSize));
                    futures[1] = exec.submit(new MulTask(a, b, c, rowA, colA + newSize, rowB + newSize, newSize));
                    futures[2] = exec.submit(new MulTask(a, b, c, rowA + newSize, colA, rowB, size - newSize));
                    futures[3] = exec.submit(new MulTask(a, b, c, rowA + newSize, colA + newSize, rowB + newSize, size - newSize));
                    futures[0].get();
                    futures[1].get();
                    futures[2].get();
                    futures[3].get();
                } catch (Exception ignored) {

                }
                return null;
            }
        }
    }
}