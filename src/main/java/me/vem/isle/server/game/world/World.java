package me.vem.isle.server.game.world;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

import gustavson.simplex.SimplexNoise;
import me.vem.isle.server.game.Game;
import me.vem.isle.server.game.objects.GameObject;
import me.vem.utils.io.Compressable;
import me.vem.utils.io.DataFormatter;
import me.vem.utils.math.Vector;

public class World implements Compressable{

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
	
	public Land getLand(Vector pos) {
		return getLand(pos.floorX(), pos.floorY());
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

	@Override
	public synchronized byte[] compress() {
		LinkedList<Byte> bytes = new LinkedList<>();
		
		for(byte b : DataFormatter.writeInts(noiseGen.getSeed(), chunks.size()))
			bytes.add(b);
		
		LinkedList<GameObject> kgo = new LinkedList<>();
		
		for(Chunk c : chunks.values()) {
			synchronized(c) {
				for(byte b : c.compress())
					bytes.add(b);
				
				for(GameObject go : c.getObjects())
					kgo.add(go);
			}
		}
		
		for(byte b : DataFormatter.writeInt(kgo.size()))
			bytes.add(b);
		
		for(GameObject go : kgo)
			for(byte b : go.compress())
				bytes.add(b);
		
		int i=0;
		byte[] out = new byte[bytes.size()];
		for(byte b : bytes)
			out[i++] = b;
		
		return out;
	}
}
