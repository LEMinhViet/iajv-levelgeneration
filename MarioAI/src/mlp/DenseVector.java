package mlp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Cette classe decrit un vecteur
 * 
 * @author denoyer
 * 
 */

public class DenseVector {
	protected double[] values;
	protected int size;

	public DenseVector(int size) {
		this.size = size;
		values = new double[size];
	}

	public DenseVector(DenseVector output) {
		this.size = output.size;
		values = new double[size];
		for (int i = 0; i < output.size; i++)
			values[i] = output.values[i];
	}

	public void setValue(int i, double v) {
		// assert ((i>=0) && (i<size));
		values[i] = v;
	}

	public double getValue(int i) {
		return (values[i]);
	}

	public int size() {
		return (size);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("V");
		for (int i = 0; i < size; i++) {
			if (values[i] != 0) {
				sb.append(" ");
				sb.append(i + ":" + values[i]);
			}
		}
		return (sb.toString());
	}

	public int argmax() {
		int max = 0;
		for (int i = 0; i < values.length; i++)
			if (values[max] < values[i])
				max = i;

		return max;
	}

	public int argmax(int debut) {
		int max = debut;
		for (int i = debut; i < values.length; i++)
			if (values[max] < values[i])
				max = i;

		return max - debut;
	}

	public void normaliserTanh() {
		for (int i = 0; i < values.length; i++) {
			values[i] = values[i] + 1.7159;
			values[i] = values[i] / (1.7159 * 2);
		}
	}

	public void diviseAll(int v) {
		for (int i = 0; i < values.length; i++) {
			values[i] /= v;
		}
	}

	public void save(String fichier) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(fichier, true));

			bw.write(values.length + " ");

			for (int i = 0; i < values.length; i++)
				bw.write(String.valueOf(values[i]) + " ");
			bw.write("\n");

			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
