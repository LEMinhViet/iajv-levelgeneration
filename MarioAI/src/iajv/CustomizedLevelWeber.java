package iajv;

import java.util.ArrayList;
import java.util.Random;

import dk.itu.mario.MarioInterface.Constraints;
import dk.itu.mario.MarioInterface.GamePlay;
import dk.itu.mario.MarioInterface.LevelInterface;
import dk.itu.mario.engine.sprites.SpriteTemplate;
import dk.itu.mario.level.Level;
/**
 * A Probabilistic Multi-Pass Level Generator.
 * 
 * Ben Weber
 * http://users.soe.ucsc.edu/~bweber
 * August 31, 2010
 * Expressive Intelligence Studio
 * UC Santa Cruz
 * 
 * Entry for the CIG 2010 Mario Level Generation Competition. For a detailed explanation:
 * 
 * Performs Multiple through the level:
 *  1. Ground
 *  2. Additional hills
 *  3. Pipes
 *  4. Enemies
 *  5. Blocks
 *  6. Coins
 *  
 * Constraints are enforced by over-generating and constraining. 
 * 
 */
public class CustomizedLevelWeber extends Level implements LevelInterface {

	 /**
	  * Provides a seed for the level generation, input an integer parameter for static generation.
	  */
	 private Random rand = new Random();
	 
	 /**  
 	  * Probabilities of specific events to occur.
 	  */
	 private double CHANCE_BLOCK_POWER_UP = 0.1;
	 private double CHANCE_BLOCK_COIN = 0.3;
	 private double CHANCE_BLOCK_ENEMY = 0.2;
	 private double CHANCE_WINGED = 0.5;
	 private double CHANCE_COIN = 0.2;
	 private double CHANCE_PLATFORM = 0.1;
	 private double CHANCE_END_PLATFORM = 0.1;
	 private double CHANCE_ENEMY = 0.1;
	 private double CHANCE_PIPE = 0.1;
	 private double CHANCE_HILL = 0.1;
	 private double CHANCE_END_HILL = 0.3;
	 private double CHANCE_HILL_ENEMY = 0.3;
	 private double CHANCE_GAP = 0.1;
	 private double CHANCE_HILL_CHANGE = 0.1;

	 /**
	  * Properties that constrain generation.
	  */
	 private double COIN_HEIGHT = 5;
	 private int PLATFORM_HEIGHT = 4;
	 private int PIPE_MIN_HEIGHT = 2;
	 private double PIPE_HEIGHT = 3.0;
	 private int minX = 5;
	 private double HILL_HEIGHT = 4;
	 private int GAP_LENGTH = 5;
	 private double GAP_OFFSET = -5;
	 private double GAP_RANGE = 10;
	 private int GROUND_MAX_HEIGHT = 5;

	 /**
	  * Counts of instances of objects that must be constrained.
	  */
     int gapCount = 0;
     int turtleCount = 0;
     int coinBlockCount = 0;

