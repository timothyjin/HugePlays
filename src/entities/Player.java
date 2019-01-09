package entities;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import game.*;

/**
 * Contains all player information and actions, along with KeyListener functionality.
 * 
 * <p>
 * There are six (6) places that need to be uncommented to enable momentum (not recommended
 * because there are weird interactions with the walls).
 * </p>
 * 
 * @author Timothy Jin
 * @version 3.0 (2018)
 */
public class Player extends MovingRectangle implements KeyListener {

//--Default values
	public static final Dimension DEFAULT_PLAYER_SIZE = new Dimension(30, 30);
	public static final double DEFAULT_PLAYER_SPEED = 4;
	public static final int DEFAULT_MAX_AMMO = 20;
	public static final double DEFAULT_GRAVITY = 0.32;
	public static final int DEFAULT_JUMP_VELOCITY = 12;
	
//--Player colors
	public final static Color P1_COLOR = Color.ORANGE;
	public final static Color P2_COLOR  = Color.MAGENTA;
	public final static Color P3_COLOR = Color.CYAN;
	
//--Player control schemes
	public final static int[] CONTROLS_1 = {KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SHIFT};
	public final static int[] CONTROLS_2 = {KeyEvent.VK_I, KeyEvent.VK_K, KeyEvent.VK_J, KeyEvent.VK_L, KeyEvent.VK_N};
	public final static int[] CONTROLS_3 = {KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_CONTROL};
	public final static int[] CONTROLS_AI = {0, 1, 2, 3, 4};
	
//--Player default spawns
	public final static Point P1_SPAWN = new Point(50, 880);
	public final static Point P2_SPAWN = new Point(1150, 90);
	public final static Point P3_SPAWN = new Point(600, 585);
	
//--Player state variables
	protected char direction;
	private boolean grounded;
	protected boolean shooting;
	protected boolean canShoot;
	private boolean reloading;
	private boolean invincible;
	protected boolean respawning;
	
//--Player information
	private double speed;
	private int kills;
	private int deaths;
	private int suicides;
	private int ammo;
	private int maxAmmo;
	private int totalShots;
	private int jumpVelocity;
	private int number;
	private int[] controls;    // {up, down, left, right, shoot}
	private double dAlpha;
	
//--Tracking position variables
	private double initX;
	private double initY;
	private char initDirection;
	private double lastX;
	private double lastY;    // used for adding line to respawn position

//--Tracking action variables
	private int ticksSinceKill;
	private int ticksSinceDeath;

//--Environment settings
	private boolean random;
	protected boolean hasBottom;
	protected boolean useInvincibility;
	private double gravity;
	
//--Environment entities
//	protected WrapAreas wrappers;
	protected LinkedList<Platform> platforms;
	private Platform current;
	protected MovingReloader reloader;
	protected Invincibility invBlock;
	protected LinkedList<Bullet> bullets;
	protected ArrayList<Player> players;
	
	private GamePanel panel;
	
	public Player(int x0, int y0, char d, int n, int[] ct, Color c, GamePanel p) {	
		
		super(p.getPlayerSize().width, p.getPlayerSize().height, 0, 0, c, p.getWrapAreas());
		
		x = x0;
		y = y0;
		direction = d;
		
		random = p.isRandom();	
		hasBottom = p.hasBottom();
		useInvincibility = p.useInvincibility();
			
		if (random && !hasBottom) {
			randomSafeSpawn();
		}
		if (!random) {
			initX = x0;
			initY = y0;
			initDirection = d;
		}	
		if (hasBottom) {
			maxY -= 5;    // account for bottom platform thickness
		}
		
		shooting = false;
		canShoot = true;
		reloading = false;
		invincible = false;
		respawning = false;
		
		speed = p.getPlayerSpeed();
		kills = 0;
		deaths = 0;
		suicides = 0;
		ammo = p.getMaxAmmo();
		maxAmmo = p.getMaxAmmo();
		totalShots = 0;	
		jumpVelocity = p.getJumpVelocity();
		number = n;
		controls = ct;	
		dAlpha = 255 / ammo;
		
		ticksSinceKill = -1;
		ticksSinceDeath = -1;
		
		gravity = p.getGravity();
		platforms = p.getPlatforms();
		initCurrent();
		reloader = p.getReloader();
		invBlock = p.getInvincbility();
		bullets = p.getBullets();
		players = p.getPlayers();
		
		panel = p;
	}
	
