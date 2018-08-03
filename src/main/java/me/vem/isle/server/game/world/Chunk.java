package me.vem.isle.server.game.world;

import static me.vem.isle.Logger.debug;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import gustavson.simplex.SimplexNoise;
import me.vem.isle.Logger;
import me.vem.isle.server.game.Game;
import me.vem.isle.server.game.objects.GameObject;
import me.vem.utils.io.Compressable;

public class Chunk implements Compressable{
	
	
	
	private TreeSet<GameObject> objs;
	private byte[][] land;
	
	private final int cx, cy;
	
	public Chunk(int cx, int cy, SimplexNoise sn) {
		this.cx = cx;
		this.cy = cy;
		land = new byte[16][16];
		
		objs = new TreeSet<GameObject>();
		
		for(int x=0;x<16;x++)
    		for(int y=0;y<16;y++) {
    			int sx = (cx << 4) + x;
    			int sy = (cy << 4) + y;
    			
                double d = sn.getNoise(sx, sy);
                
                if(d >= .55 && Math.random()<.1)
					GameObject.instantiate(new GameObject("obj_tree", sx, sy), this); 

				if(d >= .5) land[x][y]++;
				if(d >= .52) land[x][y]++;
    		}
		
		if(Game.isDebugActive())
			Logger.debug(String.format("Chunk generated at %d, %d.", cx, cy));
	}
	
	public synchronized void addObjectsTo(Collection<GameObject> col) { col.addAll(objs); }
	public Set<GameObject> getObjects(){ return objs; }
	
	public synchronized boolean add(GameObject go) {
		if(objs.add(go)) {
			go.setChunk(this);
			return true;
		}
		return false;
	}
	
	public synchronized boolean remove(GameObject go) {
		return objs.remove(go);
	}
	
	public void load(GameObject go, int r) {
		for(int x = -r; x <= r; x++)
			for(int y = -r; y <= r; y++)
				World.getInstance().getChunk(cx + x, cy + y).load(go);
	}
	
	public void unload(GameObject go, int r) {
		for(int x = -r; x <= r; x++)
			for(int y = -r; y <= r; y++)
				World.getInstance().getChunk(cx + x, cy + y).unload(go);
	}
	
	public void load(GameObject go) {
		World.getInstance().load(this);
	}
	
	public void unload(GameObject go) {
		World.getInstance().unload(this);
	}
	
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
								World.getInstance().getChunk(to.cx + x, to.cy + y).load(go);
								World.getInstance().getChunk(cx - x, cy - y).unload(go);
							}
				}
						
				if(Game.isDebugActive())
					debug(String.format("%s moved from %s to %s.", go, Chunk.this, to));
			}
		}
		
		private boolean outOfBounds(Chunk c, int r, int x, int y) {
			return x < c.cx - r || x > c.cx + r || y < c.cy - r || y > c.cy + r;
		}
	}
}