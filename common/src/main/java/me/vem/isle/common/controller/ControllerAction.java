package me.vem.isle.common.controller;

public class ControllerAction {
	private boolean state;
	
	public boolean get() {
		return state;
	}
	
	public void set(boolean on) {
		state = on;
	}
	
	public boolean toggle() {
		return state = !state;
	}
}
