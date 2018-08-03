package me.vem.isle.server.game.world;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;

import gustavson.simplex.SimplexNoise;
import me.vem.isle.Logger;
import me.vem.isle.server.game.objects.GameObject;
import me.vem.utils.io.Compressable;
import me.vem.utils.math.Vector;

public class World implements Compressable{

	private static World instance;
	public static World getInstance() { return instance; }
	
	public static void createInstance(int seed) {
		if(instance != null)
			return;
		
		new World(seed);
	}
	
	public static void loadInstance(ByteBuffer buf) {
		if(instance != null)
			return;
		
		new World(buf);
	}
	
	private SimplexNoise noiseGen;
	private HashMap<Point, Chunk> chunks;
	
	private World(int seed) {
		chunks = new HashMap<>();
		noiseGen = new SimplexNoise(300, .5, seed);
		
		instance = this;
	}
	
	private World(ByteBuffer buf) {
		this(buf.getInt());
		
		int cSize = buf.getInt();
		for(int i=0;i<cSize;i++) {
			Chunk c = new Chunk(buf);
			chunks.put(new Point(c.cx(), c.cy()), c);
		}
		
		int goSize = buf.getInt();
		for(int i=0;i<goSize;i++)
			GameObject.instantiate(buf, chunks);
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
	
	public Chunk getChunkFor(Vector pos) {
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

	public static boolean load() {
		try {
			File worldFile = new File("world.dat");
			if(!worldFile.exists())
				return false;
			
			FileInputStream fis = new FileInputStream(worldFile);
			
			byte[] read = new byte[(int) worldFile.length()];
			fis.read(read);
			
			fis.close();

			ByteBuffer buf = ByteBuffer.wrap(read);
			
			World.loadInstance(buf);
			
			Logger.info("World file loaded properly.");
		}catch(IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
