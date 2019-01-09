package input;

import java.awt.event.*;

import state.*;

public class ToggleButtonListener implements MouseListener {

	private ToggleButton button;
	private SettingsScreen screen;
	
	public ToggleButtonListener(ToggleButton b, SettingsScreen s) {
		
		button = b;
		screen = s;
	}
	
	public void mousePressed(MouseEvent e) {
		
		button.toggleButton();
		
		Togglable action = screen.getSettingsMap().get(button);
		action.toggleSetting();
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
