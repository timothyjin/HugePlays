package input;

import java.awt.event.*;
import state.*;

public class StateButtonListener implements MouseListener {
	
	private StateButton button;
	private StateScreen screen;
	
	public StateButtonListener(StateButton b, StateScreen s) {
		
		button = b;
		screen = s;
	}
	
	public void mousePressed(MouseEvent e) {
			
		screen.hideElements();
		
		GameState newState = screen.getButtonMap().get(button);
		screen.getPanel().setGameState(newState);    // change game state
	}

	public void mouseEntered(MouseEvent e) {

		button.highlightButton();
	}

	public void mouseExited(MouseEvent e) {

		button.darkenButton();
	}
	
	public void mouseClicked(MouseEvent e) { }
	
	public void mouseReleased(MouseEvent e) { }
}
