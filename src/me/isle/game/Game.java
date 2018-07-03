package me.isle.game;

import java.util.Random;

import me.isle.Startup;
import me.isle.game.entity.PlayerEntity;
import me.isle.game.event.CollisionListener;
import me.isle.game.event.EventHandler;
import me.isle.game.objects.GameObject;
import me.isle.game.world.World;

public class Game {

	public static boolean initialized = false;
	
	public static boolean DEBUG_ACTIVE = false;
	public static Random rand;
	public static World world;
	
	public static void gameStartup(int w, int h) {
		if(rand == null)
			rand = new Random();
		
		world = new World(w, h);
		
		int x = w / 4 + rand.nextInt(w / 2);
		int y = h / 4 + rand.nextInt(h / 2);
		while(world.isWater(x, y)) {
			x = w/4 + rand.nextInt(w/2);
			y = h/4 + rand.nextInt(h/2);
		}
		
		player = (PlayerEntity) GameObject.instantiate(new PlayerEntity(x, y));
		player.givePhysicsBody(2.5);
		player.setCollider(1, 1);
		Startup.getCamera().setTarget(player, true);
		
		events = new EventHandler();
		events.registerListener(new CollisionListener());
		
		initialized = true;
	}
	
	public static void gameStartup(int w, int h, long seed) { 
		rand = new Random(seed);
		gameStartup(w, h);
	}
	
	private static ArrowKeyListener akl;
	private static EventHandler events;
	
	private static PlayerEntity player;
	
	
	public static int getWidth() { return world.getWidth(); }
	public static int getHeight() { return world.getHeight(); }
	
	public static World getWorld() { return world; }
	public static PlayerEntity getPlayer() { return player; }
	
	public static ArrowKeyListener getKeyListener() {
		return akl;
	}
	
	public static ArrowKeyListener setKeyListener(ArrowKeyListener akl) {
		return Game.akl = akl;
	}
	
	public static EventHandler getEventHandler() {
		return events;
	}
}
