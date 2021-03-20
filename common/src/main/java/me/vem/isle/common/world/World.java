package me.vem.isle.common.world;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import gustavson.simplex.SimplexNoise;
import me.vem.isle.common.objects.GameObject;
import me.vem.isle.common.objects.Property;
import me.vem.utils.io.Compressable;
import me.vem.utils.io.RollingDataSaver;
import me.vem.utils.logging.Logger;
import me.vem.utils.math.Vector;

public class World implements Compressable{

	public static World loadFrom(File worldFile) {
		try {
			if(!worldFile.exists())
				return null;
			
			FileInputStream fis = new FileInputStream(worldFile);
			
			byte[] read = new byte[(int) worldFile.length()];
			fis.read(read);
			
			fis.close();

			ByteBuffer buf = ByteBuffer.wrap(read);
			World world = new World(buf);
			Logger.info("World file loaded properly.");
			
			return world;
		}catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private final int seed;
	private SimplexNoise noiseGen;
	private HashMap<Point, Chunk> chunks;
	
	private final Map<String, Integer> propertyMappingStrInt; 
	private String[] propertyMappingIntStr;
	
	public World(int seed) {
		this(seed, true);
	}
	
	public World(int seed, boolean newWorld) {
		this.seed = seed;
		chunks = new HashMap<>();
		noiseGen = new SimplexNoise(300, .5, seed);
		propertyMappingStrInt = new HashMap<>();
		
		if(newWorld) {
			//Dynamic Property ID Assignment
			List<String> iPropertyMapping = new ArrayList<>();
			
			int i = 0;
			Iterator<String> iter = Property.propertyIterator();
			
			while(iter.hasNext()) {
				String propId = iter.next();
				iPropertyMapping.add(propId);
				propertyMappingStrInt.put(propId, i++);
			}
			
			propertyMappingIntStr = iPropertyMapping.toArray(new String[0]);
		}
	}
	
	private World(ByteBuffer buf) {
		this(buf.getInt(), false);
		
		int cSize = buf.getInt();
		for(int i=0;i<cSize;i++) {
			Chunk c = new Chunk(this, buf);
			chunks.put(new Point(c.cx(), c.cy()), c);
		}
		
		//Dynamic Property ID Lookup Load
		propertyMappingIntStr = new String[buf.getInt()];
		for(int i=0;i<propertyMappingIntStr.length;i++) {
			int propIdLength = buf.getInt();
			StringBuilder sb = new StringBuilder(propIdLength);
			for(int t =0;t<propIdLength;t++)
				sb.append(buf.getChar());
			
			String propId = sb.toString();
			propertyMappingIntStr[i] = propId;
			propertyMappingStrInt.put(propId, i);
		}
		
		int goSize = buf.getInt();
		for(int i=0;i<goSize;i++)
			new GameObject(this, buf);
	}
	
	public String translatePropertyId(int i) {
		if(i < 0) return null;
		if(i >= propertyMappingIntStr.length) return null;
		
		return propertyMappingIntStr[i];
	}
	
	public int translatePropertyId(String propId) {
		Integer i = propertyMappingStrInt.get(propId);
		
		if(i == null)
			return -1;
		
		return i;
	}
	
	public int getSeed() { return seed; }
	
	public double getSimplexValue(int x, int y) {
		return noiseGen.getNoise(x, y);
	}
	
	public byte getLandOffset(int x, int y) {
		return getLandOffset(getSimplexValue(x, y));
	}
	
	public byte getLandOffset(double simplex) {
		byte result = 0;
		
		if(simplex >= .5) result++;
		if(simplex >= .52) result++;
		
		return result;
	}
	
	/**
	 * @param x
	 * @param y
	 * @return
	 */
	public Land getLand(int x, int y) {
		Chunk c = getChunkUnsafe(x, y);
		
		if(c == null) {
			return Land.valuesStatic()[getLandOffset(x,y)];
		} else {
			return c.getLand(x&15, y&15);
		}
	}
	
	public Land getLand(Vector pos) {
		return getLand(pos.floorX(), pos.floorY());
	}
	
	public Chunk getChunkUnsafe(int cx, int cy) {
		return chunks.get(new Point(cx, cy));
	}
	
	public Chunk getChunk(int cx, int cy) {
		Chunk c = getChunkUnsafe(cx, cy);
		
		if(c == null)
			chunks.put(new Point(cx, cy), c = new Chunk(this, cx, cy));
		
		return c;
	}
	
	public Chunk getChunkFor(Vector pos) {
		return getChunk(pos.floorX() >> 4, pos.floorY() >> 4);
	}
	
	public GameObject requestPlayer() {
		
		Random rand = new Random(this.seed);
		
		int x;
		int y;
		do {
			x = rand.nextInt(512) - 256;
			y = rand.nextInt(512) - 256;
		}while(!this.getLand(x, y).isSand());
		
		return new GameObject(this, "ent_player", x, y);
	}
	
	public synchronized RollingDataSaver writeTo(RollingDataSaver saver) {
		saver.putInt(getSeed());
		
		//TODO Save property mappings
		
		
		LinkedList<GameObject> kgo = new LinkedList<>();
		
		saver.putInt(chunks.size());
		for(Chunk c : chunks.values())
			synchronized(c) {
				c.writeTo(saver);
				c.addObjectsTo(kgo);
			}
		
		int numProperties = propertyMappingIntStr.length;
		saver.putInt(numProperties);
		for(int i=0;i<numProperties;i++) {
			String propId = propertyMappingIntStr[i];
			int length = propId.length();
			
			saver.putInt(length);
			for(int t=0;t<length;t++)
				saver.putChar(propId.charAt(t));
		}
		
		saver.putInt(kgo.size());
		for(GameObject go : kgo)
			go.writeTo(saver);
		
		return saver;
	}
	
	public boolean saveTo(File file) {
		try {
			RollingDataSaver rds = new RollingDataSaver(file);
			writeTo(rds);
			rds.close();

			Logger.info("World saved.");
			Logger.debug("World size: " + rds.written());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
