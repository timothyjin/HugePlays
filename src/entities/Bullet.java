package entities;

import java.awt.*;
import game.*;

/**
 * Bullets are projectiles that kill when touched by players. Players can shoot bullets by
 * pressing the shoot button (obviously). A player can only shoot one bullet at a
 * time, in a semi-automatic fashion. Bullets can wrap around to other parts of the screen
 * the same way players can. Bullets also have a specified lifetime starting from when
 * they are first shot; they will cease to exist afterwards.
 * 
 * @author Timothy Jin
 * @version v3.0 (2018)
 */
public class Bullet extends MovingRectangle {
	
	public static final Dimension DEFAULT_BULLET_SIZE = new Dimension(12, 6);
	public static final double DEFAULT_BULLET_SPEED = 8;
	public static final long DEFAULT_LIFETIME = 5000;
	
	private boolean ricochet;
	
	private long startTime;	
	private long lifeTime;
	private int number;
	
	public Bullet(int x0, int y0, int d, int vx, int vy, int n, Color c, GamePanel p) {
		
		super(p.getBulletSize().width, p.getBulletSize().height, 0, vy, c, p.getWrapAreas());
		
		x = x0;
		y = y0;
		if (d == 'r') {
			velX = p.getBulletSpeed() + vx;
		} else {
			velX = (-1 * p.getBulletSpeed()) + vx;
		}

		ricochet = p.useRicochet();
		
		startTime = System.currentTimeMillis();
		lifeTime = p.getBulletLifeTime();
		number = n;
	}
	
	public void move() {
		
		if (ricochet) {
			ricochet();    // recommended to limit number of bullets allowed on screen in GamePanel
		}
		super.move();
	}
	
	public boolean isAlive() {
		
		return System.currentTimeMillis() - startTime > lifeTime;
	}
	
	/**
	 * Determines if this <code>Bullet</code> is still on screen.
	 * This method is very similar to the <code>isTouching</code> parent method 
	 * 
	 * @return true if <code>Bullet</code> is on the screen, otherwise false
	 */
	public boolean inBounds() { 
		
		return !wrapping ||
				!(getRectY() > screenSize.height ||
						getRectY() + getHeight() < 0 ||
						getRectX() > screenSize.width ||
						getRectX() + getWidth() < 0);
	}
	
	public long getStartTime() { return startTime; }
	public long getLifeTime() { return lifeTime; }
	public int getNumber() { return number; }
}
