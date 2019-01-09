package entities;

import java.awt.*;

public class WrapAreas {
	
	private static final int WRAP_DEPTH = 5;
	private static final int WRAP_EXTEND = 30;
	
	private static final int MIN_WRAP_HEIGHT = 50;
	private static final int MIN_WRAP_WIDTH = 100;

//--Default wrap areas (1200x900)
	public static final int DEFAULT_LEFT_UPPER = 0;
	public static final int	DEFAULT_LEFT_LOWER = 105;
	
	public static final int DEFAULT_RIGHT_UPPER = 790;
	public static final int DEFAULT_RIGHT_LOWER = 895;
	
	public static final int DEFAULT_TOP_LEFT = 420;
	public static final int DEFAULT_TOP_RIGHT = 780;
	
	public static final int DEFAULT_BOTTOM_LEFT = 420;
	public static final int DEFAULT_BOTTOM_RIGHT = 780;
	
	private static final Color COLOR = Color.RED;
	
//--Instance wrap areas
	private int leftUpper;
	private int leftLower;
	
	private int rightUpper;
	private int rightLower;
	
	private int topLeft;
	private int topRight;
	
	private int bottomLeft;
	private int bottomRight;
	
	private Dimension screenSize;
	
	public WrapAreas(Dimension s, boolean rand) {
		
		screenSize = s;
		
		if (rand) {
			initRandomWrapAreas();
		} else {
			initDefaultWrapAreas();
		}
	}
	
	private void initDefaultWrapAreas() {
		
		leftUpper = DEFAULT_LEFT_UPPER;
		leftLower = DEFAULT_LEFT_LOWER;
		rightUpper = DEFAULT_RIGHT_UPPER;
		rightLower = DEFAULT_RIGHT_LOWER;
		topLeft = DEFAULT_TOP_LEFT;
		topRight = DEFAULT_TOP_RIGHT;
		bottomLeft = DEFAULT_BOTTOM_LEFT;
		bottomRight = DEFAULT_BOTTOM_RIGHT;
	}
	
	private void initRandomWrapAreas() {
		
		leftUpper = (int) ((screenSize.height - MIN_WRAP_HEIGHT) * Math.random());
		leftLower = (int) ((screenSize.height - (leftUpper + MIN_WRAP_HEIGHT)) * Math.random() + (leftUpper + MIN_WRAP_HEIGHT));
		rightUpper = (int) ((screenSize.height - (leftLower - leftUpper)) * Math.random());
		rightLower = rightUpper + (leftLower - leftUpper);
		topLeft = (int) ((screenSize.width - MIN_WRAP_WIDTH) * Math.random());
		topRight = (int) ((screenSize.width - (topLeft + MIN_WRAP_WIDTH)) * Math.random() + (topLeft + MIN_WRAP_WIDTH));
		bottomLeft = (int) ((screenSize.width - (topRight - topLeft)) * Math.random());
		bottomRight = bottomLeft + (topRight - topLeft);
	}
	
	public int[][] getWrapCoordinates() {
		
		int[][] wc = {
				{0, leftUpper, WRAP_DEPTH, leftLower - leftUpper},
				{screenSize.width - WRAP_DEPTH, rightUpper, WRAP_DEPTH, rightLower - rightUpper},
				{topLeft, 0, topRight - topLeft, WRAP_DEPTH},
				{bottomLeft, screenSize.height - WRAP_DEPTH, bottomRight - bottomLeft, WRAP_DEPTH}};
		return wc;
	}
	
	public int getLeftUpper() { return leftUpper; }
	public int getLeftLower() { return leftLower; }
	public int getRightUpper() { return rightUpper; }
	public int getRightLower() { return rightLower; }
	public int getTopLeft() { return topLeft; }
	public int getTopRight() { return topRight; }
	public int getBottomLeft() { return bottomLeft; }
	public int getBottomRight() { return bottomRight; }
	
	public Dimension getScreenSize() { return screenSize; }
	
	public static int getWrapDepth() { return WRAP_DEPTH; }
	public static int getWrapExtend() { return WRAP_EXTEND; }
	public static Color getColor() { return COLOR; }
}
