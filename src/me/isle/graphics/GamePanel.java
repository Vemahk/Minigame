package me.isle.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import me.isle.Startup;

public class GamePanel extends JPanel{

	private final int WIDTH;
	private final int HEIGHT;
	
	public GamePanel(int w, int h) {
		super();
		
		this.WIDTH = w;
		this.HEIGHT = h;
		
		this.setBackground(Color.BLACK);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		//Just a test.
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 100, 100);
		
		g.drawImage(Startup.game.getLand(0, 0).getImage(), 0, 0, this);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(WIDTH, HEIGHT);
	}
	
}
