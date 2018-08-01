package me.vem.isle.server.game.physics;

import me.vem.isle.server.game.objects.GameObject;
import me.vem.utils.math.Vector;

public class Physics {

	private final GameObject parent;
	
	private Vector vel;
	private Vector acc;
	
	private float mass;
	private float friction;
	
	private float speed;
	
	public Physics(GameObject parent) {
		this(parent, 1, 1f);
	}
	
	public Physics(GameObject parent, float mass) {
		this(parent, mass, 1f);
	}
	
	public Physics(GameObject parent, float mass, float speed) {
		this.parent = parent;
		this.mass = mass;
		this.speed = speed;
		
		vel = new Vector();
		acc = new Vector();
	}
	
	public Physics setMass(float m) {
		mass = m;
		return this;
	}
	
	/**
	 * f has to be between 0 and 1.
	 * @param f
	 * @return
	 */
	public Physics setFriction(float f) {
		this.friction = f;
		return this;
	}
	
	public float getFriction() {
		return friction;
	}
	
	public float getSpeed() {
		return speed;	
	}
	
	public void applyForce(Vector v) {
		acc.offset(v.getX() / mass, v.getY() / mass);
	}
	
	public void applyForce(float x, float y) {
		acc.offset(x / mass, y / mass);
	}
	
	public void update(float dt) {
		acc.offset(vel.getX() * -friction, vel.getY() * -friction);
		vel.offset(acc);
		acc.set(0,0);
		
		parent.getPos().offset(vel.getX() * dt, vel.getY() * dt);
	}
}