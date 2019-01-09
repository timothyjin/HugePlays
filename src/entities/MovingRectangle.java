package entities;

import java.awt.*;

/**
 * Details the motion and wrapping behavior of its descendants.
 *  
 * @author Timothy Jin
 * @version v3.0 (2018)
 */
public abstract class MovingRectangle extends RectangleEntity {
	
	public double velX;
	public double velY;
	
	WrapAreas wrappers;
	Dimension screenSize;
	
	int minX;
	int maxX;
	
	int minY;
	int maxY;

	boolean wrapping;    // fix for Player sticking to top wrap area
	
	public MovingRectangle(int w, int h, double vx, double vy, Color c, WrapAreas wa) {
		
		super(w, h, c);
		
		velX = vx;
		velY = vy;
		
		wrappers = wa;
		screenSize = wa.getScreenSize();
		
		minX = width / 2;
		maxX = screenSize.width - minX;
		minY = height / 2;
		maxY = screenSize.height - minY;
		
		wrapping = false;
	}
	
	/**
	 * All <code>MovingRectangle</code> objects move at "velX" and "velY" pixels per tick.
	 * <p>
	 * An entity triggers a wrap when its midpoint exceeds the extremes of the defined boundaries. It is then teleported to the wrap area's corresponding
	 * "wrap partner," where its midpoint is placed on the inner edge of the partner.
	 * </p>
	 */
	public void move() {
		
		/* Basic motion--------------------------------------------------------------------------*/
		x += velX;
		y += velY;
		
		/* Wrapping------------------------------------------------------------------------------*/
		wrapping = false;
		if (getX() <= minX && getX() >= minX - WrapAreas.getWrapExtend() && getY() >= wrappers.getLeftUpper() && getY() <= wrappers.getLeftLower()) {    // left --> right
			x = maxX - WrapAreas.getWrapDepth();
			y = (y - wrappers.getLeftUpper()) + wrappers.getRightUpper();
			wrapping = true;
		}
		if (getX() >= maxX && getX() <= maxX + WrapAreas.getWrapExtend() && getY() >= wrappers.getRightUpper() && getY() <= wrappers.getRightLower()) {    // right --> left
			x = minX + WrapAreas.getWrapDepth();
			y = (y - wrappers.getRightUpper()) + wrappers.getLeftUpper();
			wrapping = true;
		}
		if (getY() <= minY && getY() >= minY - WrapAreas.getWrapExtend() && getX() >= wrappers.getTopLeft() && getX() <= wrappers.getTopRight()) {    // top --> bottom
			x += wrappers.getBottomLeft() - wrappers.getTopLeft();
			y = maxY - WrapAreas.getWrapDepth();
			wrapping = true;
		}
		if (getY() >= maxY && getY() <= maxY + WrapAreas.getWrapExtend() && getX() >= wrappers.getBottomLeft() && getX() <= wrappers.getBottomRight()) {    // bottom --> top
			x += wrappers.getTopLeft() - wrappers.getBottomLeft();
			y = minY + WrapAreas.getWrapDepth();
			wrapping = true;
		}
	}
	
	/**
	 * Method that details behavior for bouncing off walls.
	 */
	void ricochet() {
		
		if (getX() <= minX || getX() >= maxX) {
			velX *= -1;
		}
		if (getY() <= minY || getY() >= maxY) {
			velY *= -1;
		}
	}
	
	/**
	 * @return the integer value that is closest to the x-velocity of this <code>MovingRectangle</code>
	 */
	public int getVelX() { return (int) (Math.round(velX)); }	
	/**
	 * @return the integer value that is closest to the y-velocity of this <code>MovingRectangle</code>
	 */
	public int getVelY() { return (int) (Math.round(velY)); }
	
	public int getMinX() { return minX; }
	public int getMaxX() { return maxX; }	
	
	public int getMinY() { return minY; }
	public int getMaxY() { return maxY; }
}
