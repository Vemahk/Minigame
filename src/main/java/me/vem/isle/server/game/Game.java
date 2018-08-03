package me.vem.isle.server.game;

import static me.vem.isle.Logger.info;

import java.util.Random;

import org.dom4j.DocumentException;

import me.vem.isle.client.input.Setting;
import me.vem.isle.server.game.controller.Controller;
import me.vem.isle.server.game.controller.PlayerController;
import me.vem.isle.server.game.objects.GameObject;
import me.vem.isle.server.game.objects.Property;
import me.vem.isle.server.game.world.World;

public class Game {
	
	public static void startup(int seed) {
		
		registerObjectProperties();
		registerControllers();
		
		World.createInstance(seed);
		
		Random rand = new Random();
		int x;
		int y;
		do {
			x = rand.nextInt(512) - 256;
			y = rand.nextInt(512) - 256;
		}while(!World.getInstance().getLand(x, y).isSand());
		
		player = GameObject.instantiate(new GameObject("ent_player", x, y));
		
		setInitialized();
		info("Game started.");
	}
	
	public static void startup() {
		startup(new Random().nextInt());
	}
	
	public static void registerObjectProperties() {
		try {
			Property.register("tree");
			Property.register("player");
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	public static void registerControllers() {
		Controller.register("plr_controller", PlayerController.class);
	}
	
	private static GameObject player;
	public static GameObject getPlayer() { return player; }
	
	private static boolean initialized;
	public static boolean isInitialized() { return initialized; }
	public static void setInitialized(){initialized = true;}
	
	public static boolean isDebugActive() {
		return Setting.TOGGLE_DEBUG.isToggled();
	}
}
