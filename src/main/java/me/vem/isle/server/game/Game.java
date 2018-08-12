package me.vem.isle.server.game;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import me.vem.isle.Logger;
import me.vem.isle.client.input.Setting;
import me.vem.isle.server.game.controller.Controller;
import me.vem.isle.server.game.controller.PlayerController;
import me.vem.isle.server.game.objects.GameObject;
import me.vem.isle.server.game.objects.Property;
import me.vem.isle.server.game.world.World;

public class Game {
	
	
	
	public static void newGame(int seed) {
		if(seed == 0)
			seed = new Random().nextInt();
		
		Random rand = new Random(seed);
		
		Property.register("tree", "player");
		registerControllers();
		
		World.createInstance(rand.nextInt());
		
		int x;
		int y;
		do {
			x = rand.nextInt(512) - 256;
			y = rand.nextInt(512) - 256;
		}while(!World.getInstance().getLand(x, y).isSand());
		
		new GameObject("ent_player", x, y);
		
		setInitialized();
		Logger.info("Game Startup Completed");
	}
	
	public static void loadGame(File f) {
		
		if(f == null) {
			f = new File("world.dat");
			if(!f.exists())
				backupOld();
		}
		
		Property.register("tree", "player");
		registerControllers();

		World.loadFrom(f);
		
		setInitialized();
		Logger.info("World loaded!");
	}
	
	public static boolean save() {
		
	}
	
	public static void backupOld() {
		
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
