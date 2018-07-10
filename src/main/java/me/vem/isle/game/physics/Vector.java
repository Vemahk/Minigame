package me.vem.isle.game.physics;

import me.vem.isle.game.objects.GameObject;

public final class Vector {

	private final double x;
	private final double y;
	
	public Vector() { this(0, 0); }
	
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() { return x; }
	public double getY() { return y; }
	
	public double getMagnetude() {
		return Math.sqrt(x * x + y * y);
	}
	
	public Vector add(Vector v) {
		return new Vector(x + v.x, y + v.y);
	}
	
	public Vector sub(Vector v) {
		return new Vector(x - v.x, y - v.y);
	}
	
	public Vector scale(double scalar) {
		return new Vector(x * scalar, y * scalar);
	}
	
	public double dotProduct(Vector v) { 
		return x * v.x + y * v.y;
	}
	
	public Vector inverseMag(double numerator) {
		double nx = this.x;
		double ny = this.y;
		
		double mag = getMagnetude();
		double prop = numerator / mag / mag;
		
		nx *= prop;
		ny *= prop;
		
		return new Vector(nx, ny);
	}
	
	/**
	 * Creates an x-y Vector based on a polar vector of magnitude r and direction th (theta).
	 * @param r Magnitude
	 * @param th Angle (in radians)
	 * @return
	 */
	public static Vector fromPolar(double r, double th) {
		return new Vector(r * Math.cos(th), r * Math.sin(th));
	}
	
	public static Vector pointDiff(double x1, double y1, double x2, double y2) {
		return new Vector(x1 - x2, y1-y2);
	}
	
	public static Vector toVector(GameObject go) {
		return new Vector(go.getX(), go.getY());
	}
	
	public static Vector posDiff(GameObject g1, GameObject g2) {
		return toVector(g1).sub(toVector(g2));
	}
}