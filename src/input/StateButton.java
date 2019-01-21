package input;

import java.awt.*;
import javax.swing.*;
import state.*;

public class StateButton extends JLabel {

    private static final long serialVersionUID = 1L;

    private static final Font DEFAULT_FONT = new Font("Arial", Font.BOLD, 24);

    public static final Color MENU_COLOR = Color.RED;
    public static final Color PLAY_COLOR = Color.GREEN;
    public static final Color HELP_COLOR = Color.YELLOW;
    public static final Color SETTINGS_COLOR = Color.CYAN;
    public static final Color QUIT_COLOR = Color.RED;

    private Color color;
    private Color colorLight;

    public StateButton(int x, int y, int w, int h, String t, Color c, StateScreen s) {

        super(t, JLabel.CENTER);

        color = new Color(c.getRed(), c.getGreen(), c.getBlue(), 125);
        colorLight = c;

        setBounds(x, y, w, h);
        setForeground(color);
        setBorder(BorderFactory.createDashedBorder(color));
        setFont(DEFAULT_FONT);
        setVisible(false);
        addMouseListener(new StateButtonListener(this, s));
    }

    public void highlightButton() {

        setForeground(colorLight);
        setBorder(BorderFactory.createDashedBorder(colorLight));
    }

    public void darkenButton() {

        setForeground(color);
        setBorder(BorderFactory.createDashedBorder(color));
    }
}

