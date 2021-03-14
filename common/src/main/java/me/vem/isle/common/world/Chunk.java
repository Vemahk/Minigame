package me.vem.isle.common.world;


import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import gustavson.simplex.SimplexNoise;
import me.vem.isle.common.Game;
import me.vem.isle.common.RIdentifiable;
import me.vem.isle.common.objects.GameObject;
import me.vem.utils.io.Compressable;
import me.vem.utils.io.RollingDataSaver;
import me.vem.utils.logging.Logger;

public class Chunk implements Compressable, RIdentifiable{
	
	private static Set<Chunk> LoadedChunks = Collections.synchronizedSet(new HashSet<>());
	private static SortedSet<GameObject> LoadedObjects = Collections.synchronizedSortedSet(new TreeSet<>());
	
	public static Set<Chunk> getLoadedChunks(){ return LoadedChunks; }
	public static Set<GameObject> getLoadedObjects(){ return LoadedObjects; }
	
	private static Queue<Chunk> toLoad = new LinkedList<>();
	private static Queue<Chunk> toUnload = new LinkedList<>();
	
	public static void loadChunkQueue() {
		Chunk c;
		while(!toLoad.isEmpty()) {
			c = toLoad.poll();
			LoadedChunks.add(c);
			c.addObjectsTo(LoadedObjects);
		}
		
		while(!toUnload.isEmpty()) {
			c = toUnload.poll();
			LoadedChunks.remove(c);
			c.removeObjectsFrom(LoadedObjects);
		}
	}
	
	public static void load(Chunk c) { toLoad.add(c); }
	public static void unload(Chunk c) { toUnload.add(c); }

	private final int RUID;
	
	private Set<GameObject> objs;
	private byte[][] land;
	private byte loaders;
	
	private final World world;
	private final int cx, cy;
	
	public Chunk(World world, int cx, int cy) {
		RUID = Game.requestRUID(this);
		
		this.world = world;
		this.cx = cx;
		this.cy = cy;
		
		land = new byte[16][16];
		objs = Collections.synchronizedSortedSet(new TreeSet<>());
	}
	
	public Chunk(World world, int cx, int cy, SimplexNoise sn) {
		this(world, cx, cy);
		
		int seed = world.getSeed();
		seed ^= ((cx & 0xFFFF) ^ (cx >>> 16)) << 16;
		seed ^= ((cy & 0xFFFF) ^ (cy >>> 16));
		
		Random rand = new Random(seed);
		
		for(int x=0;x<16;x++)
    		for(int y=0;y<16;y++) {
    			int sx = (cx << 4) + x;
    			int sy = (cy << 4) + y;
    			
                double d = sn.getNoise(sx, sy);

				if(d >= .5) land[x][y]++;
				if(d >= .52) land[x][y]++;
				if(d >= .55 && rand.nextDouble() < .1)
                	new GameObject(world, "obj_tree", sx, sy, this); 	
    		}
		
		Logger.debugf(1, "Chunk generated at %d, %d.", cx, cy);
	}
	
	public Chunk(World world, ByteBuffer buf) {
		this(world, buf.getInt(), buf.getInt());
		
		for(int b=0;b<64;b++) {
			byte bb = buf.get();
			for(int i=0;i<4;i++) {
				int t = (b << 2) | i; //Number of land we've already read in.
				land[t & 15][t >> 4] = (byte)((bb >>> ((3^i)<<1)) & 3);
			}
		}
	}
	
	public int cx() { return cx; }
	public int cy() { return cy; }
	
	public World getWorld() { return world; }
	
	public void addObjectsTo(Collection<GameObject> col) { col.addAll(objs); }
	public void removeObjectsFrom(Collection<GameObject> col) { col.removeAll(objs); }
	public Set<GameObject> getObjects(){ return objs; }
	
	public boolean add(GameObject go) {
		if(objs.add(go)) {
			go.setChunk(this);
			return true;
		}
		return false;
	}
	
	public boolean remove(GameObject go) {
		return objs.remove(go);
	}
	
	public void load(int r) {
		for(int x = -r; x <= r; x++)
			for(int y = -r; y <= r; y++)
				world.getChunk(cx + x, cy + y).load();
	}
	
	public void unload(int r) {
		for(int x = -r; x <= r; x++)
			for(int y = -r; y <= r; y++)
				world.getChunk(cx + x, cy + y).unload();
	}

	public void load() {
		if(loaders++ == 0)
			Chunk.load(this);
	}

	public void unload() {
		if(--loaders <= 0)
			Chunk.unload(this);
		
		if(loaders < 0) {
			Logger.warningf("%s has negative chunkloaders? Advise?", this);
			loaders = 0;
		}
	}
	
	public boolean isLoaded() { return loaders > 0; }
	
	public Land getLand(int x, int y) {
		return Land.values()[land[x][y]];
	}
	
	@Override
	public synchronized RollingDataSaver writeTo(RollingDataSaver saver) {
		saver.putInt(cx).putInt(cy);
		
		byte bb = 0; //bb >> Byte Buffer...
		for(int b = 0; b < 256; b++) {
			byte l = land[b & 0xF][b >> 4]; // [b % 16][b / 16]; [x][y]
			int n = b & 3;// mod 4
			
			bb |= l << ((3 - n) << 1);
			if(n == 3) {
				saver.put(bb);
				bb = 0;
			}
		}
		
		return saver;
	}
	
	public String toString() {
		return String.format("Chunk[%d,%d]", cx, cy);
	}
	
	@Override 
	public int getRUID() {
		return RUID;
	}
	
	private static Queue<Runnable> tq = new LinkedList<>();
	public static void runTransfers() {
		while(!tq.isEmpty())
			tq.poll().run();
	}
	
	public void transfer(GameObject go, Chunk to) {
		tq.add(() -> {
			if(remove(go)) {
				to.add(go);

				if(go.isChunkLoader()) {
					int r = go.chunkRadius();
					for(int x = -r; x <= r; x++)
						for(int y = -r; y <= r; y++)
							if(outOfBounds(this, r, to.cx + x, to.cy + y)) {
								world.getChunk(to.cx + x, to.cy + y).load();
								world.getChunk(cx - x, cy - y).unload();
							}
				}
				
				if(isLoaded() && !to.isLoaded())
					LoadedObjects.remove(go);
				
				Logger.debugf(1, "%s moved from %s to %s.", go, this, to);
			}
		});
	}
	
	private static boolean outOfBounds(Chunk c, int r, int x, int y) {
		return x < c.cx - r || x > c.cx + r || y < c.cy - r || y > c.cy + r;
	}
}