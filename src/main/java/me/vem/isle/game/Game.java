package me.vem.isle.game;

import java.util.Random;

import org.dom4j.DocumentException;

import me.vem.isle.App;
import me.vem.isle.game.entity.PlayerEntity;
import me.vem.isle.game.objects.GameObject;
import me.vem.isle.game.objects.Property;
import me.vem.isle.game.objects.Tree;
import me.vem.isle.game.world.World;
import me.vem.isle.menu.Setting;

public class Game {

	public static boolean initialized = false;
	
	public static Random rand;
	public static World world;
	
	public static void gameStartup() {
		if(rand == null)
			rand = new Random();
		
		try {
			Property.register(Tree.class, "tree.xml");
			Property.register(PlayerEntity.class, "player.xml");
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		world = new World();
		
		int x;
		int y;
		do {
			x = rand.nextInt(512) - 256;
			y = rand.nextInt(512) - 256;
		}while(world.isWater(x, y));
		
		player = (PlayerEntity) GameObject.instantiate(new PlayerEntity(x, y));
		player.setCollider(1, 1);
		App.getCamera().setTarget(player, true);
		
		initialized = true;
	}
	
	public static void gameStartup(long seed) { 
		rand = new Random(seed);
		gameStartup();
	}
	
	private static PlayerEntity player;
	
	public static World getWorld() { return world; }
	public static PlayerEntity getPlayer() { return player; }
	
	public static boolean isDebugActive() {
		return Setting.TOGGLE_DEBUG.isToggled();
	}
}
