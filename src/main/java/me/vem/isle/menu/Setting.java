package me.vem.isle.menu;

import java.awt.event.KeyEvent;

public class Setting {

	public static Setting MOVE_UP = new Setting("Move Up", KeyEvent.VK_UP);
	public static Setting MOVE_DOWN = new Setting("Move Down", KeyEvent.VK_DOWN);
	public static Setting MOVE_LEFT = new Setting("Move Left", KeyEvent.VK_LEFT);
	public static Setting MOVE_RIGHT = new Setting("Move Right", KeyEvent.VK_RIGHT);
	
	public static Setting SELECT = new Setting("Menu - Select", KeyEvent.VK_ENTER);
	
	public static Setting TOGGLE_MAP = new Setting("Toggle Map", KeyEvent.VK_M);
	public static Setting TOGGLE_DEBUG = new Setting("Toggle Debug", KeyEvent.VK_F3);
	public static Setting EXIT_GAME = new Setting("Quick Close", KeyEvent.VK_ESCAPE);
	
	private int key;
	private String display;
	
	private Setting(String display, int initKey) {
		this.key = initKey;
		this.display = display;
	}
	
	public void setKeyCode(int key) {
		this.key = key;
	}
	
	public int keyCode() {
		return key;
	}
	
	public String getKeyDisplay() {
		return KeyEvent.getKeyText(key);
	}
	
	public String getDisplay() {
		return display;
	}
	
}