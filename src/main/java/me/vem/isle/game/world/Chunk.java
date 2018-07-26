package me.vem.isle.game.world;

import java.util.TreeSet;

import gustavson.simplex.SimplexNoise;
import me.vem.isle.Logger;
import me.vem.isle.game.Game;
import me.vem.isle.game.objects.GameObject;

public class Chunk {
	
	private TreeSet<GameObject> objs;
	private byte[][] land;
	
	private boolean isLoaded = false;
	
	public Chunk(int cx, int cy, SimplexNoise sn) {
		
		land = new byte[16][16];
		
		objs = new TreeSet<GameObject>();
		
		for(int x=0;x<16;x++)
    		for(int y=0;y<16;y++) {
    			int sx = (cx << 4) + x;
    			int sy = (cy << 4) + y;
    			
                double d = sn.getNoise(sx, sy);
                
                if(d >= .55 && Math.random()<.1)
					GameObject.instantiate(new GameObject("obj_tree", sx+.5f, sy+.5f), this); 

				if(d >= .5) land[x][y]++;
				if(d >= .52) land[x][y]++;
    		}
		
		if(Game.isDebugActive())
			Logger.debug(String.format("Chunk generated at %d, %d.", cx, cy));
	}
	
	public Chunk setLoaded(boolean b) {
		isLoaded = b;
		return this;
	}
	
	public boolean isLoaded() {
		return isLoaded;
	}
	
	public TreeSet<GameObject> getObjects(){
		return objs;
	}
	
	public boolean hasObject(GameObject go) {
		synchronized(objs) {
			return objs.contains(go);
		}
	}
	
	public boolean addObject(GameObject go) {
		synchronized(objs) {
			objs.add(go);
			go.setChunk(this);
			return true;
		}
	}
	
	public boolean removeObject(GameObject go) {
		synchronized(objs) {
			return objs.remove(go);
		}
	}
	
	public Land getLand(int x, int y) {
		return Land.values()[land[x][y]];
	}
}