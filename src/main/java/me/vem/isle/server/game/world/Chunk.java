package me.vem.isle.server.game.world;

import static me.vem.isle.Logger.debug;

import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeSet;

import gustavson.simplex.SimplexNoise;
import me.vem.isle.Logger;
import me.vem.isle.server.game.Game;
import me.vem.isle.server.game.objects.GameObject;
import me.vem.utils.io.Compressable;
import me.vem.utils.io.DataFormatter;

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
	
	public TreeSet<GameObject> getObjects(){
		return objs;
	}
	
	public synchronized boolean hasObject(GameObject go) {
		return objs.contains(go);
	}
	
	public synchronized boolean addObject(GameObject go) {
		if(objs.add(go)) {
			go.setChunk(this);
			return true;
		}
		return false;
	}
	
	public synchronized boolean removeObject(GameObject go) {
		return objs.remove(go);
	}
	
	public Land getLand(int x, int y) {
		return Land.values()[land[x][y]];
	}
	
	@Override
	public synchronized byte[] compress() {
		byte[] out = new byte[72];
		
		DataFormatter.append(out, DataFormatter.writeInts(cx, cy), 0);
		
		byte buf = 0;
		for(int b = 0, i = 8; b < 256; b++) {
			byte l = land[b & 0xF][b >> 4]; // [b % 16][b / 16]; [x][y]
			int n = b & 3;// mod 4
			
			buf |= l << ((3 - n) << 1);
			if(n == 3) {
				out[i++] = buf;
				buf = 0;
			}
		}
		
		return out;
	}
	
	public String toString() {
		return String.format("Chunk[%d,%d]", cx, cy);
	}
	
	private static Queue<Transfer> tq = new LinkedList<>();
	public static void runTransfers() {
		while(!tq.isEmpty())
			tq.poll().execute();
	}
	
	public void transfer(GameObject go, Chunk to) {
		tq.add(new Transfer(go, to));
	}
	
	private class Transfer{

		private final GameObject go;
		private final Chunk to;
		
		private Transfer(GameObject go, Chunk to) {
			this.to = to;
			this.go = go;
		}
		
		public boolean execute() { //ORDAH 66!
			if(removeObject(go)) {
				boolean out = to.addObject(go);
				
				if(Game.isDebugActive())
					debug(String.format("%s moved from %s to %s.", go, Chunk.this, to));
				
				return out;
			}
			return false;
		}
	}
}