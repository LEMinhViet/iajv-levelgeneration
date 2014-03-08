package iajv;

import java.util.Random;

import dk.itu.mario.MarioInterface.GamePlay;
import dk.itu.mario.MarioInterface.LevelGenerator;
import dk.itu.mario.MarioInterface.LevelInterface;

public class CustomizedLevelGeneratorWeber implements LevelGenerator {
	public LevelInterface generateLevel(GamePlay playerMetrics) {
		LevelInterface level = new CustomizedLevelWeber(320,15,new Random().nextLong(),1,2,playerMetrics);
		return level;
	}

	@Override
	public LevelInterface generateLevel(String detailedInfo) {
		// TODO Auto-generated method stub
		return null;
	}
}
