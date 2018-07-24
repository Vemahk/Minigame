package me.vem.isle.game;

import java.util.Random;

import org.dom4j.DocumentException;

import me.vem.isle.game.entity.Player;
import me.vem.isle.game.objects.Property;
import me.vem.isle.game.world.World;
import me.vem.isle.menu.Setting;

public class Game {

	public static boolean initialized = false;
	
	public static Random rand;
	
	public static void gameStartup() {
		if(rand == null)
			rand = new Random();
		
		try {
			Property.register("tree.xml");
			Property.register("player.xml");
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		World.getInstance();
		Player.getInstance();
		
		initialized = true;
	}
	
	public static void gameStartup(long seed) { 
		rand = new Random(seed);
		gameStartup();
	}
	
	public static boolean isDebugActive() {
		return Setting.TOGGLE_DEBUG.isToggled();
	}
}
