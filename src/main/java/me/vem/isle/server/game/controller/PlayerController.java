package me.vem.isle.server.game.controller;

import me.vem.isle.client.input.Setting;
import me.vem.isle.server.game.objects.GameObject;
import me.vem.isle.server.game.world.World;

public class PlayerController extends Controller{
	
	protected PlayerController(GameObject parent) { super(parent); }
	
	@Override
	public void update(float dt) {
		boolean left = Setting.MOVE_LEFT.isPressed();
		boolean right = Setting.MOVE_RIGHT.isPressed();
		boolean up = Setting.MOVE_UP.isPressed();
		boolean down = Setting.MOVE_DOWN.isPressed();
		
		float speed = parent.getPhysics().getSpeed(),
			  xMove = left ^ right ? (left ? -speed : speed) : 0,
			  yMove = up ^ down ? (up ? -speed : speed) : 0;
		
		parent.getPhysics().setFriction(World.getInstance().getLand(parent.getPos()).getFriction());
		parent.getPhysics().applyForce(xMove, yMove);
	}

	/**
	 * Look at this trash.
	 * Courtesy of SolfKimblee
	 */
	@Override public boolean setRUID(int RUID) { return getRUID() > 0 ? false : (this.RUID = RUID) == RUID; }

	@Override public int getRUID() { return this.RUID; }
}
