package entities;

import java.awt.*;
import game.*;

/**
 * An invincibility block renders a player "impervious" to all bullets on the screen for
 * a specified length of time. The model, movement, and activation of the invincibility
 * block is exactly like that of the MovingReloader. After a player's invincibility has
 * worn off, it experiences a random-length cooldown period before appearing again.
 * 
 * @author Timothy Jin
 * @version 3.0 (2018)
 */
public class Invincibility extends MovingRectangle {

	public static final Dimension DEFAULT_INVINCIBILITY_SIZE = new Dimension(10, 10);
	public static final long DEFAULT_LIFETIME = 10000;
	private static final Color COLOR = Color.GRAY;
	private static final int TICK_LIMIT = 10;
	
	private long waitStartTime;    // time at instantiation
	private long waitTime;    // time between waitStartTime and first appearance on screen
	private long useStartTime;    // time at which this first appears on screen
	private long lifeTime;    // maximum time a Player can be in invincible state
	
	private boolean visible;
	private boolean showMessage;
	private boolean activated;
	
	private boolean flashing;
	private long ticks; 
	
	public Invincibility(GamePanel p) {
		
		super(p.getInvincibilitySize().width, p.getInvincibilitySize().height, 0, 0, COLOR, p.getWrapAreas());
		lifeTime = p.getInvincibilityLifeTime();
		setup();
	}
	
	public void setup() {
		
		waitStartTime = System.currentTimeMillis();    // account for length of current use
		waitTime = (long)(30000 * Math.random() + 10000);    // [10000, 30000) sec wait time
//		waitTime = (long)(5000 * Math.random() + 5000);
		useStartTime = -1;

		visible = false;
		showMessage = true;
		activated = false;
		flashing = true;
		ticks = 0;
	}
	
	public void draw(Graphics g) {
		
//		if (++ticks % TICK_LIMIT == 0) {
//			if (flashing) {
//				g.setColor(Color.WHITE);
//				flashing = false;
//			} else {
//				g.setColor(Color.GRAY);
//				flashing = true;
//			}
//			g.fillRect(getRectX(), getRectY(), width, height);
//		}
		super.draw(g);
	}
	
	public void move() {
		
		ricochet();
		super.move();
	}
	
	/**
	 * Checks if invincibility block is not yet ready to be drawn on screen
	 */
	public boolean isWaiting() {
		
		return useStartTime < 0 && System.currentTimeMillis() - waitStartTime < waitTime;
	}
	
	/**
	 * Checks if invincibility block is ready to be drawn on screen
	 */
	public boolean isReady() {
	
		if (useStartTime < 0 && System.currentTimeMillis() - waitStartTime >= waitTime) {
			if (!visible) {
				x = (int)((maxX - minX) * Math.random() + minX);
				y = (int)((maxY - minY) * Math.random() + minY);
				velX = 6 * Math.random() - 3;
				velY = 6 * Math.random() - 3;
				visible = true;    // setup movement once
			}
			if (showMessage) {
				System.out.println("\t\t\tAn invincibility block has appeared!");
				showMessage = false;    // show message once
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Called to signify that a <code>Player</code> has touched this block
	 */
	public void activate() {
		
		useStartTime = System.currentTimeMillis();
		activated = false;
		x = Integer.MIN_VALUE;
		y = Integer.MIN_VALUE;
	}
	
	/**
	 * Checks if the invincible state has totally elapsed
	 */
	public boolean finishedActivatedState() {
		
		return useStartTime >= 0 && System.currentTimeMillis() - useStartTime > lifeTime;
	}
	
	public boolean isActivated() { return activated; }
	
	public long getWaitStartTime() { return waitStartTime; }
	public long getWaitTime() { return waitTime; }
	public long getUseStartTime() { return useStartTime; }
	public long getLifeTime() { return lifeTime; }
}
