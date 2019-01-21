package game;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import entities.*;
import state.*;
import input.*;

public class GamePanel extends JPanel implements Runnable {

    private static final long serialVersionUID = 1L;

    //--Screen information
    private static Dimension NATIVE_SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();

    //--Game default values
    private static final Dimension DEFAULT_SCREEN_SIZE = new Dimension(1200, 900);
    private static final double DEFAULT_PLATFORM_DENSITY = 7 / (1200 * 900);
    private static final GameType DEFAULT_GAME_TYPE = GameType.VERSUS;
    private static final int HUMAN_PLAYERS = 2;
    private static final int BOT_PLAYERS = 0;
    private static final int DEFAULT_WIN_THRESHOLD = 10 * (HUMAN_PLAYERS + BOT_PLAYERS);

    private boolean gameOn;
    private long playStartTime;
    private long playEndTime;

    //--Game settings
    private Dimension screenSize = DEFAULT_SCREEN_SIZE;
    private GameType gameType = GameType.VERSUS;    // versus vs. time trial
    private int humanPlayers = HUMAN_PLAYERS;
    private int botPlayers = BOT_PLAYERS;
    private boolean random = false;    // random platforms, wrap areas, reloader movement
    private boolean hasBottom = true;
    private int winThreshold = DEFAULT_WIN_THRESHOLD;    // kills needed to win game
    private double playerSpeed = Player.DEFAULT_PLAYER_SPEED;
    private Dimension playerSize = Player.DEFAULT_PLAYER_SIZE;
    private int maxAmmo = Player.DEFAULT_MAX_AMMO;
    private double gravity = Player.DEFAULT_GRAVITY;
    private int jumpVelocity = Player.DEFAULT_JUMP_VELOCITY;
    private double bulletSpeed = Bullet.DEFAULT_BULLET_SPEED;
    private Dimension bulletSize = Bullet.DEFAULT_BULLET_SIZE;
    private long bulletLifeTime = Bullet.DEFAULT_LIFETIME;
    private boolean useRicochet = false;    // bullets bounce off of walls
    private Dimension reloaderSize = MovingReloader.DEFAULT_RELOADER_SIZE;
    private boolean useInvincibility = false;    // invincibility block spawns periodically
    private Dimension invincibilitySize = Invincibility.DEFAULT_INVINCIBILITY_SIZE;
    private long invincibilityLifeTime = Invincibility.DEFAULT_LIFETIME;

    //--Game state entities
    private GameState state;
    private MenuScreen menu;
    private PlayScreen play;
    private HelpScreen help;
    private SettingsScreen settings;

    //--Game environment entities
    private LinkedList<Platform> platforms;    // List of all platforms in the arena
    private WrapAreas wrappers;
    private MovingReloader reloader;
    private Invincibility invBlock;
    private LinkedList<Bullet> bullets;
    private ArrayList<Player> players;

//	private JLabel score;

    public GamePanel() {

        setPreferredSize(screenSize);
        setDoubleBuffered(true);
        setLayout(null);    // for JLabel placement
        setFocusable(true);

        gameOn = false;

        state = GameState.MENU;
        menu = new MenuScreen(this);
        play = new PlayScreen(this);
        help = new HelpScreen(this);
        settings = new SettingsScreen(this);
    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        /* background----------------------------------------------------------------------------*/
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getPreferredSize().width, getPreferredSize().height);

