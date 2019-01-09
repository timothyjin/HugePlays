package state;

import java.util.*;
import java.awt.*;
import game.*;
import input.*;

public class MenuScreen extends StateScreen {
	
	private static final int BUTTON_WIDTH = 300;
	private static final int BUTTON_HEIGHT = 100;
	
	private StateButton playButton;
	private StateButton helpButton;
	private StateButton settingsButton;
	private StateButton quitButton;
	
	public MenuScreen(GamePanel p) {
		
		super(p);
	
		playButton = new StateButton(200, 450, BUTTON_WIDTH, BUTTON_HEIGHT, "PLAY", StateButton.PLAY_COLOR, this);
		panel.add(playButton);
		buttonMap.put(playButton, GameState.PLAY);
		
		helpButton = new StateButton(700, 450, BUTTON_WIDTH, BUTTON_HEIGHT, "HELP", StateButton.HELP_COLOR, this);
		panel.add(helpButton);
		buttonMap.put(helpButton, GameState.HELP);
		
		settingsButton = new StateButton(200, 650, BUTTON_WIDTH, BUTTON_HEIGHT, "SETTINGS", StateButton.SETTINGS_COLOR, this);
		panel.add(settingsButton);
		buttonMap.put(settingsButton, GameState.SETTINGS);
		
		quitButton = new StateButton(700, 650, BUTTON_WIDTH, BUTTON_HEIGHT, "QUIT", StateButton.QUIT_COLOR, this);
		panel.add(quitButton);
		buttonMap.put(quitButton, GameState.QUIT);
	}
	
	public void draw(Graphics g) {
		
		// draw title and background
		
		super.draw(g);
	}
	
	public void hideElements() {
		
		super.hideElements();
	}
	
	public StateButton getPlayButton() { return playButton; }
	public StateButton getHelpButton() { return helpButton; }
	public StateButton getSettingsButton() { return settingsButton; }
	public StateButton getQuitButton() { return quitButton; }
}
