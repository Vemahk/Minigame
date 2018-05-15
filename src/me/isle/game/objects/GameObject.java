package me.isle.game.objects;

import java.util.HashSet;
import java.util.TreeSet;

import me.isle.game.physics.BoxCollider;
import me.isle.game.physics.Collider;
import me.isle.game.physics.PhysicsBody;

public abstract class GameObject implements Drawable, Comparable<GameObject>{
	
	public static TreeSet<GameObject> all = new TreeSet<>();
	
	public static GameObject instantiate(GameObject go) {
		synchronized(all) {
			all.add(go);
		}
		
		return go;
	}
	
	public static HashSet<GameObject> queuedToDestroy = new HashSet<>();
	public static boolean destroy(GameObject go) {
		if(!all.contains(go)) return false;
		
		queuedToDestroy.add(go);
		return true;
	}
	
	protected double x;
	protected double y;
	
	protected double z;
	
	protected PhysicsBody pBody;
	protected Collider collider;
	
	public GameObject(double x, double y) {
		setPos(x, y);
		setZ(0);
	}
	
	public PhysicsBody givePhysicsBody() {
		return givePhysicsBody(1);
	}
	
	public PhysicsBody givePhysicsBody(double mass) {
		return pBody = new PhysicsBody(this).setMass(mass);
	}
	
	public GameObject setCollider(double w, double h) {
		this.collider = new BoxCollider(this, w, h);
		return this;
	}
	
	public boolean hasCollider() { return collider != null; }
	
	public boolean hasCollidedWith(GameObject go) {
		return collider.collidedWith(go.collider);
	}
	
	public Collider getCollider() { return collider; }
	
	public boolean withinDistanceOf(GameObject o, double dist) {
		double dx = x - o.x;
		double dy = y - o.y;
		
		return dx * dx + dy * dy <= dist * dist;
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
	
	public void update(int tr) {
		if(pBody != null)
			pBody.update(tr);
	}
	
	public int compareTo(GameObject o) {
		if(z == o.z)
			return this.hashCode() - o.hashCode();
		
		return (int)Math.signum(z - o.z);
	}
}
