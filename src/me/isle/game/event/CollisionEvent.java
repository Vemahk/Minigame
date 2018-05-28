package me.isle.game.event;

import me.isle.game.objects.GameObject;

public class CollisionEvent extends Event{

	private GameObject a;
	private GameObject b;
	
	public CollisionEvent(GameObject obj1, GameObject obj2) {
		a = obj1;
		b = obj2;
	}
	
	public GameObject getObjectA() { return a; }
	public GameObject getObjectB() { return b; }
	
}