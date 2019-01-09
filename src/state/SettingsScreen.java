package state;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import game.*;
import entities.*;
import input.*;

public class SettingsScreen extends StateScreen {
	
	private static final int HEADER_WIDTH = 1100;
	private static final int HEADER_HEIGHT = 100;
	private static final Color HEADER_COLOR = Color.WHITE;
	
	private static final int SETTINGS_BUTTON_WIDTH = 300;
	private static final int SETTINGS_BUTTON_HEIGHT = 100;
	
	private static final int MENU_BUTTON_WIDTH = 200;
	private static final int MENU_BUTTON_HEIGHT = 100;	
	
	private static final int MARGIN = 50;
	private static final int SPACING = 25;
	
	private HashMap<ToggleButton, Togglable> settingsMap;
	
	private ToggleButton randomButton;
	private ToggleButton bottomButton;
	private ToggleButton ricochetButton;
	private ToggleButton invincibilityButton;
	
//	private int humanPlayers = 1;
//	private int botPlayers = 1;
//	private int winThreshold = (humanPlayers + botPlayers) * 10;
//	private double playerSpeed = Player.DEFAULT_PLAYER_SPEED;
//	private Dimension playerSize = Player.DEFAULT_PLAYER_SIZE;
//	private int maxAmmo = Player.DEFAULT_MAX_AMMO;
//	private double gravity = Player.DEFAULT_GRAVITY;
//	private int jumpVelocity = Player.DEFAULT_JUMP_VELOCITY;
//	private double bulletSpeed = Bullet.DEFAULT_BULLET_SPEED;
//	private Dimension bulletSize = Bullet.DEFAULT_BULLET_SIZE;
//	private long bulletLifeTime = Bullet.DEFAULT_LIFETIME;
	
	private JComboBox<JLabel> gameTypeButton;
	
	private JLabel header;
	
	private StateButton menuButton;

	public SettingsScreen(GamePanel p) {
		 
		super(p);
				
		settingsMap = new HashMap<>();
		
		randomButton = new ToggleButton(MARGIN, (2 * MARGIN) + HEADER_HEIGHT, SETTINGS_BUTTON_WIDTH, SETTINGS_BUTTON_HEIGHT, "Random", "Default", p.isRandom(), this);
		panel.add(randomButton);
		settingsMap.put(randomButton, () -> panel.toggleRandom());
		
		bottomButton = new ToggleButton(MARGIN, (2 * MARGIN) + SPACING + HEADER_HEIGHT + SETTINGS_BUTTON_HEIGHT, SETTINGS_BUTTON_WIDTH, SETTINGS_BUTTON_HEIGHT, "Use bottom platforms", "No bottom platforms", p.hasBottom(), this);
		panel.add(bottomButton);
		settingsMap.put(bottomButton, () ->	panel.toggleBottom());
		
		ricochetButton = new ToggleButton(MARGIN, (2 * MARGIN) + (2 * SPACING) + HEADER_HEIGHT + (2 * SETTINGS_BUTTON_HEIGHT), SETTINGS_BUTTON_WIDTH, SETTINGS_BUTTON_HEIGHT, "Bullets ricochet", "Bullets do not ricochet", p.useRicochet(), this);
		panel.add(ricochetButton);
		settingsMap.put(ricochetButton, () -> panel.toggleRicochet());
		
		invincibilityButton = new ToggleButton(MARGIN, (2 * MARGIN) + (3 * SPACING) + HEADER_HEIGHT + (3 * SETTINGS_BUTTON_HEIGHT), SETTINGS_BUTTON_WIDTH, SETTINGS_BUTTON_HEIGHT, "Use invincibility", "No invincibility", p.useInvincibility(), this);
		panel.add(invincibilityButton);
		settingsMap.put(invincibilityButton, () -> panel.toggleInvincibility());
		
		menuButton = new StateButton(MARGIN, screenSize.height - MARGIN - MENU_BUTTON_HEIGHT, MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT, "MENU", StateButton.MENU_COLOR, this);
		panel.add(menuButton);
		buttonMap.put(menuButton, GameState.MENU);
		
		setupHeader();
		labelList.add(header);
	}
	
	private void setupHeader() {
		
		header = new JLabel("SETTINGS", JLabel.CENTER);
		header.setBounds(MARGIN, MARGIN, HEADER_WIDTH, HEADER_HEIGHT);
		header.setForeground(HEADER_COLOR);
		header.setBorder(BorderFactory.createLineBorder(HEADER_COLOR));
		header.setFont(new Font("Arial", Font.BOLD, 30));
		header.setVisible(false);
		panel.add(header);
	}
	
	public void draw(Graphics g) {

	    for (ToggleButton b : settingsMap.keySet()) {
			b.setVisible(true);
		}		
		super.draw(g);
	}
	
	public void hideElements() {
		
		for (ToggleButton b : settingsMap.keySet()) {
			b.setVisible(false);
		}	
		super.hideElements();
	}
	
	public HashMap<ToggleButton, Togglable> getSettingsMap() { return settingsMap; }
}
