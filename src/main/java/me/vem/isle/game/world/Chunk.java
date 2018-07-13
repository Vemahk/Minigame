package me.vem.isle.game.world;

import java.util.TreeSet;

import gustavson.simplex.SimplexNoise;
import me.vem.isle.App;
import me.vem.isle.game.objects.GameObject;
import me.vem.isle.game.objects.Tree;

public class Chunk {

	private TreeSet<GameObject> objs;
	private Land[][] land;
	
	private boolean isLoaded = false;
	
	public Chunk(int cx, int cy, SimplexNoise sn) {
		
		land = new Land[16][16];
		objs = new TreeSet<>();
		
		for(int x=0;x<16;x++)
    		for(int y=0;y<16;y++) {
    			int sx = (cx << 4) + x;
    			int sy = (cy << 4) + y;
    			
                double d = 0.5*(1+sn.getNoise(sx, sy));
                
                if(d >= .55 && Math.random()<.2)
					GameObject.instantiate(new Tree(sx+.5, sy+.5).setZ(1).setCollider(.9, .9), this); 
                
				if(d >= .52)
					setLand(x, y, new Grass(sx, sy));
				else if(d >= .5)
					setLand(x, y, new Sand(sx, sy));
				else setLand(x, y, new Water(sx, sy));
    		}
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
	
	public void queueTransfer(Chunk to, GameObject go) {
		App.updateThread.queueChunkTransfer(new ChunkQueue(this, to, go));
	}
	
	public Land getLand(int x, int y) {
		return land[x][y];
	}
	
	public void setLand(int x, int y, Land l) {
		land[x][y] = l;
	}
	
	public boolean isWater(int x, int y) {
		return land[x][y] instanceof Water;
	}
	
	public boolean isSand(int x, int y) {
		return land[x][y] instanceof Sand;
	}
	
	public boolean isGrass(int x, int y) {
		return land[x][y] instanceof Grass;
	}
}