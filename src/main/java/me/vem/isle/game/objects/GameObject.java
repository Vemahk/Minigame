package me.vem.isle.game.objects;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import me.vem.isle.game.physics.BoxCollider;
import me.vem.isle.game.physics.Collider;
import me.vem.isle.game.physics.Physics;
import me.vem.isle.game.world.Chunk;
import me.vem.isle.game.world.ChunkQueue;
import me.vem.isle.game.world.World;
import me.vem.isle.resources.Sprite;
import me.vem.utils.math.Vector;

public class GameObject implements Comparable<GameObject>{
	
	public static HashMap<String, Property> properties = new HashMap<>();
	public static HashSet<GameObject> chunkLoaders = new HashSet<>();
	
	public static GameObject instantiate(GameObject go, Chunk c) {
		c.addObject(go);
		if(go.isChunkLoader())
			chunkLoaders.add(go);
		return go;
	}
	
	public static GameObject instantiate(GameObject go) {
		return instantiate(go, go.getPresumedChunk());
	}
	
	public static Queue<GameObject> toDestroy = new LinkedList<>();
	public static boolean destroy(GameObject go) {
		return toDestroy.add(go);
	}
	
	public static void destroyQueue() {
		while(!toDestroy.isEmpty())
			toDestroy.peek().getAssignedChunk().removeObject(toDestroy.poll());
	}
	
	
	protected Chunk chunk;
	
	protected final Property prop;

	protected Vector pos;
	
	protected Physics physics;	
	protected Collider collider;
	
	public GameObject(String id, float x, float y) {
		prop = GameObject.properties.get(id);
		pos = new Vector(x, y);
		
		if(prop.isPhysics())
			physics = new Physics(this, prop.getMass(), prop.getFriction(), prop.getSpeed());
		
		if(prop.hasCollider()) {
			Vector size = prop.getCollisionBoxSize();
			this.collider = new BoxCollider(this, size.getX(), size.getY());
		}
	}
	
	public String getId() { return prop.getId(); }

	public Vector getPos() { return pos; }
	public float getX() { return pos.getX(); }
	public float getY() { return pos.getY(); }
	public float getZ() { return prop.getZ(); }
	
	public Physics getPhysics() { return physics; }
	public Collider getCollider() { return collider; }
	
	public Chunk getAssignedChunk() { return chunk; }
	public Chunk getPresumedChunk() { return World.getInstance().getPresumedChunk(pos); }
	
	public boolean hasPhysics() { return physics != null; }
	public boolean hasCollider() { return collider != null; }
	
	public boolean isChunkLoader() { return prop.isChunkLoader(); }
	public int chunkRadius() { return prop.getLoadRadius(); }
	
	public Sprite getSprite() { return prop.getSprite(); }
	
	public boolean collidedWith(GameObject go) {
		return collider.collidedWith(go.collider);
	}
	
	public void setPos(float x, float y) { pos.set(x, y); }
	public void move(float dx, float dy) { pos.offset(dx, dy); }
	
	public void setChunk(Chunk c) { this.chunk = c; }
	
	public void update(int tr) {
		if(hasPhysics()) {
			physics.update(tr);
		
			Chunk nChunk = getPresumedChunk();
			if(chunk != nChunk)
				ChunkQueue.queue(chunk, nChunk, this);
		}
	}

	@Override
	public int compareTo(GameObject o) {
		if(getZ() == o.getZ())
			return hashCode() - o.hashCode();
		
		return (int)Math.signum(getZ() - o.getZ());
	}
}
