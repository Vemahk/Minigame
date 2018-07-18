package me.vem.isle.game;

import java.util.Random;

import me.vem.isle.App;
import me.vem.isle.game.entity.PlayerEntity;
import me.vem.isle.game.event.CollisionListener;
import me.vem.isle.game.event.EventHandler;
import me.vem.isle.game.objects.GameObject;
import me.vem.isle.game.world.World;
import me.vem.isle.menu.Setting;

public class Game {

	public static boolean initialized = false;
	
	public static Random rand;
	public static World world;
	
	public static void gameStartup() {
		if(rand == null)
			rand = new Random();
		
		world = new World();
		
		/*int x;
		int y;
		do {
			x = rand.nextInt(512) - 256;
			y = rand.nextInt(512) - 256;
		}while(world.isWater(x, y));
		
		player = (PlayerEntity) GameObject.instantiate(new PlayerEntity(x, y));*/
		player = (PlayerEntity) GameObject.instantiate(new PlayerEntity(0, 0));
		player.setCollider(1, 1);
		App.getCamera().setTarget(player, true);
		
		events = new EventHandler();
		events.registerListener(new CollisionListener());
		
		initialized = true;
	}
	
	public static void gameStartup(long seed) { 
		rand = new Random(seed);
		gameStartup();
	}
	
	private static Input input;
	private static EventHandler events;
	
	private static PlayerEntity player;
	
	public static World getWorld() { return world; }
	public static PlayerEntity getPlayer() { return player; }
	
	public static Input getInput() {
		return input;
	}
	
	public static Input setInput(Input input) {
		return Game.input = input;
	}
	
	public static EventHandler getEventHandler() {
		return events;
	}
	
	public static boolean isDebugActive() {
		return Setting.TOGGLE_DEBUG.getState();
	}
}
