package me.isle.game.objects;

import java.util.HashSet;

import me.isle.game.Game;
import me.isle.game.physics.BoxCollider;
import me.isle.game.physics.Collider;
import me.isle.game.physics.PhysicsBody;
import me.isle.game.world.Chunk;

public abstract class GameObject implements Drawable, Comparable<GameObject>{
	
	public static HashSet<ChunkLoader> chunkLoaders = new HashSet<>();
	
	public static GameObject instantiate(GameObject go) {
		go.getPresumedChunk().addObject(go);
		if(go instanceof ChunkLoader)
			chunkLoaders.add((ChunkLoader)go);
		return go;
	}
	
	public static HashSet<GameObject> queuedToDestroy = new HashSet<>();
	public static boolean destroy(GameObject go) {
		queuedToDestroy.add(go);
		return true;
	}
	
	private Chunk chunk;
	
	protected double x;
	protected double y;
	protected double z;
	
	protected PhysicsBody pBody;
	protected Collider collider;
	
	public GameObject(double x, double y) {
		setPos(x, y);
		setZ(0);
	}
	
	public PhysicsBody givePhysicsBody() {
		return givePhysicsBody(1);
	}
	
	public PhysicsBody givePhysicsBody(double mass) {
		return pBody = new PhysicsBody(this).setMass(mass);
	}
	
	public GameObject setCollider(double w, double h) {
		this.collider = new BoxCollider(this, w, h);
		return this;
	}
	
	public boolean hasCollider() { return collider != null; }
	
	public boolean hasCollidedWith(GameObject go) {
		return collider.collidedWith(go.collider);
	}
	
	public Collider getCollider() { return collider; }
	
	public boolean withinDistanceOf(GameObject o, double dist) {
		double dx = x - o.x;
		double dy = y - o.y;
		
		return dx * dx + dy * dy <= dist * dist;
	}
	
	public double getX() { return x; }
	public double getY() { return y; }
	
	public void setPos(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public GameObject setZ(double z) {
		this.z = z;
		return this;
	}
	
	public void moveBy(double x, double y) {
		this.x += x;
		this.y += y;
	}
	
	public void update(int tr) {
		if(pBody != null)
			pBody.update(tr);
		
		Chunk nChunk = getPresumedChunk();
		if(chunk != nChunk)
			chunk.queueTransfer(nChunk, this);
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
		int px = (int)(x/16);
		int py = (int)(y/16);
		return Game.game.getWorld().getChunk(px, py);
	}
}
