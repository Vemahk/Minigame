package me.isle.graphics;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

public class GameWindow extends JFrame{

	private GamePanel panel;
	private ArrowKeyListener akl;
	
	public GameWindow() {
		super("TBD");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//TODO Add the panel.
		this.add(panel = new GamePanel(500, 500));
		
		this.pack();
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setVisible(true);

		this.addKeyListener(akl = new ArrowKeyListener());
		this.requestFocus();
	}
	
	public Camera getCamera() {
		return panel.getCamera();
	}
	
	public ArrowKeyListener getKeyListener() {
		return akl;
	}
}

class ArrowKeyListener implements KeyListener{
	boolean left = false;
	boolean right = false;
	
	boolean up = false;
	boolean down = false;
	
	public int movementX() {
		return left ^ right ? (left ? -1 : 1) : 0;
	}
	
	public int movementY() {
		return up ^ down ? (up ? -1 : 1) : 0;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		
		if(key == KeyEvent.VK_LEFT)
			left = true;
		
		if(key == KeyEvent.VK_RIGHT)
			right = true;
		
		if(key == KeyEvent.VK_UP)
			up = true;
		
		if(key == KeyEvent.VK_DOWN)
			down = true;
		
		System.out.printf("Key %d pressed.%n", key);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		
		if(key == KeyEvent.VK_LEFT)
			left = false;
		
		if(key == KeyEvent.VK_RIGHT)
			right = false;
		
		if(key == KeyEvent.VK_UP)
			up = false;
		
		if(key == KeyEvent.VK_DOWN)
			down = false;
		
		System.out.printf("Key %d released.%n", key);
	}

	@Override
	public void keyTyped(KeyEvent e) { }
}