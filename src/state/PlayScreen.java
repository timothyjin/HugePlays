package state;

import java.util.*;
import java.awt.*;
import javax.swing.*;

import entities.*;
import game.*;

/**
 * Contains graphical elements that are overlaid on the screen during gameplay. These elements include
 * the scoreboard and state indicators for every player.
 * 
 * @author Timothy Jin
 * @version 3.0 (2018)
 */
public class PlayScreen extends StateScreen {
	
	private static final int SCORE_WIDTH = 300;
	private static final int SCORE_HEIGHT = 100;
	
	private static final int ACTION_ELEVATION = -30;
	private static final int ACTION_WIDTH = 6;
	private static final int ACTION_HEIGHT = 12;
	private static final int ACTION_SPACING = 30;	
	private static final int STATUS_ELEVATION = 30;
	private static final int STATUS_WIDTH = 6;
	private static final int STATUS_HEIGHT = 12;
	private static final int STATUS_SPACING = 30;	
	
	private static final int ACTION_DECAY = 10;
	
	private JLabel score;
	
	private ArrayList<int[]> actionList;
	private ArrayList<int[]> statusList;

	public PlayScreen(GamePanel p) {
		
		super(p);
		
		setupScore();
		labelList.add(score);
		
		actionList = new ArrayList<int[]>();
		statusList = new ArrayList<int[]>();
	}
	
	private void setupScore() {
		
		score = new JLabel("", JLabel.CENTER);
		score.setBounds((screenSize.width / 2) - (SCORE_WIDTH / 2), (screenSize.height / 2) - (SCORE_HEIGHT / 2) , SCORE_WIDTH, SCORE_HEIGHT);
		score.setForeground(Color.WHITE);
		score.setFont(new Font("Times New Roman", Font.BOLD, 24));
		score.setVisible(false);
		panel.add(score);
	}
	
	public void initializeLists() {
		
		int size = panel.getPlayers().size();
		int end = size / 2;
			
		if (size % 2 == 0) {
			int spacing;
			for (int i = -1 * end; i <= (end - 1); i++) {
				if (i < 0) {
					spacing = ((i + 1) * STATUS_SPACING) - (STATUS_SPACING / 2);
				} else {
					spacing = (i * STATUS_SPACING) + (STATUS_SPACING / 2);
				}
				int[] aRect = {(screenSize.width / 2) + spacing + (i * ACTION_WIDTH), (screenSize.height / 2) + ACTION_ELEVATION, ACTION_WIDTH, ACTION_HEIGHT};
				actionList.add(aRect);
				int[] sRect = {(screenSize.width / 2) + spacing + (i * STATUS_WIDTH), (screenSize.height / 2) + STATUS_ELEVATION, STATUS_WIDTH, STATUS_HEIGHT};
				statusList.add(sRect);
			}
		} else {
			int middle;
			for (int i = -1 * end; i <= end; i++) {
				middle = (i * STATUS_WIDTH) - (STATUS_WIDTH / 2);
				int[] aRect = {(screenSize.width / 2) + middle + (i * ACTION_SPACING), (screenSize.height / 2) + ACTION_ELEVATION, ACTION_WIDTH, ACTION_HEIGHT};
				actionList.add(aRect);
				int[] sRect = {(screenSize.width / 2) + middle + (i * STATUS_SPACING), (screenSize.height / 2) + STATUS_ELEVATION, STATUS_WIDTH, STATUS_HEIGHT};
				statusList.add(sRect);
			}
		}
	}
	
	public void draw(Graphics g) {
		
		super.draw(g);
		
		ArrayList<Player> players = panel.getPlayers();
		
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			int[] aRect = actionList.get(i);
			int[] sRect = statusList.get(i);		

			if (p.getTicksSinceKill() >= 0) {
				if (255 - (ACTION_DECAY * p.getTicksSinceKill()) < 0) {
					p.stopKillTickCount();
				} else {
					g.setColor(new Color(0, 255, 0, 255 - (ACTION_DECAY * p.getTicksSinceKill())));
					g.fillRect(aRect[0], aRect[1], aRect[2], aRect[3]);
				}
			} else if (p.getTicksSinceDeath() >= 0) {
				if (255 - (ACTION_DECAY * p.getTicksSinceDeath()) < 0) {
					p.stopDeathTickCount();
				} else {
					g.setColor(new Color(255, 0, 0, 255 - (ACTION_DECAY * p.getTicksSinceDeath())));
					g.fillRect(aRect[0], aRect[1], aRect[2], aRect[3]);
				}
			}
			
			if (p.isReloading()) {
				g.setColor(Color.BLUE);
			} else if (p.isInvincible()) {
				g.setColor(panel.getInvincbility().getColor());
			} else {
				g.setColor(p.getColor());
			}
			g.fillRect(sRect[0], sRect[1], sRect[2], sRect[3]);
		}
	}
	
	public void updateScore() {
		
		String s = "";
		ArrayList<Player> players = panel.getPlayers();
		int maxIndex = 0;
		boolean tied = false;
		
		for (int n = 0; n < players.size(); n++) {
			Player p = players.get(n);
			s += p.getKills();
			if (n != players.size() - 1) {
				s += " | ";
			}
			
			if (p.getKills() > players.get(maxIndex).getKills()) {    // player with most kills (so far)
				maxIndex = n;
			} else if (n != 0 && p.getKills() == players.get(maxIndex).getKills()) {    // at least two players are tied
				tied = true;
			}
		}
		score.setText(s);
		
		if (tied) {
			score.setForeground(Color.WHITE);
		} else {
			score.setForeground(players.get(maxIndex).getColor());
		}
	}
}
