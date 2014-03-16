package mlp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class TanHModule extends Module {

	protected DenseVector output;
	protected DenseVector delta;

	protected int input_size;

	public TanHModule(int input_size) {
		this.input_size = input_size;
		output = new DenseVector(input_size);
		delta = new DenseVector(input_size);
	}

	@Override
	public void forward(DenseVector input) {
		for (int i = 0; i < input_size; i++) {
			double x = input.getValue(i);
			output.setValue(i, 1.7159 * Math.tanh(x * 0.6666));
		}
	}

	@Override
	public DenseVector getOutput() {
		return (output);
	}

	@Override
	public void backward_updateGradient(DenseVector input, DenseVector deltas_output) {

	}

	@Override
	public void backward_computeDeltaInputs(DenseVector input, DenseVector deltas_output) {

		for (int i = 0; i < delta.size(); i++) {
			double x = input.getValue(i);
			double x2 = deltas_output.getValue(i);
			double t = Math.tanh(0.6666 * x);
			double v = 0.66666 * 1.7159 * (1.0 - t * t);

			delta.setValue(i, v * x2);
		}
	}

	@Override
	public DenseVector getDelta() {
		return delta;
	}

	@Override
	public void init_gradient() {

	}

	@Override
	public void updateParameters(double gradient_step) {

	}

	@Override
	public int getInputDimension() {
		return input_size;
	}

	@Override
	public int getOutputDimension() {
		return input_size;
	}

	@Override
	public void write(BufferedWriter bw) {
		
	}

	@Override
	public void randomize(double d) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void read(BufferedReader bw) throws IOException {
		// TODO Auto-generated method stub
		
	}
}
