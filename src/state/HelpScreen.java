package state;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import game.*;
import input.*;

public class HelpScreen extends StateScreen {
	
	private static final int BUTTON_WIDTH = 200;
	private static final int BUTTON_HEIGHT = 100;
	
	private static final int MARGIN = 50;
	
	private static final String INSTRUCTION_TEXT =
			"<html>"
			+ "<p>Shoot other players to get kills.</p>"
			+ "<p>Touch the blue square to replenish bullets.</p>"
			+ ""
			+ "</html>";
	
	private StateButton menuButton;
	private StateButton playButton;
	
	private JLabel header;
	private JLabel instructions;

	public HelpScreen(GamePanel p) {
		
		super(p);
		
		menuButton = new StateButton(MARGIN, screenSize.height - MARGIN - BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT, "MENU", StateButton.MENU_COLOR, this);
		panel.add(menuButton);
		buttonMap.put(menuButton, GameState.MENU);
		
		playButton = new StateButton(screenSize.width - MARGIN - BUTTON_WIDTH, screenSize.height - MARGIN - BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT, "PLAY", StateButton.PLAY_COLOR, this);
		panel.add(playButton);	
		buttonMap.put(playButton, GameState.PLAY);
		
		setupHeader();	
		labelList.add(header);
		setupInstructions();
		labelList.add(instructions);
	}
	
	private void setupHeader() {
		
		header = new JLabel("HELP", JLabel.CENTER);
		header.setBounds(MARGIN, MARGIN, 1100, 100);    // hard coded
		header.setForeground(Color.WHITE);
		header.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		header.setFont(new Font("Arial", Font.BOLD, 30));
		header.setVisible(false);
		panel.add(header);
	}
	
	private void setupInstructions() {
		
		instructions = new JLabel(INSTRUCTION_TEXT, JLabel.LEFT);
		instructions.setVerticalAlignment(JLabel.TOP);
		instructions.setBounds(MARGIN, (2 * MARGIN) + 100, screenSize.width - (2 * MARGIN), screenSize.height - (2 * MARGIN) - BUTTON_HEIGHT);
		instructions.setForeground(Color.WHITE);
		instructions.setFont(new Font("Arial", Font.PLAIN, 20));
		instructions.setVisible(false);
		panel.add(instructions);
	}

	public void draw(Graphics g) {
		
		super.draw(g);
	}
	
	public void hideElements() {
		
		super.hideElements();
	}
	
	public JLabel getInstructionsLabel() { return instructions; }
}
