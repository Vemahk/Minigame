package me.vem.isle.game.entity;

import me.vem.isle.game.objects.GameObject;

public abstract class Entity extends GameObject{

	protected double moveSpeed;
	
	public Entity(double x, double y) {
		super(x, y);
		moveSpeed = 10.0;
	}
}
