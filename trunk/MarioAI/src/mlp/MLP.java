package mlp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MLP {
	protected ArrayList<Module> modules;
	protected Loss loss;

	public MLP() {
		modules = new ArrayList<Module>();
		loss = null;
	}

	/**
	 * Permet de choisir le cout du MLP
	 */
	public void setLoss(Loss l) {
		loss = l;
	}

	public Loss getLoss() {
		return loss;
	}

	/**
	 * Permet d'ajouter un module au MLP
	 */
	public void addModule(Module m) {
		modules.add(m);
	}

	/**
	 * Permet de recuperer la sortie du MLP
	 */
	public DenseVector getOutput(DenseVector input) {
		modules.get(0).forward(input);
		for (int i = 1; i < modules.size(); i++) {
			modules.get(i).forward(modules.get(i - 1).getOutput());
		}
		int idx = modules.size() - 1;
		return (modules.get(idx).getOutput());
	}

	protected void init_gradient() {
		for (int i = 0; i < modules.size(); i++) {
			modules.get(i).init_gradient();
		}
	}

	protected void updateGradient(double gradient_step) {
		for (int i = 0; i < modules.size(); i++) {
			modules.get(i).updateParameters(gradient_step);
		}
	}

	protected void forward(DenseVector inputs) {
		modules.get(0).forward(inputs);
		for (int i = 1; i < modules.size(); i++) {
			modules.get(i).forward(modules.get(i - 1).getOutput());
		}
	}

	protected void backward(DenseVector inputs, DenseVector outputs) {
		int nb_modules = modules.size();
		loss.backward(modules.get(nb_modules - 1).getOutput(), outputs);

		if (nb_modules > 1)
			modules.get(nb_modules - 1).backward(modules.get(nb_modules - 2).getOutput(), loss.getDelta());
		else
			modules.get(nb_modules - 1).backward_updateGradient(inputs, loss.getDelta());

		for (int i = 1; i < nb_modules; i++) {
			int nbm = nb_modules - i - 1;
			if (nbm == 0) {
				modules.get(nbm).backward_updateGradient(inputs, modules.get(nbm + 1).getDelta());
			} else {
				modules.get(nbm).backward(modules.get(nbm - 1).getOutput(), modules.get(nbm + 1).getDelta());
			}
		}
	}

	/**
	 * Permet de faire un coup de gradient stochastique
	 * 
	 * @param gradient_step
	 * @param inputs
	 * @param outputs
	 */
	public void stochasticGradientStep(double gradient_step, DenseVector inputs, DenseVector outputs) {
		init_gradient();
		forward(inputs);
		backward(inputs, outputs);
		updateGradient(gradient_step);
	}

	/**
	 * Permet le calcul de l'erreur du MLP sur un ensemble de donn�es
	 * 
	 * @param set
	 * @return
	 */
	public double computeError(ExampleSet set) {
		double err = 0.0;
		// int nb_modules=modules.size();
		for (int i = 0; i < set.size(); i++) {
			DenseVector oo = getOutput(set.getInput(i));
			loss.computeValue(oo, set.getOutput(i));
			err += loss.getValue();
		}
		return (err / set.size());
	}

	public Module getModule(int i) {
		return (modules.get(i));
	}

	public void save(String fichier) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(fichier));

			for (int i = 0; i < modules.size(); i++) {
				modules.get(i).write(bw);
			}

			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void load(String fichier) {
		try {
			if (new File(fichier).exists()) {
				BufferedReader bw = new BufferedReader(new FileReader(fichier));

				for (int i = 0; i < modules.size(); i++) {
					modules.get(i).read(bw);
				}

				bw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}