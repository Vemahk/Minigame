package me.vem.isle.common.controller;

import me.vem.isle.common.objects.GameObject;

public class PlayerController extends Controller{
	
	public static final int MOVE_LEFT = 0;
	public static final int MOVE_RIGHT = 1;
	public static final int MOVE_UP = 2;
	public static final int MOVE_DOWN = 3;
	
	protected PlayerController(GameObject parent) { 
		super(parent);
		
		actions.put(MOVE_LEFT, new ControllerAction());
		actions.put(MOVE_RIGHT, new ControllerAction());
		actions.put(MOVE_UP, new ControllerAction());
		actions.put(MOVE_DOWN, new ControllerAction());
	}
	
	@Override
	public void update(float dt) {
		boolean left = this.getActionState(MOVE_LEFT);
		boolean right = this.getActionState(MOVE_RIGHT);
		boolean up = this.getActionState(MOVE_UP);
		boolean down = this.getActionState(MOVE_DOWN);
		
		float speed = parent.getPhysics().getSpeed(),
			  xMove = left ^ right ? (left ? -speed : speed) : 0,
			  yMove = up ^ down ? (up ? -speed : speed) : 0;
		
		parent.getPhysics().setFriction(parent.getWorld().getLand(parent.getPos()).getFriction());
		parent.getPhysics().applyForce(xMove, yMove);
	}
}