	public void draw(Graphics g) {
		
		/* fill player color---------------------------------------------------------------------*/
		if (reloading) {
			g.setColor(reloader.getColor());    // blue when reloading 
		} else if (shooting) {
			g.setColor(new Color(0, 255, 0, (int)(ammo * dAlpha)));    // green when shooting
		} else if (invincible) {
			g.setColor(invBlock.getColor());   // gray when reloading
		} else {
			g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(ammo * dAlpha)));
		}
		g.fillRect(getRectX(), getRectY(), width, height);
		
		/* draw player outline-------------------------------------------------------------------*/
		g.setColor(color);
		g.drawRect(getRectX(), getRectY(), width, height);
		
		/* draw player gun: (1/15?) -------------------------------------------------------------*/
		int gunThickness = height / 15;		
		g.setColor(Color.WHITE);
		if (direction == 'r') {
			g.fillRect(getX(), getY() - (gunThickness / 2), (width / 2) + gunThickness, gunThickness);
		} else {
			g.fillRect(getX() - (width / 2) - gunThickness, getY() - (gunThickness / 2), (width / 2) + gunThickness, gunThickness);
		}
		
		/* draw kill and ammo counts: (2/5?) ----------------------------------------------------*/
		g.setFont(new Font("Arial", Font.BOLD, (int)(0.4 * height)));
		if (ammo > maxAmmo / 2) {
			g.setColor(Color.BLACK);
		} else {
			g.setColor(color);
		}
		if (!isClipEmpty()) {
			g.drawString(new Integer(getBullets()).toString(), getRectX() + 2, getRectY() + height - 1);    // draw bullets left
		}
		
		/* draw respawn line---------------------------------------------------------------------*/
		if (respawning) {
			respawning = false;
			g.setColor(color);
			g.drawLine(getLastX(), getLastY(), getX(), getY());
		}
	}
	
	public void move() {
		
		checkState();		
		processKeys();	
		super.move();		
		checkBoundaries();	
		if (isDead()) {
			respawn();
		}
		
		if (ticksSinceKill >= 0) {
			ticksSinceKill++;
		}
		if (ticksSinceDeath >= 0) {
			ticksSinceDeath++;
		}
	}
	
	private void checkState() {
		
		/* falling and landing-------------------------------------------------------------------*/
		grounded = isGrounded();
		// general non-grounded falling
		if (!grounded && (getY() < maxY || (x > wrappers.getBottomLeft() && x < wrappers.getBottomRight()) || !hasBottom)) {
			accelerateDown();
		}
		// falling off a platform without jumping or dropping
		if (grounded && (x < current.getRectX() || x > current.getRectX() + current.getWidth())) {
			accelerateDown();
		}		
		if (isFalling()) {
			land();
		}
		
		/* reloading-----------------------------------------------------------------------------*/
		if (isTouching(reloader)) {
			reload();
			reloading = true;
		} else {
			reloading = false;
		}
		
		/* invincibility-------------------------------------------------------------------------*/
		if (useInvincibility) {
			if (invincible) {
				if (invBlock.finishedActivatedState()) {
					invincible = false;
					reload();
					System.out.println("\t\t\tPlayer " + number + "'s invincibility has worn off!");
				}
			}
			
			if (isTouching(invBlock) && !invincible) {
				invincible = true;
				invBlock.activate();
				if (ammo > maxAmmo / 2) {
					ammo = maxAmmo / 2;
				}
				System.out.println("\t\t\tPlayer " + number + " is now invincible!");
			}
		}
	}
	
	/**
	 * Makes the player accelerate down as if due to gravity.
	 * This method is only called if the player is airborne.
	 */
	private void accelerateDown() { velY += gravity; }
	
	/**
	 * Lands the player on a platform if he is close enough.
	 * (Took a HUGE/LARGE/SUBSTANTIAL amount of time because I'm stupid)
	 */
	private void land() {
		
		Platform land = platforms.getFirst();
		boolean landing = false;
		double difference;
		double closestDifference = Double.MAX_VALUE;    // SKETCHY
		double angle;
		double dX;
		int nextX = 0;	
		
		for (Platform p : platforms) {
			difference = p.getRectY() - getLowerY();
			if (difference >= 0 && difference <= velY) {    // player must be above the platform and within velY units to land
				angle = Math.atan(velX / velY);
				dX = difference * Math.tan(angle);
				nextX = getX() + (int) dX;    // find landing point on platform
				if (nextX >= p.getRectX() && nextX <= p.getRectX() + p.getWidth() && difference < closestDifference) {    // landing point is on platform and this platform is closest to player
					land = p;
					closestDifference = difference;
					landing = true;
				}
			}
		}
		if (landing) {
			current = land;
			velX = 0;
			velY = 0;
			x = nextX;
			y = land.getRectY() - (height / 2);
			land.setColor(color);
		}
	}
	
	/**
	 * Links the KeyEvents with their corresponding actions.
	 */
	private void processKeys() {
		
		/* movement------------------------------------------------------------------------------*/
		if (grounded) {
			if (pressed.contains(controls[0]) && !pressed.contains(controls[1])) {
				jump();
			} else if (pressed.contains(controls[1]) && !pressed.contains(controls[0])) {
				drop();
			}
			
			if (pressed.contains(controls[2]) && !pressed.contains(controls[3])) {
				moveLeft();
			} else if (pressed.contains(controls[3]) && !pressed.contains(controls[2])) {
				moveRight();
			} else {
				velX = 0;
			}
		} else {
			if (pressed.contains(controls[2]) && !pressed.contains(controls[3])) {
				moveLeft();
			} else if (pressed.contains(controls[3]) && !pressed.contains(controls[2])) {
				moveRight();
			}
		}
		
		/* shooting------------------------------------------------------------------------------*/
		if (pressed.contains(controls[4]) && canShoot && !isClipEmpty()) {    // only allows semi-automatic shooting
			shooting = true;
			shoot();
			totalShots++;
			if (!grounded) {
				recoil();
			}
		} else if (!pressed.contains(controls[4])) {
			shooting = false;
			canShoot = true;    // player can only shoot when he stops shooting
		}
	}
	
	/**
	 * Makes the player jump when called.
	 * This method can only be called the player is not airborne.
	 */
	private void jump() { velY -= jumpVelocity; }
	
	/**
	 * Makes the player drop through the platform on which he is currently standing.
	 * This method can only be called if the player is not airborne.
	 */
	protected void drop() {
		
		if (getLowerY() >= maxY && hasBottom) {
			velY -= 1;
		} else {
			velY += 1;
		}
	}
	
	private void moveLeft() {
		
		direction = 'l';		
		velX = -1 * speed;
	}
	
	private void moveRight() {
		
		direction = 'r';		
		velX = speed;
	}
	
	private void shoot() {
		
		int startX;
		if (direction == 'r') {
			startX = getRectX() + width + panel.getBulletSize().width;
		} else {
			startX = getRectX() - panel.getBulletSize().width;
		}
		bullets.add(new Bullet(startX, getY(), direction, getVelX(), getVelY(), number, color, panel));
		
		ammo--;
		canShoot = false;
	}
	
	/**
	 * Reloads the player's gun to full capacity, unless the player is invincible (half).
	 */
	public void reload() {
		
		if (invincible) {
			ammo = maxAmmo / 2;
		} else {
			ammo = maxAmmo;
		}
	}
	
	private void recoil() {
		
		if (direction == 'r') {
			velX -= 2;
		} else {
			velX += 2;
		}
	}
	
	private void checkBoundaries() {
		
		/* dealing with minX and maxX------------------------------------------------------------*/
		if (getX() < minX) {
			x = minX;
			velX = 0;
		}
		if (getX() > maxX) {
			x = maxX;
			velX = 0;
		}
		
		/* dealing with minY and maxY------------------------------------------------------------*/
		if (getY() < minY && velY < 0) {
			velY *= -0.75;    // bounce off the top of the screen inelastically
		}
		if (getY() > maxY && hasBottom) {
			y = maxY;
		}
	}
	
	/**
	 * @return true if the player is shot (touching a bullet) or falling too fast, otherwise false
	 */
	public boolean isDead() {
		
		boolean dead = false;
		Iterator<Bullet> it = bullets.iterator();
		
		while (it.hasNext()) {
			Bullet b = it.next();
			if (isTouching(b)) {
				if (invincible)	{    // remove bullet if player is shot while invincible
					it.remove();
					continue;
				}
				if (b.getNumber() == number) {    // player shoots himself
					kills--;
					suicides++;
					System.out.print("Player " + number + " suicided\t\t");
				} else {
					Player killer = players.get(b.getNumber() - 1);
					killer.addKill();
					killer.reload();
					killer.startKillTickCount();
					if (ammo < maxAmmo / 2) {
						ammo = maxAmmo / 2;    // player's bullets are half-replenished if he dies (not suicide)
					}				
					System.out.print("Player " + b.getNumber() + " --> Player " + number + "\t\t");
				}
				it.remove();    // delete bullet after it hits
				lastX = x; 
				lastY = y;
				dead = true;
				respawning = true;
				break;
			}
		}
		if (velY > 60) {    // player "falls to his death" [100]
			dead = true;
			kills--;
			suicides++;
			System.out.print("Player " + number + " fell to their death\t");
		}
		if (dead) {
			deaths++;
			startDeathTickCount();
			System.out.print("(");
			for (int n = 0; n < players.size(); n++) {
				Player p = players.get(n);
				System.out.print(p.getKills());
				if (n != players.size() - 1) {
					System.out.print(" | ");
				}
			}
			System.out.println(")");
		}
		return dead;
	}
	
	/**
	 * @param i - the invincibility block that is currently in the environment
	 */
