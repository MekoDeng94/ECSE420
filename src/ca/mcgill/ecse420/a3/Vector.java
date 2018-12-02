package ca.mcgill.ecse420.a3;

public class Vector {

    int dim;
    double[] data;
    int rowDisplace;

    public Vector(int d) {
        dim = d;
        rowDisplace = 0;
        data = new double[d];
    }

    public Vector(double[] matrix, int x,int d) {
        data = matrix;
        rowDisplace = x;
        dim = d;
    }

    public double get(int row) {
        return data[row + rowDisplace];
    }

    public void set(int row, double value) {
        data[row + rowDisplace]= value;
    }

    public void add(int row, double value) {
        data[row + rowDisplace] += value;
    }

    public int getDim() {
        return dim;
    }

    Vector[] split() {
        Vector[] result = new Vector[2];

        int newDim = dim / 2;
        result[0] =
                new Vector(data, rowDisplace, newDim);
        result[1]=
                new Vector(data, rowDisplace + newDim, newDim);

        return result;
    }
}
