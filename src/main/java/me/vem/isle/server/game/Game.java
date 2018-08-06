package me.vem.isle.server.game;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.dom4j.DocumentException;

import me.vem.isle.Logger;
import me.vem.isle.client.input.Setting;
import me.vem.isle.server.game.controller.Controller;
import me.vem.isle.server.game.controller.PlayerController;
import me.vem.isle.server.game.objects.GameObject;
import me.vem.isle.server.game.objects.Property;
import me.vem.isle.server.game.world.World;

public class Game {
	
	private static Random rand;
	public static Random random() { return rand; }
	
	public static void newGame(int seed) {
		rand = new Random(seed);
		
		registerObjectProperties();
		registerControllers();
		
		World.createInstance(rand.nextInt());
		
		int x;
		int y;
		do {
			x = rand.nextInt(512) - 256;
			y = rand.nextInt(512) - 256;
		}while(!World.getInstance().getLand(x, y).isSand());
		
		GameObject.instantiate(new GameObject("ent_player", x, y));
		
		setInitialized();
		Logger.info("Game Startup Completed");
	}
	
	public static void newGame() {
		newGame(new Random().nextInt());
	}
	
	public static void loadGame(File f) {
		rand = new Random(new Random().nextInt());
		
		registerObjectProperties();
		registerControllers();
		
		World.load(f);
		
		setInitialized();
		Logger.info("World loaded!");
	}
	
	public static void loadGame() {
		rand = new Random(new Random().nextInt());
		
		registerObjectProperties();
		registerControllers();
		
		World.load();
		
		setInitialized();
		Logger.info("World loaded!");
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

	private static Map<Integer, RIdentifiable> byRUID = Collections.synchronizedMap(new HashMap<>());
	
	private static int nextRUID = 1;
	public static void requestRUID(RIdentifiable rid) {
		if(rid.setRUID(nextRUID))
			byRUID.put(nextRUID++, rid);
	}
	
	public static RIdentifiable getByRUID(int RUID) {
		return byRUID.get(RUID);
	}
	
	private static GameObject player;
	public static GameObject getPlayer() { return player; }
	public static void setPlayer(GameObject go) { player = go; }
	
	private static boolean initialized;
	public static boolean isInitialized() { return initialized; }
	public static void setInitialized(){initialized = true;}
	
	public static boolean isDebugActive() {
		return Setting.TOGGLE_DEBUG.isToggled();
	}
}
