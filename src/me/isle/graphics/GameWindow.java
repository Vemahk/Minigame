package me.isle.graphics;

import java.awt.Dimension;

import javax.swing.JFrame;

public class GameWindow extends JFrame{

	private GamePanel panel;
	
	public GameWindow() {
		super("TBD");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//TODO Add the panel.
		this.add(panel = new GamePanel(500, 500));
		
		this.pack();
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setVisible(true);
	}
	
}
