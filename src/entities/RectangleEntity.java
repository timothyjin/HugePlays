package entities;

import java.awt.*;

/**
 * Details dimensions and collision-detection logic for every entity in the game. Every entity is descended from this object.
 * 
 * @author Timothy Jin
 * @version 3.0 (2018)
 */
public abstract class RectangleEntity {

	public double x;
	public double y;
	
	int width;
	int height;
	
	Color color;
	
	public RectangleEntity(int w, int h, Color c) {
		
		width = w;
		height = h;
		color = c;
	}
	
	/**
	 * All entities must be drawn on the screen at some point.
	 * 
	 * @param g - <code>Graphics</code> object needed to draw the <code>Rectangle</code> on screen
	 */
	public void draw(Graphics g) {
		
		g.setColor(color);
		g.fillRect(getRectX(), getRectY(), width, height);
	}
	
	
	/**
	 * @param r - the <code>Rectangle</code> that is being tested with this object
	 * @return	true if this and r are touching, otherwise false
	 */
	boolean isTouching(RectangleEntity r) {
		
		return !(getRectY() > r.getRectY() + r.getHeight() ||
				getRectY() + getHeight() < r.getRectY() ||
				getRectX() > r.getRectX() + r.getWidth() ||
				getRectX() + getWidth() < r.getRectX());
	}
	
	/**
	 * @return the integer value that is closest to the x-position of the middle of this <code>RectangleEntity</code>
	 */
	public int getX() { return (int) Math.round(x); }	
	/**
	 * @return the integer value that is closest to the y-position of the middle of this <code>RectangleEntity</code>
	 */
	public int getY() { return (int) Math.round(y); }
	
	/**
	 * @return the integer value that is closest to the x-position of the top-left corner of this <code>RectangleEntity</code>
	 */
	public int getRectX() { return (int) (Math.round(x - (width / 2))); }
	/**
	 * @return the integer value that is closest to the y-position of the top-left corner of this <code>RectangleEntity</code>
	 */
	public int getRectY() { return (int) (Math.round(y - (height / 2))); }
	
	public int getWidth() { return width; }
	public int getHeight() { return height; }	
	
	public Color getColor() { return color; }
}

