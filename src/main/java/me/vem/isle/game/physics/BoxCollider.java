package me.vem.isle.game.physics;

import me.vem.isle.game.objects.GameObject;

public class BoxCollider implements Collider{

	private final GameObject parent;
	
	private double width;
	private double height;
	
	public BoxCollider(GameObject parent) {
		this(parent, 1, 1);
	}
	
	public BoxCollider(GameObject parent, double width, double height) {
		this.parent = parent;
		this.width = width;
		this.height = height;
	}
	
	public double getWidth() { return width; }
	public double getHeight() { return height; }
	
	public boolean collidedWith(Collider c) {
		
		if(c instanceof BoxCollider) {
			BoxCollider o = (BoxCollider) c;
			
			double dx = Math.abs(this.parent.getX() - o.parent.getX());
			double dy = Math.abs(this.parent.getY() - o.parent.getY());
			
			return (width / 2 + o.width / 2) >= dx && (height + o.height) / 2 >= dy;
		}
		
		return false;
	}
	
}
