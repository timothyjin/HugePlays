package entities;

import java.awt.*;
import game.*;

/**
 * A subclass of Player that is controlled by the computer, rather than by key input. An AIPlayer
 * moves and shoots in random directions every specified number of ticks, but can exhibit more
 * predictable and definite movement under certain circumstances (e.g. player has no more bullets,
 * near an enemy player, close to the side of the screen).
 * 
 * @author Timothy Jin
 * @version 3.0 (2018)
 */
public class AIPlayer extends Player {
	
	private static final int DEFAULT_MTL = 30;	
	private static final int DEFAULT_STL = 24;
	private static final int FAST_STL = 12;

	private long ticks;
	private int moveTickLimit;    // controls AI's unpredictability of movement
	private int shootTickLimit;    // controls AI's rate of fire
	
//	public int left, right; // for debugging
	
	public AIPlayer(int x0, int y0, char d, int n, Color c, GamePanel p) {
		
		super(x0, y0, d, n, Player.CONTROLS_AI, c, p);
		ticks = 0;
		moveTickLimit = DEFAULT_MTL;
		shootTickLimit = DEFAULT_STL;
		
//		left = 0;
//		right = 0;
	}

	public void move() {
		
		if (useInvincibility && invBlock.isReady()) {    // move towards invincibility block if it exists
			pressed.clear();
			ticks++;
			if (ticks % shootTickLimit == 0) {
				shoot();
			}
			if (getX() < invBlock.getX()) {
				pressed.add(new Integer(3));
			}
			if (getX() > invBlock.getX()) {
				pressed.add(new Integer(2));
			}
			if (getY() < invBlock.getY()) {
				pressed.add(new Integer(1));
			}
			if (getY() > invBlock.getY()) {
				pressed.add(new Integer(0));
			}
		} else if (isClipEmpty()) {    // move towards reloader if clip is empty
			pressed.clear();
			if (getX() < reloader.getX()) {
				pressed.add(new Integer(3));
			}
			if (getX() > reloader.getX()) {
				pressed.add(new Integer(2));
			}
			if (getY() < reloader.getY()) {
				pressed.add(new Integer(1));
			}
			if (getY() > reloader.getY()) {
				pressed.add(new Integer(0));
			}
		} else {    // normal movement
			ticks++;
			if (ticks % moveTickLimit == 0) {
				pressed.clear();
				horizMove();
			}
			pressed.remove(new Integer(4));
			if (ticks % shootTickLimit == 0) {
				shoot();
			}
			if (isGrounded()) {
				vertMove();
			}
		}
		super.move();
	}
	
	private void shoot() {
		
		shooting = false;
		canShoot = true;
		pressed.add(new Integer(4));
	}
	
	/*
	 * Controls random vertical movement of the AI player. There is an equal chance that the
	 * AIPlayer will jump, drop through, or stay on its platform.
	 */
	private void vertMove() {
		
		double d = Math.random();		
		if (d < 0.33) {
			pressed.add(new Integer(0));
		} else if (d < 0.66) {
			pressed.add(new Integer(1));
		}
	}
	
	protected void drop() {
		
		if (!hasBottom && y > maxY - 150) {
			return;
		}
		super.drop();
	}
	
	/*
	 * Controls location-dependent random (sort of) horizontal movement of the AI player.
	 * If the AI is close to another player, it will shoot faster in the direction of that player.
	 * Otherwise, if the AI is far to one side, it is more likely to move towards the opposite side.
	 */
	private void horizMove() {
		
		for (Player p : players) {    // shoot faster and change direction when close to another player
			if (p == this) {
				continue;    // skip itself in the player list
			}
			int xDiff = getX() - p.getX();
			int yDiff = getY() - p.getY();
			if (xDiff < 0 && xDiff > -200 && yDiff < 0 && yDiff > -200) {
				pressed.add(new Integer(3));
				pressed.add(new Integer(1));
				shootTickLimit = FAST_STL;
			} else if (xDiff > 0 && xDiff < 200 && yDiff < 0 && yDiff > -200) {
				pressed.add(new Integer(2));
				pressed.add(new Integer(1));
				shootTickLimit = FAST_STL;
			} else if (xDiff < 0 && xDiff > -200 && yDiff > 0 && yDiff < 200) {
				pressed.add(new Integer(3));
				pressed.add(new Integer(0));
				shootTickLimit = FAST_STL;
			} else if (xDiff > 0 && xDiff < 200 && yDiff > 0 && yDiff < 200) {
				pressed.add(new Integer(2));
				pressed.add(new Integer(0));
				shootTickLimit = FAST_STL;
			} else {
				shootTickLimit = DEFAULT_STL;
				double d = 2 * Math.random() * getX(); // [0, 2400)
				if (d < 600) {    // use 600 for equal left and right moves, <600 for more use of the whole screen
					pressed.add(new Integer(3));
//					right++;
				} else {
					pressed.add(new Integer(2));
//					left++;
				}
			}
		}
	}
}
