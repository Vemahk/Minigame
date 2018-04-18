package me.isle.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class GamePanel extends JPanel{

	private final int WIDTH;
	private final int HEIGHT;
	
	private final Camera camera;
	
	public GamePanel(int w, int h) {
		super();
		
		this.WIDTH = w;
		this.HEIGHT = h;
		
		camera = new Camera();
		
		this.setBackground(Color.BLACK);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		camera.drawVisible(g);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(WIDTH, HEIGHT);
	}

	public Camera getCamera() {
		return camera;
	}
	
}

