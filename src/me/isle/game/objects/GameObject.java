package me.isle.game.objects;

import java.util.TreeSet;

public abstract class GameObject implements Drawable, Comparable<GameObject>{
	
	public static TreeSet<GameObject> all = new TreeSet<>();
	
	public static GameObject instantiate(GameObject go) {
		synchronized(all) {
			all.add(go);
		}
		
		return go;
	}
	
	protected double x;
	protected double y;
	
	protected double z;
	
	public GameObject(double x, double y) {
		setPos(x, y);
		setZ(0);
	}
	
	public double getX() { return x; }
	public double getY() { return y; }
	
	public void setPos(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public GameObject setZ(double z) {
		this.z = z;
		return this;
	}
	
	public void moveBy(double x, double y) {
		this.x += x;
		this.y += y;
	}
	
	public abstract void update(int tr);
	
	public int compareTo(GameObject o) {
		if(z == o.z)
			return this.hashCode() - o.hashCode();
		
		return (int)Math.signum(z - o.z);
	}
}
