package me.isle.game;

import java.util.Random;

import me.isle.game.entity.PlayerEntity;
import me.isle.game.objects.GameObject;
import me.isle.game.physics.BoxCollider;
import me.isle.game.world.World;
import me.isle.graphics.ArrowKeyListener;

public class Game {

	public static boolean DEBUG_ACTIVE = false;
	public static Game game;
	public static Random rand = new Random();
	
	private World world;
	
	private ArrowKeyListener akl;
	
	private PlayerEntity player;
	
	public Game(int w, int h) {
		world = new World(w, h);
	}
	
	public void build() {
		world.build();
		
		int w = world.getWidth();
		int h = world.getHeight();
		int x = w / 4 + rand.nextInt(w / 2);
		int y = h / 4 + rand.nextInt(h / 2);
		while(world.isWater(x, y)) {
			x = w/4 + rand.nextInt(w/2);
			y = h/4 + rand.nextInt(h/2);
		}
		
		player = (PlayerEntity) GameObject.instantiate(new PlayerEntity(x, y));
		player.givePhysicsBody(2.5);
		player.setCollider(1, 1);
	}
	
	public int getWidth() { return world.getWidth(); }
	public int getHeight() { return world.getHeight(); }
	
	public World getWorld() { return world; }
	public PlayerEntity getPlayer() { return player; }
	
	public ArrowKeyListener getKeyListener() {
		return akl;
	}
	
	public ArrowKeyListener setKeyListener(ArrowKeyListener akl) {
		this.akl = akl;
		return akl;
	}
}
