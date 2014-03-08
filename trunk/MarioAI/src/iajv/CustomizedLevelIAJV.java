package iajv;

import java.util.ArrayList;
import java.util.Random;

import dk.itu.mario.MarioInterface.Constraints;
import dk.itu.mario.MarioInterface.GamePlay;
import dk.itu.mario.MarioInterface.LevelInterface;
import dk.itu.mario.engine.sprites.SpriteTemplate;
import dk.itu.mario.level.Level;

public class CustomizedLevelIAJV extends Level implements LevelInterface {
	
	private double CHANCE_GAP = 0.05;
	private double CHANCE_HILL = 0.03;
	private double CHANCE_END_HILL = 0.15;
	private double CHANCE_HEIGHT_CHANGE = 0.1;
	private double CHANCE_PIPE = 0.05;
	private double CHANCE_ENEMY = 0.25;
	private double CHANCE_WINGED = 0.05;
	private double CHANCE_BLOCK = 0.1;
	private double CHANCE_END_BLOCK = 0.1;
	private double CHANCE_COIN = 0.05;
	private double CHANCE_END_COIN = 0.25;
	private double CHANCE_BLOCK_POWER_UP = 0.1;
	private double CHANCE_BLOCK_COIN = 0.3;
	 
	private int GROUND_MAX_HEIGHT = 7;
	private int GROUND_MIN_LENGTH = 3;
	private int GROUND_OFFSET = 5;
	private int GAP_MAX_LENGTH = 5;
	private double GAP_OFFSET = 4;
	private double HILL_MAX_HEIGHT = 5;	 
	private double HILL_OFFSET = 6;	 
	private int HILL_MIN_LENGTH = 3;
	private int COIN_OFFSET = 3;
	private int BLOCK_OFFSET = 4;
	 
	private Random random;
	
	private int difficulty;
    private int type;
    
    private int gapCount;
    private int turtleCount;
    private int coinCount;
    
    private GamePlay playerM;
    
    private ArrayList<Integer> ground;
    private ArrayList<Integer> blocks;

    private int minX;
    private int maxX;
    private int lastY;
    private int lastLandY;
	private int length;
	
	private boolean justChanged;
    
	public CustomizedLevelIAJV(int width, int height, long seed, int difficulty, int type, GamePlay playerMetrics) {
		super(width, height);
		this.difficulty = difficulty;
		this.type = type;
		this.playerM = playerMetrics;
		
		random = new Random(seed);        
        
        configureParameters();
        
        // keeps track of the ground height
        ground = new ArrayList<Integer>();
        blocks = new ArrayList<Integer>();
		
        // select the starting ground height
        minX = 4;
        maxX = width - 16;
		length = 0;
        justChanged = false;

		createLevel();
	}
	
	public void configureParameters() {
		
	}
	
	public void createLevel() {
        buildPhase1();	// Placer des sols
        buildPhase2();	// Placer des sols supplementaires plus haut
        buildPhase3();	// Ajouter des tubes
        buildPhase4();	// Ajouter des bloques
        buildPhase5();	// Ajouter des enemies
        buildPhase6();	// Ajouter des pieces
        
        /*************************/
        /* LES CODES PAR DEFAULT */
        if (type == LevelInterface.TYPE_CASTLE || type == LevelInterface.TYPE_UNDERGROUND) {
            int ceiling = 0;
            int run = 0;
            for (int x = 0; x < width; x++) {
                if (run-- <= 0 && x > 4) {
                    ceiling = random.nextInt(4);
                    run = random.nextInt(4) + 4;
                }
                for (int y = 0; y < height; y++) {
                    if ((x > 4 && y <= ceiling) || x < 1) {
                        setBlock(x, y, GROUND);
                    }
                }
            }
        }
        
        fixWalls();
        /*************************/
	}
	
