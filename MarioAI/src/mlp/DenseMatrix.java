package mlp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class DenseMatrix {
	// protected double[][] values;
	protected double[] values;
	protected int numberOfRows;
	protected int numberOfColumns;

	public DenseMatrix(int numberOfRows, int numberOfColumns) {
		this.numberOfRows = numberOfRows;
		this.numberOfColumns = numberOfColumns;
		// values=new double[numberOfRows][numberOfColumns];
		values = new double[numberOfRows * numberOfColumns];
	}

	public void setValue(int r, int c, double v) {
		// values[r][c]=v;
		values[r * numberOfColumns + c] = v;
	}

	public double getValue(int r, int c) {
		return values[r * numberOfColumns + c];
		// return(values[r][c]);
	}

	public int getNumberOfRows() {
		return (numberOfRows);
	}

	public int getNubmerOfColumns() {
		return (numberOfColumns);
	}

	public void write(BufferedWriter bw) throws IOException {
		bw.write(numberOfRows + " " + numberOfColumns + "\n");

		for (int i = 0; i < values.length; i++)
			bw.write(String.valueOf(values[i]) + " ");
		bw.write("\n");
	}

	public void load(BufferedReader bw) throws IOException {
		String l1 = bw.readLine();
		StringTokenizer st = new StringTokenizer(l1);
		numberOfRows = Integer.parseInt(st.nextToken());
		numberOfColumns = Integer.parseInt(st.nextToken());

		values = new double[numberOfRows * numberOfColumns];

		l1 = bw.readLine();
		st = new StringTokenizer(l1);
		for (int i = 0; i < values.length; i++)
			values[i] = Double.parseDouble(st.nextToken());
	}
}
