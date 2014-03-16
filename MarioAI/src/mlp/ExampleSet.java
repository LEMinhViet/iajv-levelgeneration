package mlp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ExampleSet {
	protected ArrayList<DenseVector> inputs;
	protected ArrayList<DenseVector> outputs;
	int input_dimension;
	int output_dimension;

	public ExampleSet(int input_dimension, int output_dimension) {
		this.input_dimension = input_dimension;
		this.output_dimension = output_dimension;
		inputs = new ArrayList<DenseVector>();
		outputs = new ArrayList<DenseVector>();
	}

	/**
	 * Permet la lecture d'un fichier au format libSVM (binaire)
	 * 
	 * @param filename
	 */
	public ExampleSet(String filename) {
		inputs = new ArrayList<DenseVector>();
		outputs = new ArrayList<DenseVector>();
		int feat_max = 0;

		try {
			InputStream ips = new FileInputStream(filename);
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			String ligne;

			while ((ligne = br.readLine()) != null) {
				String category;

				StringTokenizer st = new StringTokenizer(ligne);
				if (st.hasMoreTokens()) {
					category = st.nextToken();
					while (st.hasMoreElements()) {
						String token = st.nextToken();
						StringTokenizer st2 = new StringTokenizer(token, ":");
						int feat = Integer.parseInt(st2.nextToken());
						String val = st2.nextToken();
						if (feat > feat_max)
							feat_max = feat;
					}
				}
			}
			br.close();
		} catch (Exception e) {
			System.out.println("Probleme de lecture de fichier");
			System.exit(1);
		}
		int size_vector = feat_max + 1;
		input_dimension = size_vector;
		output_dimension = 1;

		System.out.println("Number of features is :" + size_vector);

		try {
			InputStream ips = new FileInputStream(filename);
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			String ligne;

			while ((ligne = br.readLine()) != null) {
				String category;
				StringTokenizer st = new StringTokenizer(ligne);
				if (st.hasMoreTokens()) {
					category = st.nextToken();
					double val = Double.parseDouble(category);
					if (val != 1)
						val = -1;
					DenseVector output = new DenseVector(1);
					output.setValue(0, val);
					DenseVector input = new DenseVector(size_vector);
					while (st.hasMoreElements()) {
						String token = st.nextToken();
						StringTokenizer st2 = new StringTokenizer(token, ":");
						int feat = Integer.parseInt(st2.nextToken());
						String vv = st2.nextToken();
						input.setValue(feat, Double.parseDouble(vv));
					}
					addExample(input, output);
				}

			}
			br.close();
		} catch (Exception e) {
			System.out.println("Probleme de lecture de fichier");
			System.exit(1);
		}
		System.out.println("Number of examples is " + size());
	}

	/**
	 * Permet la lecture de 2 fichiers entree et sortie
	 * 
	 * @param filename
	 * @throws Exception
	 */
	public ExampleSet(String filename_inputs, String filename_outputs) throws Exception {
		inputs = new ArrayList<DenseVector>();
		outputs = new ArrayList<DenseVector>();
		BufferedReader bw1 = new BufferedReader(new FileReader(filename_inputs));
		BufferedReader bw2 = new BufferedReader(new FileReader(filename_outputs));

		String l1 = null;
		while ((l1 = bw1.readLine()) != null) {
			StringTokenizer st1 = new StringTokenizer(l1);
			int taille1 = Integer.parseInt(st1.nextToken());

			String l2 = bw2.readLine();
			StringTokenizer st2 = new StringTokenizer(l2);
			int taille2 = Integer.parseInt(st2.nextToken());

			DenseVector dv1 = new DenseVector(taille1);
			for (int i = 0; i < taille1; i++)
				dv1.setValue(i, Double.parseDouble(st1.nextToken()));

			DenseVector dv2 = new DenseVector(taille2);
			for (int i = 0; i < taille2; i++)
				dv2.setValue(i, Double.parseDouble(st2.nextToken()));

			addExample(dv1, dv2);
		}
		this.input_dimension = getInput(0).size;
		this.output_dimension = getOutput(0).size;

		bw1.close();
		bw2.close();
	}

	public void addExample(DenseVector input, DenseVector output) {
		inputs.add(input);
		outputs.add(output);
	}

	public DenseVector getInput(int i) {
		return (inputs.get(i));
	}

	public DenseVector getOutput(int i) {
		return (outputs.get(i));
	}

	public int size() {
		return (inputs.size());
	}

	public int getInputDimension() {
		return (input_dimension);
	}

	public int getOutputDimension() {
		return (output_dimension);
	}

}