	/**
	 * Placer les sols
	 */
	private void buildPhase1() {
		int floor = getRandomHeight();
		lastLandY = floor;
		lastY = floor;
		// Debut du niveau
		for (int x = 0; x < minX; x++) {
			setHeightGround(x, floor);
			ground.add(floor);
		}
		
		for (int x = minX; x < maxX; x++) {		
			if (length >= GAP_MAX_LENGTH && floor >= height) {	// Fin d'un trou
				do {
					floor = getRandomHeight();
				} while (lastLandY - floor >= GAP_OFFSET);
				length = 1;
				justChanged = true;
			} else if (random.nextDouble() < CHANCE_HEIGHT_CHANGE & !justChanged) {  // Changer l'hauteur
				do {
					floor = getRandomHeight();
				} while (Math.abs(lastY - floor) >= GROUND_OFFSET);
				length = 1;
				justChanged = true;
			} else if (floor < height && random.nextDouble() < CHANCE_GAP &&  // Un nouveau trou
										!justChanged && gapCount < Constraints.gaps) { 
				floor = height;
				gapCount++;
				length = 1;
				justChanged = true;
			} else {	// Par default
				length++;
				if (length >= GROUND_MIN_LENGTH) {
					justChanged = false;
				}
			}
			
			setHeightGround(x, floor);
			ground.add(floor);	
			lastY = floor;
			if (floor < height) 	lastLandY = floor;
		}

		// Fin du niveau
		for (int x = maxX; x < width; x++) {
			if (floor == height)	floor = height - 2;
			setHeightGround(x, floor);
			ground.add(floor);
		}
		
		xExit = width - 5;
        yExit = floor;
	}
	
	/**
	 * Placer les sols supplementaires plus haut
	 */
	private void buildPhase2() {
		int y = height;
		int lastFloor;
		int floor = height;
		int length = 0;
		for (int x = minX + 5; x < maxX; x++) {
			lastFloor = floor;
			floor = ground.get(x);
			if (length == 0) {				
				if (floor != height && lastFloor != height && random.nextDouble() < CHANCE_HILL) {  // Une colline
					do {
						y = (int)(floor - 1 - random.nextDouble() * Math.abs(floor - HILL_MAX_HEIGHT));
					} while (floor - y >= HILL_OFFSET);
					
					setBlock(x, y, Level.HILL_TOP_LEFT);		
					
					for (int i = y + 1; i < floor; i++) {
						setBlock(x, i, Level.HILL_LEFT);		
					}
					
					length = 1;
				}
			} else if (y >= floor) {	// Fin de la colline si il y a un mur 
				length = 0;
			} else { 					// Fin de la colline					
				if ((length >= HILL_MIN_LENGTH && random.nextDouble() < CHANCE_END_HILL)
						|| (random.nextDouble() < CHANCE_END_HILL * 2 && floor == height)
						|| x == maxX - 1) {
					setBlock(x, y, Level.HILL_TOP_RIGHT);		
					
					for (int i = y + 1; i < floor; i++) {
						setBlock(x, i, Level.HILL_RIGHT);		
					}
					
					length = 0;
				} else {		// la colline continue
					setBlock(x, y, Level.HILL_TOP);		
					
					for (int i = y + 1; i < floor; i++) {
						setBlock(x, i, Level.HILL_FILL);		
					}
					
					length++;
				}
			}
		}
	}
	
	/**
	 * Ajouter les tubes
	 */
	private void buildPhase3() {
		int floor;
		boolean isTube = false;
		// int high = 0; ??? Tube avec different hauteur
		for (int x = minX + 5; x < maxX; x++) {
			floor = ground.get(x);			

			if (isTube) {
				setBlock(x, floor - 2, Level.TUBE_TOP_RIGHT);	
				setBlock(x, floor - 1, Level.TUBE_SIDE_RIGHT);				
				isTube = false;
			} else if (x != maxX - 1 && floor == ground.get(x + 1) && floor == ground.get(x - 1) && floor != height) {
				if (!isTube && random.nextDouble() < CHANCE_PIPE) {
					setBlock(x, floor - 2, Level.TUBE_TOP_LEFT);
					setBlock(x, floor - 1, Level.TUBE_SIDE_LEFT);		
					isTube = true;
				}				
			} 
		}
	}
	
