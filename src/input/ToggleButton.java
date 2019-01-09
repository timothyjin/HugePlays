package input;

import java.awt.*;
import javax.swing.*;
import state.*;

public class ToggleButton extends JLabel {

	private static final long serialVersionUID = 1L;
	
	private static final Font DEFAULT_FONT = new Font("Arial", Font.BOLD, 24);
	
	private static final Color ON_COLOR = new Color(0, 255, 0, 125);
	private static final Color ON_COLOR_LIGHT = Color.GREEN;
	private static final Color OFF_COLOR = new Color(255, 0, 0, 125);
	private static final Color OFF_COLOR_LIGHT = Color.RED;
	
	private boolean on;
	
	private String onText;
	private String offText;

	public ToggleButton(int x, int y, int w, int h, String ont, String offt, boolean o, SettingsScreen s) {
		
		super(offt, JLabel.CENTER);
		
		on = o;
		onText = ont;
		offText = offt;
		
		if (on) {
			setText(ont);
			setForeground(ON_COLOR);
			setBorder(BorderFactory.createLineBorder(ON_COLOR));
		} else {
			setForeground(OFF_COLOR);
			setBorder(BorderFactory.createLineBorder(OFF_COLOR));
		}
		
		setBounds(x, y, w, h);
		setFont(DEFAULT_FONT);
		setVisible(false);
		addMouseListener(new ToggleButtonListener(this, s));
	}
	
	public void toggleButton() {
		
		if (on) {
			setText(offText);
			setForeground(OFF_COLOR_LIGHT);
			setBorder(BorderFactory.createLineBorder(OFF_COLOR_LIGHT));
		} else {
			setText(onText);
			setForeground(ON_COLOR_LIGHT);
			setBorder(BorderFactory.createLineBorder(ON_COLOR_LIGHT));
		}
		on = !on;
	}
	
	public void highlightButton() {
		
		if (on) {
			setForeground(ON_COLOR_LIGHT);
			setBorder(BorderFactory.createLineBorder(ON_COLOR_LIGHT));
		} else {
			setForeground(OFF_COLOR_LIGHT);
			setBorder(BorderFactory.createLineBorder(OFF_COLOR_LIGHT));
		}
	}
	
	public void darkenButton() {
		
		if (on) {
			setForeground(ON_COLOR);
			setBorder(BorderFactory.createLineBorder(ON_COLOR));
		} else {
			setForeground(OFF_COLOR);
			setBorder(BorderFactory.createLineBorder(OFF_COLOR));
		}
	}
}