//	public void updateInvBlock(Invincibility i) { invBlock = i; }
	
	/**
	 * Places the player in their assigned spawn location, or in a random location, after he dies.
	 */
	public void respawn() {
		
		if (random) {
			if (hasBottom) {
				x = (maxX - minX) * Math.random() + minX;
				y = (maxY - minY) * Math.random() + minY;
			} else {
				randomSafeSpawn();
			}
			if (Math.random() < 0.5) {
				direction = 'r';
			} else {
				direction = 'l';
			}
		} else {
			x = initX;
			y = initY;
			direction = initDirection;
		}
		velX = 0;
		velY = 0;
		initCurrent();
	}
	
	private void randomSafeSpawn() {
		
		double d = Math.random();
		if (d < 0.33) {    // respawn over left wrapper platform
			x = ((maxX - minX) / 4) * Math.random() + minX;
			y = (wrappers.getLeftUpper() - minY) * Math.random() + minY;
		} else if (d < 0.66) {    // respawn over right wrapper platform
			x = ((maxX - minX) / 4) * Math.random() + (maxX - ((maxX - minX) / 4));
			y = (wrappers.getRightUpper() - minY) * Math.random() + minY;
		} else {    // respawn over bottom wrapper
			x = (wrappers.getBottomRight() - wrappers.getBottomLeft()) * Math.random() + wrappers.getBottomLeft();
			y = (maxY - minY) * Math.random() + minY;
		}
	}
	
	private void initCurrent() {
		
		for (Platform p : platforms) {
			if (getLowerY() == p.getRectY() && (x >= p.getRectX() && x <= p.getRectX() + p.getWidth())) {			
				current = p;
				current.setColor(color);
				return;
			}
		}
		current = null;
	}
	
	public void addKill() { kills++; }
	public char getDirection() { return direction; }
	public int getLowerY() { return getY() + (height / 2); }
	public int getLastX() { return (int) lastX; }
	public int getLastY() { return (int) lastY; }
	public boolean isJumping() { return velY < 0; }
	public boolean isFalling() { return velY > 0; }
	public boolean isGrounded() { return velY == 0 && current != null && !wrapping; }
	public boolean isShooting() { return shooting; }
	public boolean isClipEmpty() { return getBullets() == 0; }
	public boolean isReloading() { return reloading; }
	public boolean isInvincible() { return invincible; }
	public boolean isRespawning() { return respawning; }	
	public int getKills() { return kills; }
	public int getDeaths() { return deaths; }
	public int getSuicides() { return suicides; }
	public int getBullets() { return ammo; }
	public int getTotalShots() { return totalShots; }
	public int getNumber() { return number; }
	
	public void startKillTickCount() { ticksSinceKill = 0; }
	public void startDeathTickCount() { ticksSinceDeath = 0; }
	public void stopKillTickCount() { ticksSinceKill = -1; }
	public void stopDeathTickCount() { ticksSinceDeath = -1; }
	public int getTicksSinceKill() { return ticksSinceKill; }
	public int getTicksSinceDeath() { return ticksSinceDeath; }
	
//--KeyListener variables and methods	
	
	protected HashSet<Integer> pressed = new HashSet<Integer>();
	
	public void keyPressed(KeyEvent e) {
		
		int key = e.getKeyCode();
		for (int i : controls) {
			if (key == i) {
				pressed.add(new Integer(key));
			}
		}
	}

	public void keyReleased(KeyEvent e) {
		
		int key = e.getKeyCode();
		for (int i : controls) {
			if (key == i) {
				pressed.remove(new Integer(key));
			}
		}
	}
	
	public void keyTyped(KeyEvent e) { }
}

