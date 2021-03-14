package me.vem.isle.client.graphics;

import me.vem.isle.common.controller.Controller;
import me.vem.isle.common.controller.PlayerController;
import me.vem.isle.common.objects.GameObject;
import me.vem.isle.common.world.World;
import me.vem.utils.math.Vector;

public class CameraAnchor {
	
	private GameObject link;
	
	private final World world;
	private final Vector pos;
	
	public CameraAnchor(World world, float x, float y) {
		this.world = world;
		this.pos = new Vector(x,y);
	}
	
	public void linkTo(GameObject link) {
		this.link = link;
	}
	
	public void follow() {
		this.getPos().lerpTo(link.getPos(), .1f);
	}
	
	public World getWorld() {
		return this.world;
	}
	
	public PlayerController getPlayerController() {
		Controller controller = link.getController();
		
		if(controller == null)
			return null;
		
		if(!(controller instanceof PlayerController)) 
			return null;
		
		return (PlayerController) controller;
	}
	
	public Vector getPos() {
		return pos;
	}
}