     /**
      * Level constructor specified by the competition.
      * 
      * @param width - width of the level (tiles)
      * @param height - height of the level (tiles)
      * @param seed - ???
      * @param difficulty - ???
      * @param type - ???
      * @param playerMetrics - metrics collected about the player from the training level (ignored by my generator)
      */
     public CustomizedLevelWeber(int width, int height, long seed, int difficulty, int type, GamePlay playerMetrics) {
    	super(width, height);
    	
    	// keeps track of the ground height
        ArrayList<Integer> ground = new ArrayList<Integer>();
		
        // select the starting ground height
		int lastY = GROUND_MAX_HEIGHT + (int)(rand.nextDouble()*(height - 1 - GROUND_MAX_HEIGHT));
		int y = lastY;
		int nextY = y;
		boolean justChanged = false;
		int length = 0;
		int landHeight = height - 1;
		
		/**
		 * Pass 1: Place the ground
		 */
		for (int x=0; x<width; x++) {
			
			// need more ground (current gap is too large)
			if (length > GAP_LENGTH && y >= height) {
				nextY = landHeight;
				justChanged = true;
				length = 1;
			}
			// adjust ground level
			else if (x > minX && rand.nextDouble() < CHANCE_HILL_CHANGE && !justChanged) {
				nextY += (int)(GAP_OFFSET + GAP_RANGE*rand.nextDouble());
				nextY = Math.min(height - 2, nextY);
				nextY = Math.max(5, nextY);
				justChanged = true;
				length = 1;
			}
			// add a gap
			// checks that the gap constraint is not violated
			else if (x > minX && y < height &&  rand.nextDouble() < CHANCE_GAP && !justChanged && gapCount < Constraints.gaps) {
				landHeight = Math.min(height - 1, lastY);
				nextY = height;
				justChanged = true;
				length = 1;
				gapCount++;
			}
			// continue placing flat ground
			else {
				length++;
				justChanged = false;
			}
			
			setGroundHeight(x, y, lastY, nextY);
			ground.add(y);			
			lastY = y;			
			y = nextY;
		}
				
		/**
		 * Pass 2: Place additional hills (non-x colliding)
		 */
		int x=0;		
		y = height;
		for (Integer h : ground) {	// iterate from left to right at the current ground height
			if (y == height) {			
				
				// start a hill
				if (x > 10 && rand.nextDouble() < CHANCE_HILL) {
					y  = (int)(HILL_HEIGHT + rand.nextDouble()*(h - HILL_HEIGHT));
					setBlock(x, y, this.HILL_TOP_LEFT);		
					
					for (int i=y + 1; i<h; i++) {
						setBlock(x, i, this.HILL_LEFT);		
					}

				}
			}
			else {
				// end hill if hitting a wall
				if (y >= h) {
					y = height;
				}
				else {
					// end the current hill
					if (rand.nextDouble() < CHANCE_END_HILL) {
						setBlock(x, y, this.HILL_TOP_RIGHT);		
						
						for (int i=y + 1; i<h; i++) {
							setBlock(x, i, this.HILL_RIGHT);		
						}
						
						y = height;
					}
					// continue placing the hill
					else {
						setBlock(x, y, this.HILL_TOP);		
						
						for (int i=y + 1; i<h; i++) {
							setBlock(x, i, this.HILL_FILL);		
						}

						// place enemies on the hill
						if (rand.nextDouble() < CHANCE_HILL_ENEMY) {
							boolean winged = rand.nextDouble() < CHANCE_WINGED;
							int t = (int)(rand.nextDouble()*(SpriteTemplate.CHOMP_FLOWER + 1));
							
							// check that turtle constraint is not violated
							if (t==SpriteTemplate.GREEN_TURTLE || t==SpriteTemplate.RED_TURTLE) {				
								if (turtleCount < Constraints.turtels) {
									turtleCount++;
								}
								else {
									t = SpriteTemplate.GOOMPA;
								}
							}
													
							setSpriteTemplate(x, y - 1, new SpriteTemplate(t, winged));
						}
					}
				}
			}
			
			x++;
		}		

		/**
		 * Pass 3: Decorate with pipes
		 */
		lastY = 0;
		int lastlastY = 0;
		x=0;		
		int lastX = 0;
		for (Integer h : ground) {
			
			// place a pipe
			if (x > minX && rand.nextDouble() < CHANCE_PIPE) {
				if (h == lastY && lastlastY <= lastY && x > (lastX + 1)) {				
					height = PIPE_MIN_HEIGHT + (int)(rand.nextDouble()*PIPE_HEIGHT);
					placePipe(x - 1, h, height);
					lastX = x;
				}
			}
			
			lastlastY = lastY;
			lastY = h;
			x++;
		}		
		
		/**
		 * Pass 4: Place enemies (on top of the ground, hills and pipes)
		 */
		x=0;		
		for (Integer h : ground) {
			if (x > minX && rand.nextDouble() < CHANCE_ENEMY) {
				boolean winged = rand.nextDouble() < CHANCE_WINGED;
				int t = (int)(rand.nextDouble()*(SpriteTemplate.CHOMP_FLOWER + 1));

				// check that the turtle constraint is not violated
				if (t==SpriteTemplate.GREEN_TURTLE || t==SpriteTemplate.RED_TURTLE) {				
					if (turtleCount < Constraints.turtels) {
						turtleCount++;
					}
					else {
						t = SpriteTemplate.GOOMPA;
					}
				}
				
				int tile = getBlock(x, h - 1);
				if (tile == 0) {
					setSpriteTemplate(x, h - 1, new SpriteTemplate(t, winged));
				}

			}
			
			x++;
		}
				
		/**
		 * Pass 5: Place blocks
		 */
		x=0;		
		y = height;
		for (Integer h : ground) {
			int max = 0;
			
			// find the highest object
			for (max=0; max<h; max++) {
				int tile = getBlock(x, max);
				if (tile != 0) {
					break;
				}				
			}
			
			if (y == height) {		
				
				// start a block
				if (x > minX && rand.nextDouble() < CHANCE_PLATFORM) {
					y  = max - PLATFORM_HEIGHT; // (int)(-5*rand.nextDouble()*(h - 0));
					
					if (y >= 1  && h - max > 1) {
						placeBlock(x, y);
					}
					else {
						y = height;
					}
				}
			}
			else {
				
				// end if hitting a wall
				if (y >= (max + 1)) {
					y = height;
				}
				// end the current block
				else if (rand.nextDouble() < CHANCE_END_PLATFORM) {
					placeBlock(x, y);
					y = height;
				}
				// continue placing the current block
				else {
					placeBlock(x, y);
				}
			}
			
			x++;
		}

		/**
		 * Pass 6: Decorate with coins
		 */
		x=0;		
		for (Integer h : ground) {
			
			// place a coin
			if (x > 5 && rand.nextDouble() < CHANCE_COIN) {
				y = h - (int)(1 + rand.nextDouble()*COIN_HEIGHT);
				
				int tile = getBlock(x, y);
				if (tile == 0) {
					setBlock(x, y, this.COIN);		
				}
			}
			
			x++;
		}

		// place the exit
		this.xExit = width - 5;
	}

