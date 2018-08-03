package me.vem.isle.server.game.world;

import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import gustavson.simplex.SimplexNoise;
import me.vem.isle.Logger;
import me.vem.isle.server.game.objects.GameObject;
import me.vem.utils.io.Compressable;
import me.vem.utils.math.Vector;

public class World implements Compressable{

	private static World instance;
	public static World getInstance() {
		return instance;
	}
	
	public static void createInstance(int seed) {
		if(instance != null)
			return;
		
		instance = new World(seed);
	}
	
	public static void loadInstance(ByteBuffer buf) {
		//TODO LATER
	}
	
	private SimplexNoise noiseGen;
	
	private HashMap<Point, Chunk> chunks;
	private HashSet<Chunk> loaded;
	
	public HashSet<Chunk> getLoadedChunks(){
		return loaded;
	}
	
	public Set<GameObject> getLoadedObjects(){
		TreeSet<GameObject> out = new TreeSet<>();
		
		synchronized(loaded) {
			for(Chunk c : loaded) {
				synchronized(c) {
					c.addObjectsTo(out);
				}
			}
		}
		
		return out;
	}
	
	private Queue<Chunk> toLoad = new LinkedList<>();
	private Queue<Chunk> toUnload = new LinkedList<>();
	
	public void loadChunkQueue() {
		while(!toLoad.isEmpty())
			loaded.add(toLoad.poll());
		while(!toUnload.isEmpty())
			loaded.remove(toUnload.poll());
	}
	
	public void load(Chunk c) { toLoad.add(c); }
	public void unload(Chunk c) { toUnload.add(c); }
	
	private World(int seed) {
		chunks = new HashMap<>();
    	loaded = new HashSet<>();
    	
		noiseGen = new SimplexNoise(300, .5, seed);
	}
	
	private World(ByteBuffer buff) {
		//TODO LATER
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
	
	public synchronized ByteBuffer writeTo(ByteBuffer buf) {
		buf.putInt(noiseGen.getSeed()).putInt(chunks.size());
		
		LinkedList<GameObject> kgo = new LinkedList<>();
		
		for(Chunk c : chunks.values())
			synchronized(c) {
				c.writeTo(buf);
				c.addObjectsTo(kgo);
			}
		
		buf.putInt(kgo.size());
		
		for(GameObject go : kgo)
			go.writeTo(buf);
		
		return buf;
	}
	
	@Override public int writeSize() {
		int x = 1 << 6; //64
		//TODO Actually estimate and don't be lazy.
		return x << 10; //Returns in kilobytes
	}
	
	public static boolean save() {
		try {
			ByteBuffer buf = ByteBuffer.allocate(World.getInstance().writeSize());
			World.getInstance().writeTo(buf);
			
			if(!buf.hasArray())
				return false;
			
			File file = new File("world.dat");
			if (file.exists())
				file.delete();
			
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(buf.array(), 0, buf.position());
			fos.flush();
			fos.close();

			Logger.info("World saved.");
			Logger.debug("World size: " + buf.position());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
