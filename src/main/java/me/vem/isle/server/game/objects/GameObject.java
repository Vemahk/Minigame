package me.vem.isle.server.game.objects;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

import me.vem.isle.client.resources.Sprite;
import me.vem.isle.server.game.controller.Controller;
import me.vem.isle.server.game.physics.Collider;
import me.vem.isle.server.game.physics.Physics;
import me.vem.isle.server.game.world.Chunk;
import me.vem.isle.server.game.world.World;
import me.vem.utils.io.Compressable;
import me.vem.utils.math.Vector;

public class GameObject implements Comparable<GameObject>, Compressable{
	
	public static GameObject instantiate(GameObject go, Chunk c) {
		c.add(go);
		if(go.isChunkLoader())
			c.load(go, go.chunkRadius());
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
		while(!toDestroy.isEmpty()) {
			GameObject go = toDestroy.poll();
			go.chunk.remove(go);
			if(go.isChunkLoader())
				go.chunk.unload(go, go.chunkRadius());
		}
	}
	
	protected Chunk chunk;
	
	protected final Property prop;

	protected Vector pos;
	
	protected Physics physics;	
	protected Collider collider;
	protected Controller controller;
	
	public GameObject(String id, int x, int y) {
		this(id, x+.5f, y+.5f);
	}
	
	public GameObject(String id, float x, float y) {
		this(id.hashCode(), x, y);
	}
	
	public GameObject(int hash, float x, float y) {
		prop = Property.get(hash);
		pos = new Vector(x, y);
		
		physics = prop.buildPhysics(this);
		collider = prop.buildCollider(this);
		controller = prop.buildController(this);
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
	public boolean hasController() { return controller != null; }
	
	public boolean isChunkLoader() { return prop.isChunkLoader(); }
	public int chunkRadius() { return prop.getLoadRadius(); }
	
	public Sprite getSprite() { return prop.getSprite(); }
	
	public boolean collidedWith(GameObject go) {
		return collider.collidedWith(go.collider);
	}
	
	public void setPos(float x, float y) { pos.set(x, y); }
	public void move(float dx, float dy) { pos.offset(dx, dy); }
	
	public void setChunk(Chunk c) { this.chunk = c; }
	
	public void update(float dt) {
		if(hasPhysics()) {
			physics.update(dt);
		
			Chunk nChunk = getPresumedChunk();
			if(chunk != nChunk)
				chunk.transfer(this, nChunk);
		}
		
		if(hasController())
			controller.update(dt);
	}

	@Override
	public int compareTo(GameObject o) {
		if(getZ() == o.getZ())
			return hashCode() - o.hashCode();
		
		return (int)Math.signum(getZ() - o.getZ());
	}
	
	@Override
	public String toString() {
		return String.format("GameObject[%s:%s]", prop.getId(), pos);
	}
	
	@Override
	public synchronized ByteBuffer writeTo(ByteBuffer buf) {
		buf.putInt(prop.hashCode());
		pos.writeTo(buf);
		
		return buf;
	}
	
	@Override public int writeSize() { return 12; }
}
