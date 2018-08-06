package me.vem.isle.server.game.physics.collider;

import me.vem.isle.server.game.objects.GameObject;

public interface Collider {
	public boolean collidedWith(Collider c);
	
	public Collider copy(GameObject nParent);
}