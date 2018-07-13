package me.vem.isle.game.world;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import gustavson.simplex.SimplexNoise;
import me.vem.isle.game.Game;
import me.vem.isle.game.objects.GameObject;
import me.vem.isle.game.objects.Tree;

public class World {

	private SimplexNoise noiseGen;
	
	private HashMap<Point, Chunk> chunks;
	private HashSet<Chunk> loaded;
	
	public HashSet<Chunk> getLoadedChunks(){
		return loaded;
	}
	
	public World() {
    	chunks = new HashMap<>();
    	loaded = new HashSet<>();
    	
    	Random rand = Game.rand;
		noiseGen = new SimplexNoise(300, .5, rand.nextInt());
	}
	
	/**
	 * @param x
	 * @param y
	 * @return
	 */
	public Land getLand(int x, int y) {
		return getChunk(x>>4, y>>4).getLand(x&15, y&15);
	}
	
	public boolean isWater(int x, int y) {
		return getLand(x,y) instanceof Water;
	}
	
	public boolean isSand(int x, int y) {
		return getLand(x,y) instanceof Sand;
	}
	
	public boolean isGrass(int x, int y) {
		return getLand(x,y) instanceof Grass;
	}
	
	public Chunk getChunk(int cx, int cy) {
		Chunk c = chunks.get(new Point(cx, cy));
		
		if(c == null)
			chunks.put(new Point(cx, cy), c = new Chunk(cx, cy, noiseGen));
		
		return c;
	}
}
