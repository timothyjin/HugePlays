package state;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import game.*;
import input.*;

public abstract class StateScreen {
	
	LinkedList<JLabel> labelList;
	HashMap<StateButton, GameState> buttonMap;	
	GamePanel panel;
	Dimension screenSize;
	
	public StateScreen(GamePanel p) {
		
		labelList = new LinkedList<JLabel>();
		buttonMap = new HashMap<StateButton, GameState>();
		panel = p;
		screenSize = panel.getScreenSize();
	}
	
	public void draw(Graphics g) {
		
		for (JLabel l : labelList) {
			l.setVisible(true);
		}
		for (StateButton b : buttonMap.keySet()) {
			b.setVisible(true);
		}
	}
	
	public void hideElements() {
		
		for (JLabel l : labelList) {
			l.setVisible(false);
		}
		for (StateButton b : buttonMap.keySet()) {
			b.setVisible(false);
		}
	}
	
	public LinkedList<JLabel> getLabelList() { return labelList; }
	public HashMap<StateButton, GameState> getButtonMap() { return buttonMap; }
	public GamePanel getPanel() { return panel; }
}