     /**
      * Places a block at the specific level location.
      */
	public void placeBlock(int x, int y) {

		// choose block type
		if (rand.nextDouble() < CHANCE_BLOCK_POWER_UP) {
			setBlock(x, y, this.BLOCK_POWERUP);		
		}
		else if (rand.nextDouble() < CHANCE_BLOCK_COIN && coinBlockCount < Constraints.coinBlocks) {
			setBlock(x, y, this.BLOCK_COIN);					
			coinBlockCount++;
		}
		else {
			setBlock(x, y, this.BLOCK_EMPTY);		
		}		
		
		// place enemies
		if (rand.nextDouble() < CHANCE_BLOCK_ENEMY) {
			boolean winged = rand.nextDouble() < CHANCE_WINGED;
			int t = (int)(rand.nextDouble()*(SpriteTemplate.CHOMP_FLOWER + 1));
						
			// turtle constraint
			if (t==SpriteTemplate.GREEN_TURTLE || t==SpriteTemplate.RED_TURTLE) {				
				if (turtleCount < Constraints.turtels) {
					turtleCount++;
				}
				else {
					t = SpriteTemplate.GOOMPA;
				}
			}
			
			setSpriteTemplate(x, y - 1, new SpriteTemplate(t, winged));
		}
	}

	/**
	 * Utility for placing nice-looking pipes.
	 */
	public void placePipe(int x, int y, int height) {
		for (int i=1; i<height; i++) {
			setBlock(x, y - i, this.TUBE_SIDE_LEFT);				
			setBlock(x + 1, y - i, this.TUBE_SIDE_RIGHT);							
		}
		
		setBlock(x, y - height, this.TUBE_TOP_LEFT);				
		setBlock(x + 1, y - height, this.TUBE_TOP_RIGHT);				
	}
	
	/**
	 * Utility for placing nice-looking ground.
	 */
	public void setGroundHeight(int x, int y, int lastY, int nextY) {
		for (int i=y + 1; i<height; i++) {
			setBlock(x, i, this.HILL_FILL);		
		}

		if (y < lastY) {			
			setBlock(x, y, this.LEFT_UP_GRASS_EDGE);					
			
			for (int i=y + 1; i<lastY; i++) {
				setBlock(x, i, this.LEFT_GRASS_EDGE);		
			}
			
			setBlock(x, lastY, this.RIGHT_POCKET_GRASS);					
		}
		else if (y < nextY) {			
			setBlock(x, y, this.RIGHT_UP_GRASS_EDGE);					
			
			for (int i=y + 1; i<nextY; i++) {
				setBlock(x, i, this.RIGHT_GRASS_EDGE);		
			}
			
			setBlock(x, nextY, this.LEFT_POCKET_GRASS);					
		}
		else {
			setBlock(x, y, this.HILL_TOP);		
		}		
		
		// place the exit
		if (x == (width - 5)) {
			this.yExit = y;
		}
	}	
}