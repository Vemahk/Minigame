package me.vem.isle.game.physics;

import me.vem.isle.game.objects.GameObject;

public class PhysicsBody {

	private final GameObject parent;
	
	private Vector vel;
	private Vector appliedForce;
	
	private double mass;
	
	private double friction;
	private double frictionMult; // Friction Multiplier
	
	public PhysicsBody(GameObject parent) {
		this.parent = parent;
		mass = 1;
		friction = .3;
		
		vel = new Vector();
		appliedForce = new Vector();
	}
	
	public PhysicsBody setMass(double m) {
		mass = m;
		return this;
	}
	
	/**
	 * f has to be between 0 and 1.
	 * @param f
	 * @return
	 */
	public PhysicsBody setFriction(double f) {
		this.friction = f;
		return this;
	}
	
	public void setFrictionMod(double fm) {
		this.frictionMult = fm;
	}
	
	public double getFriction() {
		return friction;
	}
	
	public void applyForce(Vector v) {
		appliedForce = appliedForce.add(v);
	}
	
	public void update(int ups) {
		
		// Velocity + Fa/Mass - Velocity * Mu
		
		vel = vel.add(appliedForce.scale(1 / mass).sub(vel.scale(friction * frictionMult)));
		appliedForce = new Vector(); //Reset applied force.
		Vector dispos = vel.scale(1.0 / ups); //Scale to updates per second so you don't ZOOOOOM
		parent.moveBy(dispos.getX(), dispos.getY());
	}
}