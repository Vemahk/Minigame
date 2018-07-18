package me.vem.isle;

import static me.vem.isle.Logger.debug;
import static me.vem.isle.Logger.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TreeSet;

import me.vem.isle.game.Game;
import me.vem.isle.game.entity.Entity;
import me.vem.isle.game.objects.ChunkLoader;
import me.vem.isle.game.objects.GameObject;
import me.vem.isle.game.physics.Vector;
import me.vem.isle.game.world.Chunk;
import me.vem.isle.game.world.ChunkQueue;
import me.vem.isle.game.world.World;

public class UpdateThread extends Thread{
	
	public static final boolean DISPLAY_TIME_INFO = false;

	public static double deltaTime; //TODO Implement Delta-time
	
	private int ups; //ups --> Updates per Second
	
	public UpdateThread(int tickrate) {
		super();
		this.ups = tickrate;
	}
	
	@Override
	public void run() {
		info("Game Thread Started...");
		
		while(true) {
			long start = System.currentTimeMillis();

			/* --==BEGIN PHYSICS UPDATE==-- */
			
			World world = Game.getWorld();
			
			long prUp = preUpdate(world);
			long up = update(world);
			long poUp = postUpdate(world);
			
			if(DISPLAY_TIME_INFO) {
				debug("Pre-Update time in nanoseconds: " + prUp);
				debug("Update time in nanoseconds: " + up);
				debug("Post-Update time in nanoseconds: " + poUp);
			}
			
			App.getCamera().follow(.05f);
			
			/* --==END PHYSICS UPDATE==-- */
			
			long deltaTime = System.currentTimeMillis() - start;
			
			long tickDelay = 1000/ups - deltaTime;
			if(tickDelay<=0) continue;
			
			try {
				Thread.sleep(tickDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Part 1 of 3 of the physics update. Handles, currently, chunk loading.
	 * @param world
	 */
	private long preUpdate(World world) {
		long start = System.nanoTime();
		
		HashSet<Chunk> loadedChunks = world.getLoadedChunks();
		synchronized(loadedChunks) {
			loadedChunks.clear();
			
			HashSet<ChunkLoader> loaders = GameObject.chunkLoaders;
			synchronized(loaders){
				
				for(ChunkLoader cl : loaders) {
					GameObject go = (GameObject) cl;
					
					int px = go.getPos().floorX() >> 4;
					int py = go.getPos().floorY() >> 4;
					int r = cl.chunkRadius();
					
					for(int x = px - r; x<= px + r; x++)
						for(int y = py - r; y<= py + r; y++)
							loadedChunks.add(world.getChunk(x, y));
				}
				
			}
		}
		
		return System.nanoTime() - start;
	}
	
	/**
	 * Part 2 of 3 of the physics update. Handles movement & collisions. (NOTE: collisions not yet implemented)
	 * @param world
	 */
	private long update(World world) {
		long start = System.nanoTime();
		
		HashSet<Chunk> loadedChunks = world.getLoadedChunks();
		TreeSet<GameObject> loadedObjects = new TreeSet<>();
		synchronized(loadedChunks) {
			for(Chunk c : loadedChunks) {
				TreeSet<GameObject> objs = c.getObjects();
				synchronized(objs) {
					for(GameObject go : objs)
						loadedObjects.add(go);
				}
			}
		}
		
		HashMap<Entity, List<Entity>> fsh = new HashMap<>();
		for(GameObject go : loadedObjects) {
			if(go instanceof Entity && go.hasCollider())
				for(GameObject go2 : loadedObjects)
					if(go != go2 && go2.hasCollider() && go.hasCollidedWith(go2)) {
						Entity ent = (Entity)go;
						if(go2 instanceof Entity) {
							Entity ent2 = (Entity)go2;
							List<Entity> f = fsh.get(ent2);
							if(f != null && f.contains(ent))
								continue;
							
							if(f == null)
								fsh.put(ent, f = new LinkedList<>());
							f.add(ent2);

							ent.getPhysicsBody().applyForce(ent.getPos().sub(ent2.getPos()).inverseMag(3));
							ent2.getPhysicsBody().applyForce(ent2.getPos().sub(ent.getPos()).inverseMag(3));
							
						}else ent.getPhysicsBody().applyForce(ent.getPos().sub(go2.getPos()).inverseMag(3));
					}

			go.update(ups);
		}
		
		return System.nanoTime() - start;
	}
	
	/**
	 * Part 3 of 3 of the physics update. Handles queued tasks that could not be run during the chunk synchronization.
	 */
	private long postUpdate(World world) {
		long start = System.nanoTime();
		
		while(!GameObject.queuedToDestroy.isEmpty()) {
			GameObject go = GameObject.queuedToDestroy.poll();
			go.getAssignedChunk().removeObject(go);
		}
		
		//TODO Handle queued chunk tradeoffs.
		while(!chunkQueue.isEmpty())
			chunkQueue.poll().execute();
		
		return System.nanoTime() - start;
	}
	
	private Queue<ChunkQueue> chunkQueue = new LinkedList<>();
	public void queueChunkTransfer(ChunkQueue cq) {
		chunkQueue.add(cq);
	}
}
