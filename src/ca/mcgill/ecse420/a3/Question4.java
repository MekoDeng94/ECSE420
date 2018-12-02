package ca.mcgill.ecse420.a3;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Question4 {
    static ExecutorService exec = Executors.newFixedThreadPool(8);

    public static void main(String[] args) {
        long startTime = 0;
        long endTime = 0;
        exec = Executors.newCachedThreadPool();

        startTime = System.nanoTime();      //for timing purposes

        double[][] A = generateSquareMatrix(2000);
        double[] B = generateVector(2000);
        Matrix A_matrix = new Matrix(A, 0, 0, A.length);
        Vector B_vector = new Vector(B, 0, B.length);
        Vector C = null;
        try {
            C = mul(A_matrix, B_vector);
        } catch (Exception e) {
            System.out.println("Failed");

        }
        exec.shutdown();

        endTime = System.nanoTime();      //for timing purposes

        System.out.println("Size " + 2000 + "Parallel total time: " + (endTime - startTime) / 1000000000.0 + " s");

    }

    private static double[][] generateSquareMatrix(int size) {
        int N = size;
        if (size % 2 != 0) {
            N++;
        }
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
        if (size % 2 != 0) {
            N++;
        }
        double vector[] = new double[N];
        for (int i = 0; i < size; i++) {
            vector[i] = (double) ((int) (Math.random() * 10.0));
        }
        return vector;
    }

    static Matrix add(Matrix a, Matrix b) throws ExecutionException, InterruptedException {
        int n = a.getDim();
        Matrix c = new Matrix(n);
        Future<?> future = exec.submit(new AddTask(a, b, c));
        future.get();
        return c;
    }

    static Vector mul(Matrix a, Vector b) throws ExecutionException, InterruptedException {
        int n = a.getDim();
        Vector c = new Vector(n);
        Future<?> future = exec.submit(new MulTask(a, b, c));
        future.get();
        return c;
    }

    static class AddTask implements Runnable {
        Matrix a, b, c;

        public AddTask(Matrix myA, Matrix myB, Matrix myC) {
            a = myA;
            b = myB;
            c = myC;
        }

        public void run() {
            try {
                int n = a.getDim();
                if (n == 1) {
                    c.set(0, 0, a.get(0, 0) + b.get(0, 0));
                } else {
                    Matrix[][] aa = a.split(), bb = b.split(), cc = c.split();
                    Future<?>[][] future = (Future<?>[][]) new Future[2][2];
                    for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < 2; j++) {
                            future[i][j] =
                                    exec.submit(new AddTask(aa[i][j], bb[i][j], cc[i][j]));
                        }
                    }
                    for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < 2; j++) {
                            future[i][j].get();
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    static class MulTask implements Runnable {
        Matrix a;
        Vector b, c;

        public MulTask(Matrix myA, Vector myB, Vector myC) {
            a = myA;
            b = myB;
            c = myC;
        }

        public void run() {
            try {
                if (a.getDim() == 1) {
                    double valA = a.get(0, 0);
                    double valB = b.get(0);
                    c.add(0, valA * valB);
                } else {
                    Matrix[][] aa = a.split();
                    Vector[] bb = b.split(), cc = c.split();
                    Future<?>[][] future = (Future<?>[][]) new Future[2][2];
                    for (int i = 0; i < 2; i++)
                        for (int j = 0; j < 2; j++) {
                            future[i][j] =
                                    exec.submit(new MulTask(aa[i][j], bb[j], cc[i]));
                        }

                   /* future[0][0][0] =
                            exec.submit(new MulTask(aa[0][0], bb[0], cc[0]));
                    future[0][0][1] =
                            exec.submit(new MulTask(aa[0][1], bb[1], cc[0]));

                    future[1][0][0] =
                            exec.submit(new MulTask(aa[1][0], bb[0], cc[1]));
                    future[1][0][1] =
                            exec.submit(new MulTask(aa[1][1], bb[1], cc[1]));*/


                    for (int i = 0; i < 2; i++)
                        for (int j = 0; j < 2; j++)
                            future[i][j].get();

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}