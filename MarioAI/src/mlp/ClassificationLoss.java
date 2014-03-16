package mlp;


public class ClassificationLoss extends Loss {

	protected double value;

	@Override
	public void backward(DenseVector input, DenseVector output) {

	}

	@Override
	public DenseVector getDelta() {
		return null;
	}

	@Override
	public void computeValue(DenseVector input, DenseVector output) {
		value = 0L;

		if(input.argmax() == output.argmax())
			value = 1L;
	}

	@Override
	public double getValue() {
		return value;
	}

}
