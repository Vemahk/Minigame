package me.vem.isle.client.graphics;

import me.vem.isle.common.controller.Controller;
import me.vem.isle.common.controller.PlayerController;
import me.vem.isle.common.objects.GameObject;
import me.vem.isle.common.world.World;

public class CameraAnchor extends GameObject {
	
	private GameObject link;
	
	public CameraAnchor(World world, float x, float y) {
		super(world, "empty", x, y);
	}
	
	public void linkTo(GameObject link) {
		this.link = link;
	}
	
	public void follow() {
		this.getPos().lerpTo(link.getPos(), .1f);
	}
	
	public PlayerController getPlayerController() {
		Controller controller = link.getController();
		
		if(controller == null)
			return null;
		
		if(!(controller instanceof PlayerController)) 
			return null;
		
		return (PlayerController) controller;
	}
}
