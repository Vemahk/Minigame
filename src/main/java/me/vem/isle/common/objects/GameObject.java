package me.vem.isle.common.objects;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

//TODO Separate Common from Client
import me.vem.isle.client.resources.Sprite;
import me.vem.isle.common.Game;
import me.vem.isle.common.RIdentifiable;
import me.vem.isle.common.controller.Controller;
import me.vem.isle.common.physics.Physics;
import me.vem.isle.common.physics.collider.Collider;
import me.vem.isle.common.world.Chunk;
import me.vem.isle.common.world.World;
import me.vem.utils.io.Compressable;
import me.vem.utils.io.RollingDataSaver;
import me.vem.utils.math.Vector;

public class GameObject implements Comparable<GameObject>, Compressable, RIdentifiable{
	
	public static Queue<GameObject> toDestroy = new LinkedList<>();
	public static boolean destroy(GameObject go) {
		return toDestroy.add(go);
	}
	
	public static void destroyQueue() {
		while(!toDestroy.isEmpty()) {
			GameObject go = toDestroy.poll();
			go.chunk.remove(go);
			if(go.isChunkLoader())
				go.chunk.unload(go.chunkRadius());
		}
	}

	private final int RUID;
	protected final Property prop;
	
	protected Chunk chunk;
	protected Vector pos;
	
	protected Physics physics;	
	protected Collider collider;
	protected Controller controller;
	
	public GameObject(World world, ByteBuffer buf) {
		this(world, buf.getInt(), buf.getFloat(), buf.getFloat());
	}
	
	public GameObject(World world, String id, int x, int y) {
		this(world, id, x+.5f, y+.5f);
	}
	
	public GameObject(World world, String id, float x, float y) {
		this(world, id.hashCode(), x, y);
	}
	
	public GameObject(World world, int hash, float x, float y) {
		this(world, hash, x, y, null);
	}
	
	public GameObject(World world, String s, float x, float y, Chunk c) {
		this(world, s.hashCode(), x, y, c);
	}
	
	public GameObject(World world, int hash, float x, float y, Chunk chunk) {
		RUID = Game.requestRUID(this);
		prop = Property.get(hash);
		pos = new Vector(x, y);
		
		physics = prop.buildPhysics(this);
		collider = prop.buildCollider(this);
		controller = prop.buildController(this);
		
		if(chunk == null)
			chunk = world.getChunkFor(pos);
		
		//Chunk handling
		chunk.add(this);
		
		if(isChunkLoader())
			chunk.load(chunkRadius());
	}
	
	public World getWorld() { return chunk.getWorld(); }
	
	public String getId() { return prop.getId(); }
	public int getRUID() { return RUID; }
	public boolean isId(String s) {
		return s.hashCode() == prop.hashCode();
	}
	
	public Vector getPos() { return pos; }
	public void setPos(float x, float y) { pos.set(x, y); }
	public void move(float dx, float dy) { pos.offset(dx, dy); }
	
	public float getX() { return pos.getX(); }
	public float getY() { return pos.getY(); }
	public float getZ() { return prop.getZ(); }
	
	public Physics getPhysics() { return physics; }
	public Collider getCollider() { return collider; }
	public Controller getController() { return controller; }
	
	public Chunk getAssignedChunk() { return chunk; }
	
	public boolean hasPhysics() { return physics != null; }
	public boolean hasCollider() { return collider != null; }
	
	public boolean isChunkLoader() { return prop.isChunkLoader(); }
	public int chunkRadius() { return prop.getLoadRadius(); }
	
	public Sprite getSprite() { return prop.getSprite(); }
	public boolean hasSprite() { return prop.getSprite() != null; }
	
	public boolean collidedWith(GameObject go) {
		return collider.collidedWith(go.collider);
	}
	
	public void setChunk(Chunk c) { this.chunk = c; }
	
	public void update(float dt) {
		if(hasPhysics()) 
			physics.update(dt);
		
		if(controller != null)
			controller.update(dt);
	}

	@Override
	public int compareTo(GameObject o) {
		if(getZ() == o.getZ())
			return RUID - o.RUID;
		
		return (int)Math.signum(getZ() - o.getZ());
	}
	
	@Override
	public String toString() {
		return String.format("GameObject[%s:%s]", prop.getId(), pos);
	}
	
	@Override
	public RollingDataSaver writeTo(RollingDataSaver saver) {
		saver.putInt(prop.hashCode());
		pos.writeTo(saver);
		
		return saver;
	}
	
	@Override
	public int hashCode() {
		return RUID;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GameObject other = (GameObject) obj;
		if (RUID != other.RUID)
			return false;
		return true;
	}
}
