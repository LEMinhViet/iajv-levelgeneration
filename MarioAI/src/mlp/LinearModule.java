package mlp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class LinearModule extends Module {
	protected DenseMatrix parameters;
	protected DenseMatrix gradient;
	protected DenseVector output;
	protected DenseVector delta;

	protected int input_size;
	protected int output_size;

	public LinearModule(int input_size, int output_size) {
		this.input_size = input_size;
		this.output_size = output_size;
		parameters = new DenseMatrix(input_size + 1, output_size);
		gradient = new DenseMatrix(input_size + 1, output_size);
		output = new DenseVector(output_size);
		delta = new DenseVector(input_size + 1);
	}

	public void forward(DenseVector input) {
		for (int i = 0; i < output_size; i++) {
			double sum = parameters.getValue(0, i) * 1;
			for (int j = 1; j < input_size + 1; j++)
				sum += input.getValue(j - 1) * parameters.getValue(j, i);
			output.setValue(i, sum);
		}
	}

	@Override
	public DenseVector getOutput() {
		return (output);
	}

	@Override
	public void backward_updateGradient(DenseVector input, DenseVector deltas_output) {
		for (int j = 0; j < output_size; j++)
			gradient.setValue(0, j, gradient.getValue(0, j) + deltas_output.getValue(j));

		for (int i = 1; i < input_size + 1; i++) {
			for (int j = 0; j < output_size; j++)
				gradient.setValue(i, j, gradient.getValue(i, j) + input.getValue(i - 1) * deltas_output.getValue(j));
		}
	}

	@Override
	public void backward_computeDeltaInputs(DenseVector input, DenseVector deltas_output) {
		double sum = 0L;
		for (int j = 0; j < deltas_output.size(); j++)
			sum += parameters.getValue(0, j) * deltas_output.getValue(j);
		delta.setValue(0, sum * 1);

		for (int i = 1; i < delta.size(); i++) {
			sum = 0L;
			for (int j = 0; j < deltas_output.size(); j++)
				sum += parameters.getValue(i, j) * deltas_output.getValue(j);
			delta.setValue(i, sum * input.getValue(i - 1));
		}
	}

	@Override
	public DenseVector getDelta() {
		return delta;
	}

	@Override
	public void init_gradient() {
		for (int i = 0; i < input_size + 1; i++) {
			for (int j = 0; j < output_size; j++)
				gradient.setValue(i, j, 0L);
		}
	}

	@Override
	public void updateParameters(double gradient_step) {
		for (int i = 0; i < input_size + 1; i++)
			for (int j = 0; j < output_size; j++)
				parameters.setValue(i, j, parameters.getValue(i, j) - gradient_step * gradient.getValue(i, j));
	}

	/**
	 * Randomize the parameters of a module
	 */
	public void randomize(double d) {
		for (int i = 0; i < input_size + 1; i++)
			for (int j = 0; j < output_size; j++)
				parameters.setValue(i, j, d * (Math.random() * 2 - 1.0));
	}

	@Override
	public int getInputDimension() {
		return input_size;
	}

	@Override
	public int getOutputDimension() {
		return output_size;
	}

	@Override
	public void write(BufferedWriter bw) throws IOException {
		parameters.write(bw);
	}

	@Override
	public void read(BufferedReader bw) throws IOException {
		parameters.load(bw);
		
	}
}
