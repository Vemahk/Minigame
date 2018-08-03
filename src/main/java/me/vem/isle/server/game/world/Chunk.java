package me.vem.isle.server.game.world;

import static me.vem.isle.Logger.debug;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import gustavson.simplex.SimplexNoise;
import me.vem.isle.Logger;
import me.vem.isle.server.game.Game;
import me.vem.isle.server.game.objects.GameObject;
import me.vem.utils.io.Compressable;

public class Chunk implements Compressable{
	
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
	
	private Set<GameObject> objs;
	private byte[][] land;
	private byte loaders;
	
	private final int cx, cy;
	
	public Chunk(int cx, int cy) {
		this.cx = cx;
		this.cy = cy;
		land = new byte[16][16];
		
		objs = Collections.synchronizedSortedSet(new TreeSet<>());
	}
	
	public Chunk(int cx, int cy, SimplexNoise sn) {
		this(cx, cy);
		
		for(int x=0;x<16;x++)
    		for(int y=0;y<16;y++) {
    			int sx = (cx << 4) + x;
    			int sy = (cy << 4) + y;
    			
                double d = sn.getNoise(sx, sy);
                
                if(d >= .55 && Game.random().nextDouble() < .1)
					GameObject.instantiate(new GameObject("obj_tree", sx, sy), this); 

				if(d >= .5) land[x][y]++;
				if(d >= .52) land[x][y]++;
    		}
		
		if(Game.isDebugActive())
			Logger.debugf("Chunk generated at %d, %d.", cx, cy);
	}
	
	public Chunk(ByteBuffer buf) {
		this(buf.getInt(), buf.getInt());
		
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
				World.getInstance().getChunk(cx + x, cy + y).load();
	}
	
	public void unload(int r) {
		for(int x = -r; x <= r; x++)
			for(int y = -r; y <= r; y++)
				World.getInstance().getChunk(cx + x, cy + y).unload();
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
	public synchronized ByteBuffer writeTo(ByteBuffer buf) {
		buf.putInt(cx).putInt(cy);
		
		byte bb = 0; //bb >> Byte Buffer...
		for(int b = 0; b < 256; b++) {
			byte l = land[b & 0xF][b >> 4]; // [b % 16][b / 16]; [x][y]
			int n = b & 3;// mod 4
			
			bb |= l << ((3 - n) << 1);
			if(n == 3) {
				buf.put(bb);
				bb = 0;
			}
		}
		
		return buf;
	}
	
	@Override public int writeSize() { return 72; }
	
	public String toString() {
		return String.format("Chunk[%d,%d]", cx, cy);
	}
	
	private static Queue<Transfer> tq = new LinkedList<>();
	public static void runTransfers() {
		while(!tq.isEmpty())
			tq.poll().run();
	}
	
	public void transfer(GameObject go, Chunk to) {
		tq.add(new Transfer(go, to));
	}
	
	private class Transfer implements Runnable{

		private final GameObject go;
		private final Chunk to;
		
		private Transfer(GameObject go, Chunk to) {
			this.to = to;
			this.go = go;
		}
		
		@Override
		public void run() {
			if(remove(go)) {
				to.add(go);

				if(go.isChunkLoader()) {
					int r = go.chunkRadius();
					for(int x = -r; x <= r; x++)
						for(int y = -r; y <= r; y++)
							if(outOfBounds(Chunk.this, r, to.cx + x, to.cy + y)) {
								World.getInstance().getChunk(to.cx + x, to.cy + y).load();
								World.getInstance().getChunk(cx - x, cy - y).unload();
							}
				}
				
				if(isLoaded() && !to.isLoaded())
					LoadedObjects.remove(go);
				
				if(Game.isDebugActive())
					debug(String.format("%s moved from %s to %s.", go, Chunk.this, to));
			}
		}
		
		private boolean outOfBounds(Chunk c, int r, int x, int y) {
			return x < c.cx - r || x > c.cx + r || y < c.cy - r || y > c.cy + r;
		}
	}
}