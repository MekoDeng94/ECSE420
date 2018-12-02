package ca.mcgill.ecse420.a3;

public class Matrix {
    int dim;
    double[][] data;
    int rowDisplace, colDisplace;

    public Matrix(int d) {
        dim = d;
        rowDisplace = colDisplace = 0;
        data = new double[d][d];
    }

    public Matrix(double[][] matrix, int x, int y, int d) {
        data = matrix;
        rowDisplace = x;
        colDisplace = y;
        dim = d;
    }

    public double get(int row, int col) {
        return data[row + rowDisplace][col + colDisplace];
    }

    public void set(int row, int col, double value) {
        data[row + rowDisplace][col + colDisplace] = value;
    }

    public void add(int row, int col, double value) {
        data[row + rowDisplace][col + colDisplace] += value;
    }

    public int getDim() {
        return dim;
    }

    Matrix[][] split() {
        Matrix[][] result = new Matrix[2][2];
        int newDim = dim / 2;
        result[0][0] =
                new Matrix(data, rowDisplace, colDisplace, newDim);
        result[0][1] =
                new Matrix(data, rowDisplace, colDisplace + newDim, newDim);
        result[1][0] =
                new Matrix(data, rowDisplace + newDim, colDisplace, newDim);
        result[1][1] =
                new Matrix(data, rowDisplace + newDim, colDisplace + newDim, newDim);
        return result;
    }
}