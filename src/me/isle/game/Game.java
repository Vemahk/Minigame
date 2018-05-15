package me.isle.game;

import java.util.Random;

import gustavson.simplex.SimplexNoise;
import me.isle.game.entity.PlayerEntity;
import me.isle.game.land.Grass;
import me.isle.game.land.Land;
import me.isle.game.land.Landmass;
import me.isle.game.land.Water;
import me.isle.game.objects.GameObject;
import me.isle.game.objects.Tree;
import me.isle.graphics.ArrowKeyListener;

public class Game {

	public static boolean DEBUG_ACTIVE = false;
	public static Game game;
	public static Random rand = new Random();
	
	private Landmass landmass;
	
	private ArrowKeyListener akl;
	
	private PlayerEntity player;
	
	public Game(int w, int h) {
		
		landmass = new Landmass(w, h);
		
		int x = w / 4 + rand.nextInt(w / 2);
		int y = h / 4 + rand.nextInt(h / 2);
		while(landmass.isWater(x, y)) {
			x = w/4 + rand.nextInt(w/2);
			y = h/4 + rand.nextInt(h/2);
		}
		
		player = (PlayerEntity) GameObject.instantiate(new PlayerEntity(x, y));
	}
	
	public int getWidth() { return landmass.getWidth(); }
	public int getHeight() { return landmass.getHeight(); }
	
	public Landmass getLandmass() { return landmass; }
	public PlayerEntity getPlayer() { return player; }
	
	public ArrowKeyListener getKeyListener() {
		return akl;
	}
	
	public ArrowKeyListener setKeyListener(ArrowKeyListener akl) {
		this.akl = akl;
		return akl;
	}
}
