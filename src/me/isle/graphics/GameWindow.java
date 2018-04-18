package me.isle.graphics;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

import me.isle.Startup;
import me.isle.game.Game;

public class GameWindow extends JFrame{

	private GamePanel panel;
	
	public GameWindow() {
		super("TBD");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//TODO Add the panel.
		this.add(panel = new GamePanel(500, 500));

		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);

		this.addKeyListener(Startup.game.setKeyListener(new ArrowKeyListener()));
		this.requestFocus();
	}
	
	public Camera getCamera() {
		return panel.getCamera();
	}
}

