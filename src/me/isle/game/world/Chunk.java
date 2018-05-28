package me.isle.game.world;

import java.util.TreeSet;

import me.isle.Startup;
import me.isle.game.objects.GameObject;

public class Chunk {

	private TreeSet<GameObject> objs;
	private Land[][] land;
	
	private final int x;
	private final int y;
	
	private boolean isLoaded = false;
	
	public Chunk(int x, int y) {
		this.x = x;
		this.y = y;
		
		land = new Land[16][16];
		objs = new TreeSet<>();
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
		Startup.gameThread.queueChunkTransfer(new ChunkQueue(this, to, go));
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