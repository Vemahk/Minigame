package me.vem.isle.game.physics;

import me.vem.isle.game.entity.Entity;
import me.vem.isle.game.objects.GameObject;

public class PhysicsBody {

	private final Entity parent;
	
	private Vector vel;
	private Vector appliedForce;
	
	private float mass;
	
	private float friction;
	
	public PhysicsBody(Entity parent) {
		this(parent, 1, .3f);
	}
	
	public PhysicsBody(Entity parent, float mass) {
		this(parent, mass, .3f);
	}
	
	public PhysicsBody(Entity parent, float mass, float friction) {
		this.parent = parent;
		this.mass = mass;
		this.friction = friction;
		
		vel = new Vector();
		appliedForce = new Vector();
	}
	
	public PhysicsBody setMass(float m) {
		mass = m;
		return this;
	}
	
	/**
	 * f has to be between 0 and 1.
	 * @param f
	 * @return
	 */
	public PhysicsBody setFriction(float f) {
		this.friction = f;
		return this;
	}
	
	public float getFriction() {
		return friction;
	}
	
	public void applyForce(Vector v) {
		appliedForce = appliedForce.add(v);
	}
	
	public void update(int ups) {
		
		// Velocity + Fa/Mass - Velocity * Mu
		
		vel = vel.add(appliedForce.scale(1 / mass).sub(vel.scale(friction)));
		appliedForce = new Vector(); //Reset applied force.
		Vector dispos = vel.scale(1f / ups); //Scale to updates per second so you don't ZOOOOOM
		parent.offset(dispos.getX(), dispos.getY());
	}
}