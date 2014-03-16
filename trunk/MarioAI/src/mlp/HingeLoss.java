package mlp;

public class HingeLoss extends Loss {

	protected DenseVector delta;

	protected double value;

	protected int size;

	public HingeLoss(int size) {
		this.size = size;
		value = 0.0;
		delta = new DenseVector(size);
	}

	@Override
	public void backward(DenseVector input, DenseVector output) {
		for (int i = 0; i < input.size(); i++)
			if (input.getValue(i) * output.getValue(i) < 1L)
				delta.setValue(i, -output.getValue(i) /* * input.getValue(i)*/);
			else
				delta.setValue(i, 0);
	}

	@Override
	public DenseVector getDelta() {
		return (delta);
	}

	@Override
	public void computeValue(DenseVector input, DenseVector output) {
		value = 0L;

		for (int i = 0; i < input.size(); i++){
			value += Math.max(0L, 1L - input.getValue(i) * output.getValue(i));
//			System.out.println(Math.max(0L, 1L - input.getValue(i) * output.getValue(i)));
		}
		
		value /= input.size();
//		System.out.println(value*input.size() + " "+input.getValue(0)+" "+output.getValue(0)+" "+
//		Math.max(0L, 1L - input.getValue(0) * output.getValue(0))+" "+
//		Math.max(1L, 1L - input.getValue(1) * output.getValue(1)));

//		System.out.println(output);
	}

	@Override
	public double getValue() {
		return value;
	}

}
