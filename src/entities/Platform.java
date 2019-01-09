package entities;

import java.awt.*;

/**
 * Platforms are the basic way for the player to get from one elevation to another.
 * When the player is rising in his jump, he is able to pass through platforms. However,
 * when falling, the player cannot phase down through the platform. When pressing the
 * drop button while standing on a platform, the player will be able to drop through
 * the platform.
 * 
 * @author Timothy Jin
 * @version v3.0 (2018)
 */
public class Platform extends RectangleEntity {
	
	private static final Color INITIAL_COLOR = Color.WHITE;
	
	public Platform(int x0, int y0, int w, int h, boolean center) {
		
		super(w, h, INITIAL_COLOR);
		
		if (center) {    // (x0, y0) is platform's center
			x = x0;
			y = y0;
		} else {    // (x0, y0) is platform's upper left corner
			x = x0 + (w / 2); 
			y = y0 + (h / 2);
		}
	}
	
	public void draw(Graphics g) {
		
		super.draw(g);
		g.setColor(Color.BLACK);
		g.drawRect(getRectX(), getRectY(), width, height);    // draw platform outline
	}
	
	public void setColor(Color c) { color = c; }
}
