package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MatrixMultiplication {

    private static final int NUMBER_THREADS = 2;
    private static final int MATRIX_SIZE = 5000;

    public static void main(String[] args) {

        // Generate two random matrices, same size
        double[][] a = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
        double[][] b = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);

        long startTime = System.nanoTime();     //for timing purposes
        double[][] sequential = sequentialMultiplyMatrix(a, b);
        long endTime = System.nanoTime();       //for timing purposes

        System.out.println("Sequential total time: " + (endTime - startTime)/1000000000.0 + " s");

        startTime = System.nanoTime();      //for timing purposes
        double[][] parallel = parallelMultiplyMatrix(a, b);
        endTime = System.nanoTime();        //for timing purposes

        System.out.println("Parallel total time: " + (endTime - startTime)/1000000000.0 + " s");
    }

    /**
     * Returns the result of a sequential matrix multiplication
     * The two matrices are randomly generated
     * @param a is the first matrix
     * @param b is the second matrix
     * @return the result of the multiplication
     * */
    public static double[][] sequentialMultiplyMatrix(double[][] a, double[][] b) {

        double[][] c = new double[MATRIX_SIZE][MATRIX_SIZE];

        for (int i = 0; i < MATRIX_SIZE; i++){
            for (int j = 0; j < MATRIX_SIZE; j++){
                c[i][j] = 0.0000;
                for (int k = 0; k < MATRIX_SIZE; k++) {
                    c[i][j] += a[i][k] * b[k][j];
                }
            }
        }

        return c;
    }

    /**
     * Returns the result of a concurrent matrix multiplication
     * The two matrices are randomly generated
     * @param a is the first matrix
     * @param b is the second matrix
     * @return the result of the multiplication
     * */
    public static double[][] parallelMultiplyMatrix(double[][] a, double[][] b) {
        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);

        double[][] c = new double [MATRIX_SIZE][MATRIX_SIZE] ;

        for (int i = 0; i < MATRIX_SIZE; i++){
            for (int j = 0; j < MATRIX_SIZE; j++){
                executor.execute(new TaskClass(i,j,a,b,c));
            }
        }
        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {

        }
        return c;
    }

    public static class TaskClass implements Runnable {
        int row;
        int column;
        double[][] a;
        double[][] b;
        double[][] c;

        public TaskClass(int row, int column, double[][] a, double[][] b, double[][] c ) {
            this.row = row;
            this.column = column;
            this.a = a;
            this.b = b;
            this.c = c;
        }

        public void run() {
            c[row][column] = 0;
            for (int i=0; i<MATRIX_SIZE; i++){
                c[row][column] += a[row][i] * b[i][column];
            }
        }
    }

    /**
     * Populates a matrix of given size with randomly generated integers between 0-10.
     * @param numRows number of rows
     * @param numCols number of cols
     * @return matrix
     */
    private static double[][] generateRandomMatrix (int numRows, int numCols) {
        double matrix[][] = new double[numRows][numCols];
        for (int row = 0 ; row < numRows ; row++ ) {
            for (int col = 0 ; col < numCols ; col++ ) {
                matrix[row][col] = (double) ((int) (Math.random() * 10.0));
            }
        }
        return matrix;
    }
}
