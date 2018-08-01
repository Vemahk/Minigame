package me.vem.isle.server.game.objects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import me.vem.isle.client.resources.Sprite;
import me.vem.isle.server.game.physics.BoxCollider;
import me.vem.isle.server.game.physics.Collider;
import me.vem.isle.server.game.physics.Physics;
import me.vem.isle.server.game.world.Chunk;
import me.vem.isle.server.game.world.World;
import me.vem.utils.io.Compressable;
import me.vem.utils.io.DataFormatter;
import me.vem.utils.math.Vector;

public class GameObject implements Comparable<GameObject>, Compressable{
	
	public static HashMap<Integer, Property> properties = new HashMap<>();
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
		while(!toDestroy.isEmpty()) {
			GameObject go = toDestroy.poll();
			go.chunk.removeObject(go);
			if(go.isChunkLoader())
				chunkLoaders.remove(go);
		}
	}
	
	protected Chunk chunk;
	
	protected final Property prop;

	protected Vector pos;
	
	protected Physics physics;	
	protected Collider collider;
	
	public GameObject(String id, int x, int y) {
		this(id, x+.5f, y+.5f);
	}
	
	public GameObject(String id, float x, float y) {
		prop = GameObject.properties.get(id.hashCode());
		pos = new Vector(x, y);
		
		if(prop.isPhysics())
			physics = new Physics(this, prop.getMass(), prop.getSpeed());
		
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
	
	public void update(float dt) {
		if(hasPhysics()) {
			physics.update(dt);
		
			Chunk nChunk = getPresumedChunk();
			if(chunk != nChunk)
				chunk.transfer(this, nChunk);
		}
	}

	@Override
	public int compareTo(GameObject o) {
		if(getZ() == o.getZ())
			return hashCode() - o.hashCode();
		
		return (int)Math.signum(getZ() - o.getZ());
	}

	@Override
	public synchronized byte[] compress() {
		byte[] out = new byte[12];
		
		DataFormatter.append(out, DataFormatter.writeInt(prop.hashCode()), 0);
		DataFormatter.append(out, pos.compress(), 4);
		
		return out;
	}
}
