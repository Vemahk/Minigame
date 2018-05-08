package me.isle.graphics;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import me.isle.game.Game;

public class ArrowKeyListener implements KeyListener{
	boolean left = false;
	boolean right = false;
	
	boolean up = false;
	boolean down = false;
	
	private boolean map = false;
	
	public boolean showMap() {
		return map;
	}
	
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

		if(key == KeyEvent.VK_F3)
			Game.DEBUG_ACTIVE = !Game.DEBUG_ACTIVE;
		
		if(key == KeyEvent.VK_M)
			map = !map;
		//System.out.printf("Key %d pressed.%n", key);
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
		
		//System.out.printf("Key %d released.%n", key);
	}

	@Override
	public void keyTyped(KeyEvent e) {}
}