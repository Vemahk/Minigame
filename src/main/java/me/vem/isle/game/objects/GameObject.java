package me.vem.isle.game.objects;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import me.vem.isle.Logger;
import me.vem.isle.game.Game;
import me.vem.isle.game.physics.BoxCollider;
import me.vem.isle.game.physics.Collider;
import me.vem.isle.game.physics.Vector;
import me.vem.isle.game.world.Chunk;
import me.vem.isle.graphics.Spritesheet;
import me.vem.isle.io.Savable;
import me.vem.isle.resources.ResourceManager;

public abstract class GameObject implements Comparable<GameObject>, Savable<GameObject>{
	
	public static HashMap<Class<? extends GameObject>, Property> properties = new HashMap<>();
	
	public static HashSet<ChunkLoader> chunkLoaders = new HashSet<>();
	
	public static GameObject instantiate(GameObject go, Chunk c) {
		c.addObject(go);
		if(go instanceof ChunkLoader)
			chunkLoaders.add((ChunkLoader)go);
		return go;
	}
	
	public static GameObject instantiate(GameObject go) {
		return instantiate(go, go.getPresumedChunk());
	}
	
	public static Queue<GameObject> queuedToDestroy = new LinkedList<>();
	public static boolean destroy(GameObject go) {
		queuedToDestroy.add(go);
		return true;
	}
	
	protected Chunk chunk;
	
	protected Vector pos;
	protected float z;
	
	protected Collider collider;
	
	public GameObject(float x, float y) {
		pos = new Vector(x, y);
		setZ(0);
	}
	
	public GameObject setCollider(float w, float h) {
		this.collider = new BoxCollider(this, w, h);
		return this;
	}
	
	public boolean hasCollider() { return collider != null; }
	
	public boolean hasCollidedWith(GameObject go) {
		return collider.collidedWith(go.collider);
	}
	
	public Collider getCollider() { return collider; }
	
	public boolean withinDistanceOf(GameObject o, float dist) {
		return pos.sub(o.getPos()).getMagSq() <= dist * dist;
	}
	
	public Vector getPos() { return pos; }
	public float getX() { return pos.getX(); }
	public float getY() { return pos.getY(); }
	
	public void setPos(float x, float y) {
		pos.set(x, y);
	}
	
	public GameObject setZ(float z) {
		this.z = z;
		return this;
	}
	
	public void offset(float dx, float dy) {
		pos.offset(dx, dy);
	}
	
	public int compareTo(GameObject o) {
		if(z == o.z)
			return this.hashCode() - o.hashCode();
		
		return (int)Math.signum(z - o.z);
	}
	
	public GameObject setChunk(Chunk c) {
		this.chunk = c;
		return this;
	}
	
	public Chunk getAssignedChunk() {
		return chunk;
	}
	
	public Chunk getPresumedChunk() {
		return Game.getWorld().getChunk(pos.floorX() >> 4, pos.floorY() >> 4);
	}
	
	public Spritesheet getSpriteSheet() {
		return ResourceManager.getSpritesheet(getProperties().getSpritesheetName());
	}
	
	public BufferedImage getImage() {
		return getSpriteSheet().getImage(Integer.parseInt(getProperties().getImageID()));
	}
	
	public Property getProperties() {
		return properties.get(this.getClass());
	}

	public abstract void update(int tr);
}
