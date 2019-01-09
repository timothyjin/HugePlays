package entities;

import java.awt.*;
import game.*;

/**
 * A reloader that constantly moves around the screen at random x and y velocity. When a player touches 
 * a <code>MovingReloader</code>, the player's bullets are replenished to full capacity (half capacity
 * if the player is invincible).
 * 
 * @author Timothy Jin
 * @version v3.0 (2018)
 */
public class MovingReloader extends MovingRectangle {

	public static final Dimension DEFAULT_RELOADER_SIZE = new Dimension(10, 10);
//	private static final Dimension DEFAULT_RELOADER_SPEED = new Dimension(0, -3);
	private static final Color COLOR = Color.BLUE;
	
	public MovingReloader(boolean r, GamePanel p) {
		
		super(p.getReloaderSize().width, p.getReloaderSize().height, 0, -3, COLOR, p.getWrapAreas());
		if (r) {
			velX = 6 * Math.random() - 3;
			velY = 6 * Math.random() - 3;
		}
		x = screenSize.width / 2;
		y = screenSize.height / 2;    // start in middle of screen
	}
	
	public void move() {
		
		ricochet();
		super.move();
	}
}
