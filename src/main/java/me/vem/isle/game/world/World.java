package me.vem.isle.game.world;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import gustavson.simplex.SimplexNoise;
import me.vem.isle.game.Game;
import me.vem.isle.game.physics.Vector;

public class World {

	private static World instance;
	public static World getInstance() {
		if(instance == null)
			instance = new World();
		return instance;
	}
	
	private SimplexNoise noiseGen;
	
	private HashMap<Point, Chunk> chunks;
	private HashSet<Chunk> loaded;
	
	public HashSet<Chunk> getLoadedChunks(){
		return loaded;
	}
	
	private World() {
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
	
	public Chunk getChunk(int cx, int cy) {
		Chunk c = chunks.get(new Point(cx, cy));
		
		if(c == null)
			chunks.put(new Point(cx, cy), c = new Chunk(cx, cy, noiseGen));
		
		return c;
	}
	
	public Chunk getPresumedChunk(Vector pos) {
		return getChunk(pos.floorX() >> 4, pos.floorY() >> 4);
	}
}
