package me.vem.isle.game.physics;

import me.vem.isle.game.objects.GameObject;
import me.vem.utils.math.Vector;

public class BoxCollider implements Collider{

	private final GameObject parent;
	
	private Vector dim;
	
	public BoxCollider(GameObject parent) {
		this(parent, 1, 1);
	}
	
	public BoxCollider(GameObject parent, float width, float height) {
		this.parent = parent;
		dim = new Vector(width, height);
	}
	
	public double getWidth() { return dim.getX(); }
	public double getHeight() { return dim.getY(); }
	
	public boolean collidedWith(Collider c) {
		
		if(c instanceof BoxCollider) {
			BoxCollider o = (BoxCollider) c;
			
			double dx = Math.abs(this.parent.getX() - o.parent.getX());
			double dy = Math.abs(this.parent.getY() - o.parent.getY());
			
			return (getWidth() + o.getWidth()) / 2 >= dx && (getHeight() + o.getHeight()) / 2 >= dy;
		}
		
		return false;
	}
	
}
