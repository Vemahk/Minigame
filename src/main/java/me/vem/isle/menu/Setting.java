package me.vem.isle.menu;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.LinkedList;

import me.vem.isle.App;
import me.vem.isle.Logger;

public class Setting {

	public static LinkedList<Setting> all = new LinkedList<>();
	public static HashSet<Setting> actionPressed = new HashSet<>();
	public static HashSet<Setting> actionReleased = new HashSet<>();
	
	public static final int TYPE_TOGGLE = 0;
	public static final int TYPE_ONPRESS = 1;
	public static final int TYPE_ONRELEASE = 2;
	
	public static Setting MOVE_UP = new Setting("Move Up", KeyEvent.VK_UP, TYPE_ONRELEASE)
									.setAction(() -> App.menu.moveUp());
	
	public static Setting MOVE_DOWN = new Setting("Move Down", KeyEvent.VK_DOWN, TYPE_ONRELEASE)
									.setAction(() -> App.menu.moveDown());
	
	public static Setting MOVE_LEFT = new Setting("Move Left", KeyEvent.VK_LEFT, TYPE_ONRELEASE);
	public static Setting MOVE_RIGHT = new Setting("Move Right", KeyEvent.VK_RIGHT, TYPE_ONRELEASE);
	
	public static Setting SELECT = new Setting("Menu - Select", KeyEvent.VK_ENTER, TYPE_ONRELEASE)
									.setAction(() -> App.menu.select());
	public static Setting EXIT_GAME = new Setting("Quick Close", KeyEvent.VK_ESCAPE, TYPE_ONRELEASE)
									.setAction(() -> App.shutdown());
	
	public static Setting TOGGLE_MAP = new Setting("Toggle Map", KeyEvent.VK_M, TYPE_TOGGLE);
	public static Setting TOGGLE_DEBUG = new Setting("Toggle Debug", KeyEvent.VK_F3, TYPE_TOGGLE);
	
	private boolean state;
	
	private int type;
	private int key;
	private String display;
	
	private Runnable run;
	
	private Setting(String display, int initKey, int type) {
		this.key = initKey;
		this.display = display;
		this.type = type;
		
		all.add(this);
		if(type == 1)
			actionPressed.add(this);
		else
			actionReleased.add(this);
	}
	
	public Setting setAction(Runnable r) {
		this.run = r;
		return this;
	}
	
	public void run() {
		if(run == null)
			return;
		run.run();
	}
	
	public void setKeyCode(int key) { this.key = key; } 
	
	public boolean getState() { return state; }
	public void toggleState() {  state = !state; }

	public void setOn() { this.state = true; }
	public void setOff() { this.state = false; }
	
	public int getKeyCode() { return key; }
	public String getKeyDisplay() { return KeyEvent.getKeyText(key); }
	public String getDisplay() { return display; }
	public boolean isType(int t) { return this.type == t; }
	
}