	/**
	 * Ajouter des bloques
	 */
	private void buildPhase4() {
		int floor;
		int max;
		int y = height;
		for (int x = minX + 5; x < maxX; x++) {
			floor = ground.get(x);
			max = 0; 
			
			for (max = 0; max < floor; max++) {		// Trouver l'objet le plus haut
				int tile = getBlock(x, max);
				if (tile != 0) {
					break;
				}				
			}
			
			if (y == height) {						
				if (x > minX && random.nextDouble() < CHANCE_BLOCK) { 		// Commencer 
					y  = max - BLOCK_OFFSET;
					
					if (floor - max > 1) 		placeBlock(x, y);
					else						y = height;
				}
			} else {
				if (y >= max - 1 || random.nextDouble() < CHANCE_END_BLOCK) 		y = height;
				else																placeBlock(x, y);
			}
		}
	}
	
	/**
	 * Ajouter des enemies
	 */
	private void buildPhase5() {
		int floor;
		int max;
		for (int x = minX + 5; x < maxX; x++) {
			floor = ground.get(x);
			
			if (random.nextDouble() < CHANCE_ENEMY) {
				max = 0;			
				for (max = 0; max < floor; max++) {		// Trouver l'objet le plus haut et placer les enemies sur eux
					int tile = getBlock(x, max);
					if (tile != 0) {
						break;
					}				
				}
				
				boolean winged = random.nextDouble() < CHANCE_WINGED;
				int t = (int)(random.nextInt(6));

				if (t == SpriteTemplate.GREEN_TURTLE || t == SpriteTemplate.RED_TURTLE) {				
					if (turtleCount < Constraints.turtels) {
						turtleCount++;
					} else {
						t = SpriteTemplate.GOOMPA;
					}
				} else if (t == SpriteTemplate.ARMORED_TURTLE) {
					if (random.nextInt(2) == 1)		t = SpriteTemplate.GOOMPA;
				} else {
					t = SpriteTemplate.GOOMPA;
				}
				
				if (getBlock(x, max - 1) == 0) {
					setSpriteTemplate(x, max - 1, new SpriteTemplate(t, winged));
				}
			}	
		}
	}
		
	/**
	 * Ajouter des pieces
	 */
	private void buildPhase6() {
		int floor;
		int y = height;
		boolean isCoin = false;
		for (int x = minX + 5; x < maxX; x++) {
			floor = ground.get(x);
			
			if (!isCoin && floor != height && random.nextDouble() < CHANCE_COIN) {  	// Placer des pieces
				y = floor - 2 - random.nextInt(COIN_OFFSET);
				isCoin = true;
				
				if (getBlock(x, y) == 0) {
					setBlock(x, y, Level.COIN);		
				} else {
					isCoin = false;
				}
			} else if (isCoin) {
				if (getBlock(x, y) == 0) {
					setBlock(x, y, Level.COIN);		
					if (random.nextDouble() < CHANCE_END_COIN) 	isCoin = false;
				} else {
					isCoin = false;
				}
			}
		}
	}
	
	public void placeBlock(int x, int y) {
		if (random.nextDouble() < CHANCE_BLOCK_POWER_UP) {
			setBlock(x, y, Level.BLOCK_POWERUP);		
		} else if (random.nextDouble() < CHANCE_BLOCK_COIN && coinCount < Constraints.coinBlocks) {
			setBlock(x, y, Level.BLOCK_COIN);					
			coinCount++;
		} else {
			setBlock(x, y, Level.BLOCK_EMPTY);		
		}
	}
	
	
	private int getRandomHeight() {
		return GROUND_MAX_HEIGHT + (int)(random.nextDouble() * (height - 1 - GROUND_MAX_HEIGHT));
	}
	
	private void setHeightGround(int x, int floor) {
		for (int y = 0; y < height; y++) { //paint ground up until the floor
            if (y >= floor) {
                setBlock(x, y, GROUND);
            }
		}
	}
	
