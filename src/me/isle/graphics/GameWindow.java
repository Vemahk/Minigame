package me.isle.graphics;

import javax.swing.JFrame;

import me.isle.game.Game;

public class GameWindow extends JFrame{

	private GamePanel panel;
	
	public GameWindow() {
		super("TBD");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.add(panel = new GamePanel(500, 500));

		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);

		this.addKeyListener(Game.game.setKeyListener(new ArrowKeyListener()));
		this.requestFocus();
	}
	
	public Camera getCamera() {
		return panel.getCamera();
	}
}

