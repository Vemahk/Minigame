package me.vem.isle.game.physics;

import me.vem.isle.game.objects.GameObject;

public class Physics {

	private final GameObject parent;
	
	private Vector vel;
	private Vector appliedForce;
	
	private float mass;
	
	private final float defFrict;
	private float friction;
	
	private float speed;
	
	public Physics(GameObject parent) {
		this(parent, 1, .3f, 1f);
	}
	
	public Physics(GameObject parent, float mass) {
		this(parent, mass, .3f, 1f);
	}
	
	public Physics(GameObject parent, float mass, float friction) {
		this(parent, mass, friction, 1f);
	}
	
	public Physics(GameObject parent, float mass, float friction, float speed) {
		this.parent = parent;
		this.mass = mass;
		this.defFrict = friction;
		this.friction = friction;
		this.speed = speed;
		
		vel = new Vector();
		appliedForce = new Vector();
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
	
	public float getDefaultFriction() {
		return defFrict;
	}
	
	public float getFriction() {
		return friction;
	}
	
	public float getSpeed() {
		return speed;	
	}
	
	public void applyForce(Vector v) {
		appliedForce = appliedForce.add(v);
	}
	
	public void update(int ups) {
		
		// Velocity + Fa/Mass - Velocity * Mu
		
		vel = vel.add(appliedForce.scale(1 / mass).sub(vel.scale(friction)));
		appliedForce = new Vector(); //Reset applied force.
		Vector dispos = vel.scale(1f / ups); //Scale to updates per second so you don't ZOOOOOM
		parent.move(dispos.getX(), dispos.getY());
	}
}