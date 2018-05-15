package me.isle.game.physics;

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
	
	/**
	 * Creates an x-y Vector based on a polar vector of magnitude r and direction th (theta).
	 * @param r Magnitude
	 * @param th Angle (in radians)
	 * @return
	 */
	public static Vector fromPolar(double r, double th) {
		return new Vector(r * Math.cos(th), r * Math.sin(th));
	}
}