package mlp;

public class SquareLoss extends Loss {
	protected DenseVector delta;

	protected double value;

	protected int size;

	public SquareLoss(int size) {
		this.size = size;
		value = 0.0;
		delta = new DenseVector(size);
	}

	@Override
	public void backward(DenseVector input, DenseVector output) {

		for (int i = 0; i < input.size(); i++)
			delta.setValue(i, 2. * (input.getValue(i) - output.getValue(i)));
	}

	@Override
	public DenseVector getDelta() {
		return (delta);
	}

	@Override
	public void computeValue(DenseVector input, DenseVector output) {
		value = 0L;

		for (int i = 0; i < input.size(); i++)
			value += Math.pow(input.getValue(i) - output.getValue(i), 2);

		value /= input.size();
	}

	@Override
	public double getValue() {
		return value;
	}

}
