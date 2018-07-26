package me.vem.isle.menu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

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
	
	private LinkedList<String> credits;
	private boolean creditsActive = false;
	private float creditsOffset = 0f;
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.WHITE);
		
		FontMetrics fm = g.getFontMetrics();	

		if(!creditsActive) {
			int y = 50;
			int x = 50;
			for(int i=0;i<opt.length;i++) {
				if(i == selected)
					drawString(g, fm, ">", x-15, y);
				y = drawString(g, fm, opt[i] == null ? "" : opt[i], x, y) + 50;
			}
		}else {
			
			int y = 50 - (int)creditsOffset;
			for(String s : credits)
				y = drawStringCenter(g, fm, s, 0, y, this.getWidth()) + 10;
			
			creditsOffset += 1;
			if(creditsOffset > 620)
				creditsActive = false;
		}
	}
	
	public void select() {
		if(!isVisible()) return;
		
		if(selected == 0) { //GET ME DAT NEW GAME BOOIIII
			App.newGame();
		}else if(selected == 4) { //SHOW ME DA CREDITS
			creditsActive = true;
			creditsOffset = 0f;
			
			if(credits == null) {
				credits = new LinkedList<>();
				BufferedReader reader = new BufferedReader(new InputStreamReader(ResourceManager.getResource("credits.txt")));
				
				try {
					String read;
					while((read = reader.readLine()) != null)
						credits.add(read);
				}catch(IOException e) {
					e.printStackTrace();
				}
				
				System.out.println(credits);
			}
			
			new Thread() {
				@Override
				public void run() {
					while(MainMenu.this.creditsActive) {
						MainMenu.this.repaint();
						
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					MainMenu.this.repaint();
				}
			}.start();
		}else if(selected == 5)
			App.shutdown();
	}
	
	public void moveUp() {
		if(!isVisible()) return;
		
		int sel = selected;
		
		do {
			if(--sel < 0) return;
		}while(opt[sel] == null);
		
		selected = sel;
		repaint();
	}
	
	public void moveDown() {
		if(!isVisible()) return;
		
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
	
	private int drawStringCenter(Graphics g, FontMetrics fm, String s, int x, int y, float w) {
		y += fm.getAscent() - fm.getDescent();
		int strWidth = fm.stringWidth(s);
		
		g.drawString(s, (int)(x + w/2 - strWidth/2), y);
		
		return y;
	}
	
}
