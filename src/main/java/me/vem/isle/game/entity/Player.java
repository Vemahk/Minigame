package me.vem.isle.game.entity;

import me.vem.isle.game.Game;
import me.vem.isle.game.objects.GameObject;
import me.vem.isle.game.world.World;
import me.vem.isle.menu.Setting;
import me.vem.utils.math.Vector;

public class Player extends GameObject{
	
	private static Player instance;
	public static Player getInstance() {
		if(instance == null) {
			if(!Game.initialized) return null;
			int x;
			int y;
			do {
				x = Game.rand.nextInt(512) - 256;
				y = Game.rand.nextInt(512) - 256;
			}while(!World.getInstance().getLand(x, y).isSand());
			
			instance = (Player) GameObject.instantiate(new Player(x, y));
			//Camera.getInstance().setTarget(instance, true);
		}
		return instance;
	}
	
	private Player(float x, float y) {
		super("ent_player", x, y);
	}
	
	@Override
	public void update(int tr) {
		super.update(tr);
		
		int ix = pos.floorX();
		int iy = pos.floorY();
		
		boolean left = Setting.MOVE_LEFT.isPressed();
		boolean right = Setting.MOVE_RIGHT.isPressed();
		boolean up = Setting.MOVE_UP.isPressed();
		boolean down = Setting.MOVE_DOWN.isPressed();
		
		int xMove = left ^ right ? (left ? -1 : 1) : 0;
		int yMove = up ^ down ? (up ? -1 : 1) : 0;
		
		if(World.getInstance().getLand(ix, iy).isWater())		
			this.getPhysics().setFriction(1 - physics.getDefaultFriction());
		else this.getPhysics().setFriction(physics.getDefaultFriction());
		
		float Fx = xMove * physics.getSpeed();
		float Fy = yMove * physics.getSpeed();
		this.physics.applyForce(new Vector(Fx, Fy));
	}
}
