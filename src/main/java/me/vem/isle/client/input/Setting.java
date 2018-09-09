package me.vem.isle.client.input;

import java.awt.event.KeyEvent;
import java.util.LinkedList;

public class Setting {

	public static LinkedList<Setting> all = new LinkedList<>();
	
	public static Setting MOVE_UP = new Setting("Move Up", KeyEvent.VK_UP);
	public static Setting MOVE_DOWN = new Setting("Move Down", KeyEvent.VK_DOWN);
	public static Setting MOVE_LEFT = new Setting("Move Left", KeyEvent.VK_LEFT);
	public static Setting MOVE_RIGHT = new Setting("Move Right", KeyEvent.VK_RIGHT);
	
	public static Setting SELECT = new Setting("Menu - Select", KeyEvent.VK_ENTER);
	public static Setting EXIT_GAME = new Setting("Quick Close", KeyEvent.VK_ESCAPE, ActionSet.SHUTDOWN);
	
	public static Setting TOGGLE_MAP = new Setting("Toggle Map", KeyEvent.VK_M);
	public static Setting TOGGLE_DEBUG = new Setting("Toggle Debug", KeyEvent.VK_F3, ActionSet.TOGGLE_DEBUG);
	
	private byte state;
	
	private int key;
	private String display;
	
	private Runnable action;
	
	private Setting(String display, int initKey, Runnable action) {
		this.key = initKey;
		this.display = display;
		this.action = action;
		
		all.add(this);
	}
	
	private Setting(String display, int initKey) {
		this(display, initKey, null);
	}
	
	public Setting setAction(Runnable r) {
		this.action = r;
		return this;
	}
	
	public void run() {
		if(action == null)
			return;
		action.run();
	}
	
	public void setKeyCode(int key) { this.key = key; }

	public boolean isPressed() { return (state & 1) != 0; }
	public boolean isToggled() { return (state & 2) != 0; }
	
	public void toggle() { state ^= 2; }
	public void setPressed() { state |= 1; }
	public void setReleased() { state &= 0xFE; }
	
	public void resetAction() { this.action = null; }
	
	public int getKeyCode() { return key; }
	public String getKeyDisplay() { return KeyEvent.getKeyText(key); }
	public String getDisplay() { return display; }
	
}

