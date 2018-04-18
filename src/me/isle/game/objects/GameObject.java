package me.isle.game.objects;

import java.util.HashSet;

public abstract class GameObject implements Drawable{

	public static HashSet<GameObject> all = new HashSet<>();
	
	protected double x;
	protected double y;
	
	public GameObject(double x, double y) {
		setPos(x, y);
	}
	
	public GameObject initialize() {
		
		synchronized(all) {
			all.add(this);
		}
		
		return this;
	}
	
	public double getX() { return x; }
	public double getY() { return y; }
	
	public void setPos(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public void moveBy(double x, double y) {
		this.x += x;
		this.y += y;
	}
	
	public abstract void update(int tr);
}
