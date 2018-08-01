package me.vem.isle.server.game.entity;

import me.vem.isle.client.input.Setting;
import me.vem.isle.server.game.Game;
import me.vem.isle.server.game.objects.GameObject;
import me.vem.isle.server.game.world.World;
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
	
	private Player(int x, int y) {
		this(x+.5f, y+.5f);
	}
	
	private Player(float x, float y) {
		super("ent_player", x, y);
	}
	
	public String toString() {
		return String.format("Player[%s]", pos);
	}
	
	@Override
	public void update(float dt) {
		super.update(dt);
		
		boolean left = Setting.MOVE_LEFT.isPressed();
		boolean right = Setting.MOVE_RIGHT.isPressed();
		boolean up = Setting.MOVE_UP.isPressed();
		boolean down = Setting.MOVE_DOWN.isPressed();
		
		float speed = physics.getSpeed(),
			  xMove = left ^ right ? (left ? -speed : speed) : 0,
			  yMove = up ^ down ? (up ? -speed : speed) : 0;
		
		physics.setFriction(World.getInstance().getLand(pos).getFriction());
		physics.applyForce(xMove, yMove);
	}
}
