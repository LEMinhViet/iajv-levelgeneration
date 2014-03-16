package mlp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public abstract class Module 
{
	
		/**
		 * Permet le calcul de la sortie du module
		 * @param input
		 */
		public abstract void forward(DenseVector input);

		/**
		 * Permet de recuperer la derniere sortie calculee
		 * @return
		 */
		public abstract DenseVector getOutput();
		
		/**
		 * Permet de mettre a jour le gradient des parametres du module
		 * @param input
		 * @param deltas_output
		 */
		public abstract void backward_updateGradient(DenseVector input,DenseVector deltas_output);	
		
		/**
		 * Permet de calculer la contriubtion a l'erreur des neurones d'ENTREE (delta)
		 * 
		 * @param input
		 * @param deltas_output
		 */
		public abstract void backward_computeDeltaInputs(DenseVector input,DenseVector deltas_output);	

		/**
		 * Permet de recuperer les derniers delta calcules
		 * @return
		 */		
		public abstract DenseVector getDelta();
		
		public void backward(DenseVector input,DenseVector deltas_output)
		{
			backward_updateGradient(input,deltas_output);
			backward_computeDeltaInputs(input,deltas_output);
		}
		
		/**
		 * Initialise le graident a 0 
		 **/
		public abstract void init_gradient();

		/**
		 * Permet de mettre a jour les parameters p <- p + gradient_step * gradient
		 **/
		public abstract void updateParameters(double gradient_step);

		/**
		 * Permet de recuperer les parametres du module pour les afficher
		 */
		public DenseVector getParameters()
		{
			return(null);
		}

		/**
		 * Permet de tirer au hasard les parameteres du module
		 */
		public abstract void randomize(double d);
		
		public abstract int getInputDimension();
		public abstract int getOutputDimension();

		public abstract void write(BufferedWriter bw) throws IOException;

		public abstract void read(BufferedReader bw) throws IOException;
		
}
