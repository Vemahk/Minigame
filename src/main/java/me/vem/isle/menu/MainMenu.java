package me.vem.isle.menu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.io.IOException;

import javax.swing.JPanel;

import me.vem.isle.App;
import me.vem.isle.resources.ResourceManager;

public class MainMenu extends JPanel{
	private static final long serialVersionUID = -5689029521190126878L;
	private static final String[] opt = {"New Game", "Load Game", "Settings", null, "Credits", "Exit"};
	
	private int selected = 0;
	
	public MainMenu() {
		this.setBackground(Color.BLACK);
		this.setPreferredSize(new Dimension(512, 512));
		
		try {
			Font font = Font.createFont(Font.TRUETYPE_FONT, ResourceManager.getResource("8bitbold.ttf"));
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(font);
			
			this.setFont(font.deriveFont(Font.TRUETYPE_FONT, 24));
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.WHITE);
		
		FontMetrics fm = g.getFontMetrics();	

		int y = 50;
		int x = 50;
		for(int i=0;i<opt.length;i++) {
			if(i == selected)
				drawString(g, fm, ">", x-15, y);
			y = drawString(g, fm, opt[i] == null ? "" : opt[i], x, y) + 50;
		}
	}
	
	public void select() {
		if(selected == 0) { //GET ME DAT NEW GAME BOOIIII
			this.setVisible(false);
			App.newGame();
		}else if(selected == 4) { //SHOW ME DA CREDITS
			//TODO
		}else if(selected == 5) {
			App.getWindow().dispose();
			System.exit(0);
		}
	}
	
	public void moveUp() {
		int sel = selected;
		do {
			if(--sel < 0) return;
		}while(opt[sel] == null);
		
		selected = sel;
		repaint();
	}
	
	public void moveDown() {
		int sel = selected;
		do {
			if(++sel >= opt.length) return;
		}while(opt[sel] == null);
		
		selected = sel;
		repaint();
	}
	
	private int drawString(Graphics g, FontMetrics fm, String s, int x, int y) {
		y += fm.getAscent() - fm.getDescent();
		g.drawString(s, x, y);
		
		return y;
	}
	
}