        switch (state) {
            case MENU:
                menu.draw(g);
                break;
            case PLAY:
                if (gameOn) {
                    for (Player p : players) {
                        p.draw(g);
                    }
                    for (Platform p : platforms) {
                        p.draw(g);
                    }
                    reloader.draw(g);
                    if (useInvincibility && invBlock.isReady()) {
                        invBlock.draw(g);
                    }
                    for (Bullet b : bullets) {
                        b.draw(g);
                    }
                    drawWrapAreas(g);
                    play.draw(g);
                }
                break;
            case HELP:
                help.draw(g);
                break;
            case SETTINGS:
                settings.draw(g);
                break;
            case QUIT:
                break;
            default:
                break;
        }
    }

    private void drawWrapAreas(Graphics g) {    // change hard-coding that is in this method

        int[][] wrapAreas = wrappers.getWrapCoordinates();
        g.setColor(WrapAreas.getColor());
        for (int i = 0; i < wrapAreas.length; i++) {    // draw wrap areas
            g.fillRect(wrapAreas[i][0], wrapAreas[i][1], wrapAreas[i][2], wrapAreas[i][3]);
        }
        if (random) {    // draw lines connecting corresponding wrap areas
            g.drawLine(WrapAreas.getWrapDepth(), wrapAreas[0][1], getPreferredSize().width - WrapAreas.getWrapDepth(), wrapAreas[1][1]);
            g.drawLine(WrapAreas.getWrapDepth(), wrapAreas[0][1] + wrapAreas[0][3], getPreferredSize().width - WrapAreas.getWrapDepth(), wrapAreas[1][1] + wrapAreas[1][3]);
            g.drawLine(wrapAreas[2][0], WrapAreas.getWrapDepth(), wrapAreas[3][0], getPreferredSize().height - WrapAreas.getWrapDepth());
            g.drawLine(wrapAreas[2][0] + wrapAreas[2][2], WrapAreas.getWrapDepth(), wrapAreas[3][0] + wrapAreas[3][2], getPreferredSize().height - WrapAreas.getWrapDepth());

//			g.setColor(Color.WHITE);
//			g.drawLine(WrapAreas.getWrapDepth(), wrapAreas[0][1] + (wrapAreas[0][3] / 2), getPreferredSize().width - WrapAreas.getWrapDepth(), wrapAreas[1][1] + (wrapAreas[1][3] / 2));
//			g.drawLine(wrapAreas[2][0] + (wrapAreas[2][2] / 2), WrapAreas.getWrapDepth(), wrapAreas[3][0] + (wrapAreas[3][2] / 2), getPreferredSize().height - WrapAreas.getWrapDepth());
        }
    }

    public void run() {

        while (true) {
            switch (state) {
                case MENU:
                    break;
                case PLAY:
                    if (!gameOn) {
                        gameOn = true;
                        initializeEntities();
                        playStartTime = System.currentTimeMillis();
                        System.out.println("Kill Feed:");
                    }
                    tick();
                    break;
                case HELP:
                    break;
                case SETTINGS:
                    break;
                case QUIT:
                    System.exit(0);
                default:
                    break;
            }
            repaint();
            try { Thread.sleep(10); } catch (Exception e) { }
        }
    }

    private void initializeEntities() {

        wrappers = new WrapAreas(getPreferredSize(), random);
        if (random) {
            initializeRandomPlatforms();
        } else {
            initializePlatforms();
        }
        reloader = new MovingReloader(random, this);
        if (useInvincibility) {
            invBlock = new Invincibility(this);
        }
        bullets = new LinkedList<Bullet>();
        initializePlayers();
        play.initializeLists();
    }

    private void initializePlatforms() {

        platforms = new LinkedList<Platform>();
        platforms.add(new Platform(0, 105, 400, 5, false)); // top left
        platforms.add(new Platform(800, 105, 400, 5, false)); // top right
        platforms.add(new Platform(0, 895, 420, 5, false)); // bottom left
        platforms.add(new Platform(780, 895, 420, 5, false)); // bottom right
        platforms.add(new Platform(300, 600, 600, 5, false)); // center
        platforms.add(new Platform(510, 300, 180, 5, false)); // top center
        platforms.add(new Platform(310, 450, 180, 5, false)); // left center
        platforms.add(new Platform(700, 450, 180, 5, false)); // right center
        platforms.add(new Platform(510, 750, 180, 5, false)); // bottom center
//		platforms.add(new Platform(300, 563, 600, 5, false)); // testing
//		platforms.add(new Platform(250, 630, 550, 5, false)); // testing
//		platforms.add(new Platform(250, 645, 550, 5, false)); // testing
//		platforms.add(new Platform(0, 105, 900, 5, false)); // testing
    }

    private void initializeRandomPlatforms() {

        platforms = new LinkedList<Platform>();
        platforms.add(new Platform(0, wrappers.getLeftLower(), (int)(getPreferredSize().width / 4), 5, false)); // left wrapper
        platforms.add(new Platform(getPreferredSize().height, wrappers.getRightLower(), (int)(getPreferredSize().width / 4), 5, false)); // right wrapper

        if (hasBottom) {
            platforms.add(new Platform(0, getPreferredSize().height - 5, wrappers.getBottomLeft(), 5, false)); // bottom left
            platforms.add(new Platform(wrappers.getBottomRight(), getPreferredSize().height - 5, getPreferredSize().width - wrappers.getBottomRight(), 5, false)); // bottom right
        }

        int platX;
        int platY;
        int platWidth;

        /* create 5-9 random platforms */
        for (int i = 0; i < 5 * Math.random() + 5; i++) {
            platX = (int)(getPreferredSize().width * Math.random());
            platY = (int)((getPreferredSize().height - Player.DEFAULT_PLAYER_SIZE.height) * Math.random() + Player.DEFAULT_PLAYER_SIZE.height);
            platWidth = (int)((getPreferredSize().width - 200) * Math.random() + 100);
            platforms.add(new Platform(platX, platY, platWidth, 5, true));
        }
    }

    private void initializePlayers() {

        players = new ArrayList<Player>();
        Player one, two, three;

        if (humanPlayers >= 1) {
            one = new Player(Player.P1_SPAWN.x, Player.P1_SPAWN.y, 'l', 1, Player.CONTROLS_1, Player.P1_COLOR, this);
            addKeyListener(one);
            players.add(one);
        }
        if (humanPlayers >= 2) {
            two = new Player(Player.P2_SPAWN.x, Player.P2_SPAWN.y, 'r', 2, Player.CONTROLS_3, Player.P2_COLOR, this);
            addKeyListener(two);
            players.add(two);
        }
        if (humanPlayers == 3) {
            three = new Player(Player.P3_SPAWN.x, Player.P3_SPAWN.y, 'l', 3, Player.CONTROLS_2, Player.P3_COLOR, this);
            addKeyListener(three);
            players.add(three);
        }

        if (botPlayers >= 1) {
            if (humanPlayers == 0) {    // first bot is P1
                one = new AIPlayer(Player.P1_SPAWN.x, Player.P1_SPAWN.y, 'l', 1, Player.P1_COLOR, this);
                players.add(one);
            } else if (humanPlayers == 1) {    // first bot is P2
                two = new AIPlayer(Player.P2_SPAWN.x, Player.P2_SPAWN.y, 'r', 2, Player.P2_COLOR, this);
                players.add(two);
            } else if (humanPlayers == 2) {    // first bot is P3
                three = new AIPlayer(Player.P3_SPAWN.x, Player.P3_SPAWN.y, 'l', 3, Player.P3_COLOR, this);
                players.add(three);
            }
        }
        if (botPlayers >= 2) {
            if (humanPlayers == 0) {    // second bot is P2
                two = new AIPlayer(Player.P2_SPAWN.x, Player.P2_SPAWN.y, 'r', 2, Player.P2_COLOR, this);
                players.add(two);
            } else if (humanPlayers == 1) {    // second bot is P3
                three = new AIPlayer(Player.P3_SPAWN.x, Player.P3_SPAWN.y, 'l', 3, Player.P3_COLOR, this);
                players.add(three);
            }
        }
        if (botPlayers == 3) {    // third bot is P3
            three = new AIPlayer(Player.P3_SPAWN.x, Player.P3_SPAWN.y, 'l', 3, Player.P3_COLOR, this);
            players.add(three);
        }
    }

    private void tick() {

        updateBullets();
        for (Player p : players) {
            p.move();
            if (p.getKills() >= winThreshold) {
                gameOn = false;
                play.hideElements();
                playEndTime = System.currentTimeMillis();
                System.out.println("==========Player " + p.getNumber() + " wins!!!==========");
//				System.out.println("\nFinal Stats (K / D / S (KD) (rKD) (KS) (Acc.%))\n");
//				for (Player a : players) {
//					System.out.print("Player " + a.getNumber() + ":\t" + a.getKills() + " / " + a.getDeaths() + " / " + a.getSuicides());
//					System.out.printf(" (" + "%.2f) (" + "%.2f) ", 1.0 * a.getKills() / a.getDeaths(),
//							(1.0 * a.getKills() + a.getSuicides()) / a.getDeaths());
//					System.out.printf("(%.2f) (%.3f%%)\n", (1.0 * a.getKills() + a.getSuicides()) / a.getSuicides(),
//							(100.0 * (a.getKills() + a.getSuicides())) / a.getTotalShots());
//				}
                System.out.println("Round time: " + ((playEndTime - playStartTime) / 1000) + " seconds\n");
                state = GameState.MENU;
            }
        }
        reloader.move();
        if (useInvincibility) {
            updateInvincibility();
        }
        play.updateScore();
    }

    private void updateBullets() {

        Iterator<Bullet> it = bullets.iterator();
        if (!bullets.isEmpty()) {
            while (it.hasNext()) {
                Bullet b = it.next();
                if (b.isAlive() && b.inBounds()) {    // if bullet is older than its max lifetime and still on screen
                    it.remove();
                } else {
                    b.move();
                }
            }
        }
    }

    private void updateInvincibility() {

        if (invBlock.isReady()) {
            invBlock.move();
        }
        if (invBlock.finishedActivatedState()) {
            invBlock.setup();
        }
    }

    public Dimension getScreenSize() { return screenSize; }

    public int numHumans() { return humanPlayers; }
    public int numBots() { return botPlayers; }

    public boolean isRandom() { return random; }
    public void toggleRandom() { random = !random; }

    public boolean hasBottom() { return hasBottom; }
    public void toggleBottom() { hasBottom = !hasBottom; }

    public double getPlayerSpeed() { return playerSpeed; }
    public Dimension getPlayerSize() { return playerSize; }
    public int getMaxAmmo() { return maxAmmo; }
    public double getGravity() { return gravity; }
    public int getJumpVelocity() { return jumpVelocity; }
    public double getBulletSpeed() { return bulletSpeed; }
    public Dimension getBulletSize() { return bulletSize; }
    public long getBulletLifeTime() { return bulletLifeTime; }

    public boolean useRicochet() { return useRicochet; }
    public void toggleRicochet() { useRicochet = !useRicochet; }

    public Dimension getReloaderSize() { return reloaderSize; }

    public Dimension getInvincibilitySize() { return invincibilitySize; }
    public long getInvincibilityLifeTime() { return invincibilityLifeTime; }

    public boolean useInvincibility() { return useInvincibility; }
    public void toggleInvincibility() { useInvincibility = !useInvincibility; }

    public GameState getGameState() { return state; }
    public void setGameState(GameState s) { state = s; }

    public WrapAreas getWrapAreas() { return wrappers; }
    public LinkedList<Platform> getPlatforms() { return platforms; }
    public MovingReloader getReloader() { return reloader; }
    public Invincibility getInvincbility() { return invBlock; }
    public LinkedList<Bullet> getBullets() { return bullets; }
    public ArrayList<Player> getPlayers() { return players; }
}
