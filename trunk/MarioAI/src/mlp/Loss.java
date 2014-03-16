package mlp;

public abstract class Loss {
		/**
		 *  Permet de calculer les delta de la fonction de cout
		 **/
		 public abstract void backward(DenseVector input,DenseVector output);
		 
		 /**
		  * Permet de recuperer les derniers deltas calcules
		  */
		 public abstract DenseVector getDelta();

		 /**
		  * Permet de calculer la sortie du module de cout
		  * @param input entree du module
		  * @param output sortie desiree
		  */
		 public abstract void computeValue(DenseVector input,DenseVector output);
		 
		 /**
		  * Permet de recuperer la derniere valeur calculee
		  * @return
		  */
		 public abstract double getValue();
}
