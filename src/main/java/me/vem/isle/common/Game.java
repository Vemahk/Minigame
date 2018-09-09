package me.vem.isle.common;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import me.vem.isle.common.controller.Controller;
import me.vem.isle.common.controller.PlayerController;
import me.vem.isle.common.eio.ExtResourceManager;
import me.vem.isle.common.objects.GameObject;
import me.vem.isle.common.objects.Property;
import me.vem.isle.common.world.World;
import me.vem.utils.logging.Logger;

public class Game {
	
	private static void init() {
		Property.register("tree", "player");
		registerControllers();
		
	}
	
	private static void postinit() {
		setInitialized();
		Logger.info("Game Startup Completed");
	}
	
	private static File worldFile;
	
	public static void newGame(int seed) {
		init();
		
		if(seed == 0)
			seed = new Random().nextInt();
		
		Random rand = new Random(seed);
		
		World.createInstance(rand.nextInt());
		
		int x;
		int y;
		do {
			x = rand.nextInt(512) - 256;
			y = rand.nextInt(512) - 256;
		}while(!World.getInstance().getLand(x, y).isSand());
		
		new GameObject("ent_player", x, y);
		
		postinit();
	}
	
	public static void loadGame(File f) {
		init();
		
		World.loadFrom(f);
		worldFile = f;
		
		Logger.infof("World file '%s' loaded!", f.getPath());
		postinit();
	}
	
	public static boolean save() {
		if(worldFile == null) {
			worldFile = ExtResourceManager.getDefaultWorldFile();
			
			File bckDir = ExtResourceManager.getBackupsDirectory();
			String newFileName = "world" + bckDir.listFiles().length + ".dat.bck";
			worldFile.renameTo(new File(bckDir, newFileName));
			worldFile.delete();
		}
		
		return World.saveTo(worldFile);
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
	
	private static boolean debugActive;
	public static void toggleDebugMode() {
		debugActive = !debugActive;
	}
	
	public static boolean isDebugActive() {
		return debugActive;
	}
}
