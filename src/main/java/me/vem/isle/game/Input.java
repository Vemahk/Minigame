package me.vem.isle.game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import me.vem.isle.App;
import me.vem.isle.game.entity.PlayerEntity;
import me.vem.isle.menu.Setting;

public class Input implements KeyListener, MouseListener{
	
	public static final int TYPE_MENU = 0;
	public static final int TYPE_GAME = 1;
	public static final int TYPE_INTERCEPT = 2;
	
	private int type = 0;
	
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
	
	public void setType(int type) {
		this.type = type;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		
		if(type == TYPE_GAME) {
			if(key == Setting.MOVE_LEFT.keyCode())
				left = true;
			
			if(key == Setting.MOVE_RIGHT.keyCode())
				right = true;
			
			if(key == Setting.MOVE_UP.keyCode())
				up = true;
			
			if(key == Setting.MOVE_DOWN.keyCode())
				down = true;
	
		}
		//System.out.printf("Key %d pressed.%n", key);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		
		if(type == TYPE_MENU) {
			if(key == Setting.MOVE_UP.keyCode())
				App.menu.moveUp();
			
			if(key == Setting.MOVE_DOWN.keyCode())
				App.menu.moveDown();
			
			if(key == Setting.SELECT.keyCode())
				App.menu.select();
		}else if(type == TYPE_GAME) {
			if(key == Setting.MOVE_LEFT.keyCode())
				left = false;
			
			if(key == Setting.MOVE_RIGHT.keyCode())
				right = false;
			
			if(key == Setting.MOVE_UP.keyCode())
				up = false;
			
			if(key == Setting.MOVE_DOWN.keyCode())
				down = false;
			
			if(key == Setting.TOGGLE_DEBUG.keyCode())
				Game.DEBUG_ACTIVE = !Game.DEBUG_ACTIVE;
			
			if(key == Setting.TOGGLE_MAP.keyCode())
				map = !map;
		}
		
		//System.out.printf("Key %d released.%n", key);
	}

	@Override public void keyTyped(KeyEvent e) {}
	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
}