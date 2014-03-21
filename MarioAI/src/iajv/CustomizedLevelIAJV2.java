package iajv;

import iajv.CustomizedLevelIAJV;

import java.util.ArrayList;
import java.util.Random;

import mlp.DenseVector;
import mlp.ExampleSet;
import mlp.LinearModule;
import mlp.MLP;
import mlp.SquareLoss;
import mlp.TanHModule;
import dk.itu.mario.MarioInterface.Constraints;
import dk.itu.mario.MarioInterface.GamePlay;
import dk.itu.mario.MarioInterface.LevelInterface;
import dk.itu.mario.engine.sprites.Enemy;
import dk.itu.mario.engine.sprites.SpriteTemplate;
import dk.itu.mario.level.Level;

public class CustomizedLevelIAJV2 extends CustomizedLevelIAJV {

	// nomdre de neurone cache dans le reseau de neurone
	private static final int nb_neurone = 8;
	private MLP p;

	public CustomizedLevelIAJV2(int width, int height, long seed, int difficulty, int type, GamePlay playerMetrics) {
		super(width, height, seed, difficulty, type, playerMetrics);

	}

	public void configureParameters(GamePlay playerM) {
		setReseau();

		// Calculer les parametres a partir de traces et amusement
		int i = 0;

		DenseVector entree = new DenseVector(12 +1);
		entree.setValue(i++, playerM.aimlessJumps);
		entree.setValue(i++, (float)playerM.timeSpentRunning / playerM.completionTime);
		entree.setValue(i++, (float)playerM.coinsCollected / playerM.totalCoins);
		entree.setValue(i++, playerM.percentageCoinBlocksDestroyed);
		entree.setValue(i++, playerM.percentageEmptyBlockesDestroyed);
		entree.setValue(i++, playerM.percentagePowerBlockDestroyed);
		entree.setValue(i++, playerM.timesOfDeathByFallingIntoGap);
		entree.setValue(i++, (float)playerM.enemyKillByFire / playerM.totalEnemies);
		entree.setValue(i++, (float)playerM.enemyKillByKickingShell / playerM.totalEnemies);
		entree.setValue(i++, (float)playerM.totalTimeLittleMode / playerM.completionTime);
		entree.setValue(i++, (float)playerM.totalTimeLargeMode / playerM.completionTime);
		entree.setValue(i++, (float)playerM.totalTimeFireMode / playerM.completionTime);
		entree.setValue(i++, 1);

		DenseVector parametres = p.getOutput(entree);
		entree.setValue(i - 1, 0);
		entree.save("trace");
		parametres.save("parametres");
		parametres.normaliserTanh();

		i = 0;
		CHANCE_GAP = parametres.getValue(i++) / 6;
		CHANCE_HILL = parametres.getValue(i++) / 5;
		CHANCE_END_HILL = parametres.getValue(i++) / 2;
		CHANCE_HEIGHT_CHANGE = parametres.getValue(i++) / 3;
		CHANCE_PIPE = parametres.getValue(i++) / 5;
		CHANCE_ENEMY = parametres.getValue(i++) / 3;
		CHANCE_WINGED = parametres.getValue(i++) / 5;
		CHANCE_ARMOREDTURTLE = parametres.getValue(i++) / 3;
		CHANCE_JUMPFLOWER = parametres.getValue(i++) / 3;
		CHANCE_CHOMPFLOWER = parametres.getValue(i++) / 3;
		CHANCE_BLOCK = parametres.getValue(i++) / 3;
		CHANCE_END_BLOCK = parametres.getValue(i++) / 2;
		CHANCE_COIN = parametres.getValue(i++) / 4;
		CHANCE_END_COIN = parametres.getValue(i++) / 2;
		CHANCE_BLOCK_POWER_UP = parametres.getValue(i++) / 4;
		CHANCE_BLOCK_COIN = parametres.getValue(i++) / 3;

		for (i = 0; i < 16; i++) {
			System.out.println(parametres.getValue(i));
		}
	}

	private void setReseau() {
		p = new MLP();
		p.setLoss(new SquareLoss(16));
		LinearModule c = new LinearModule(13, nb_neurone);
		c.randomize(0.0025);
		p.addModule(c);
		p.addModule(new TanHModule(nb_neurone));
		c = new LinearModule(nb_neurone, 16);
		c.randomize(0.0025);
		p.addModule(c);
		p.addModule(new TanHModule(16));

		try {
			ExampleSet training_set = new ExampleSet("trace", "parametres");
			// Apprentissage

			double lerr = 0, err = 1;
			int iteration = 0;
			while (true) {
				if (iteration % 40 == 0) {
					err = p.computeError(training_set);
					System.out.println(iteration + " " + err);
					if (Math.abs(err - lerr) < 0.0001)
						break;
					lerr = err;
				}

				for (int i = 0; i < training_set.size(); i++) {
					int idx = (int) (Math.random() * training_set.size());
					p.stochasticGradientStep(0.01, training_set.getInput(idx), training_set.getOutput(idx));
				}
				iteration++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Fin d app");
	}

}