	/**
	 * Generer les graphiques à partir de la carte qu'on a généré
	 */
	private void fixWalls() {
        boolean[][] blockMap = new boolean[width + 1][height + 1];

        for (int x = 0; x < width + 1; x++) {
            for (int y = 0; y < height + 1; y++) {
                int blocks = 0;
                for (int xx = x - 1; xx < x + 1; xx++) {
                    for (int yy = y - 1; yy < y + 1; yy++) {
                        if (getBlockCapped(xx, yy) == GROUND) {
                            blocks++;
                        }
                    }
                }
                blockMap[x][y] = blocks == 4;
            }
        }
        blockify(this, blockMap, width + 1, height + 1);
    }

	/**
	 * Generer les graphiques à partir de la carte qu'on a généré
	 */
    private void blockify(Level level, boolean[][] blocks, int width,
                          int height) {
        int to = 0;
        if (type == LevelInterface.TYPE_CASTLE) {
            to = 4 * 2;
        } else if (type == LevelInterface.TYPE_UNDERGROUND) {
            to = 4 * 3;
        }

        boolean[][] b = new boolean[2][2];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int xx = x; xx <= x + 1; xx++) {
                    for (int yy = y; yy <= y + 1; yy++) {
                        int _xx = xx;
                        int _yy = yy;
                        if (_xx < 0) _xx = 0;
                        if (_yy < 0) _yy = 0;
                        if (_xx > width - 1) _xx = width - 1;
                        if (_yy > height - 1) _yy = height - 1;
                        b[xx - x][yy - y] = blocks[_xx][_yy];
                    }
                }

                if (b[0][0] == b[1][0] && b[0][1] == b[1][1]) {
                    if (b[0][0] == b[0][1]) {
                        if (b[0][0]) {
                            level.setBlock(x, y, (byte) (1 + 9 * 16 + to));
                        } else {
                            // KEEP OLD BLOCK!
                        }
                    } else {
                        if (b[0][0]) {
                            //down grass top?
                            level.setBlock(x, y, (byte) (1 + 10 * 16 + to));
                        } else {
                            //up grass top
                            level.setBlock(x, y, (byte) (1 + 8 * 16 + to));
                        }
                    }
                } else if (b[0][0] == b[0][1] && b[1][0] == b[1][1]) {
                    if (b[0][0]) {
                        //right grass top
                        level.setBlock(x, y, (byte) (2 + 9 * 16 + to));
                    } else {
                        //left grass top
                        level.setBlock(x, y, (byte) (0 + 9 * 16 + to));
                    }
                } else if (b[0][0] == b[1][1] && b[0][1] == b[1][0]) {
                    level.setBlock(x, y, (byte) (1 + 9 * 16 + to));
                } else if (b[0][0] == b[1][0]) {
                    if (b[0][0]) {
                        if (b[0][1]) {
                            level.setBlock(x, y, (byte) (3 + 10 * 16 + to));
                        } else {
                            level.setBlock(x, y, (byte) (3 + 11 * 16 + to));
                        }
                    } else {
                        if (b[0][1]) {
                            //right up grass top
                            level.setBlock(x, y, (byte) (2 + 8 * 16 + to));
                        } else {
                            //left up grass top
                            level.setBlock(x, y, (byte) (0 + 8 * 16 + to));
                        }
                    }
                } else if (b[0][1] == b[1][1]) {
                    if (b[0][1]) {
                        if (b[0][0]) {
                            //left pocket grass
                            level.setBlock(x, y, (byte) (3 + 9 * 16 + to));
                        } else {
                            //right pocket grass
                            level.setBlock(x, y, (byte) (3 + 8 * 16 + to));
                        }
                    } else {
                        if (b[0][0]) {
                            level.setBlock(x, y, (byte) (2 + 10 * 16 + to));
                        } else {
                            level.setBlock(x, y, (byte) (0 + 10 * 16 + to));
                        }
                    }
                } else {
                    level.setBlock(x, y, (byte) (0 + 1 * 16 + to));
                }
            }
        }
    }
}
