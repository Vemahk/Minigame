package me.vem.isle.common.physics.collider;

import me.vem.isle.common.objects.GameObject;

public interface Collider {
	public boolean collidedWith(Collider c);
	
	public Collider copy(GameObject nParent);
}