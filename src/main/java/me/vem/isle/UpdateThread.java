package me.vem.isle;

import static me.vem.isle.Logger.debug;
import static me.vem.isle.Logger.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import me.vem.isle.game.Game;
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
			
			App.getCamera().follow(.05);
			
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
			
			HashSet<Chunk> shouldBeLoaded = new HashSet<Chunk>();
			
			HashSet<ChunkLoader> loaders = GameObject.chunkLoaders;
			synchronized(loaders){
				
				for(ChunkLoader cl : loaders) {
					GameObject go = (GameObject) cl;
					
					int px = (int)(go.getX()/16);
					int py = (int)(go.getY()/16);
					int r = cl.chunkRadius();
					
					for(int x = px - r; x<= px + r; x++) {
						for(int y = py - r; y<= py + r; y++) {
							Chunk c = world.getChunk(x, y);
							if(c != null)
								shouldBeLoaded.add(c);
						}
					}
				}
				
			}
			
			HashSet<Chunk> toLoad = new HashSet<>();
			HashSet<Chunk> toUnload = new HashSet<>();
			
			for(Chunk c : loadedChunks)
				if(!shouldBeLoaded.contains(c))
					toUnload.add(c);
			
			for(Chunk c : shouldBeLoaded)
				if(!loadedChunks.contains(c))
					toLoad.add(c);
			
			loadedChunks.addAll(toLoad);
			toLoad.clear();
			
			loadedChunks.removeAll(toUnload);
			toUnload.clear();
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
		
		HashMap<GameObject, List<GameObject>> fsh = new HashMap<>();
		for(GameObject go : loadedObjects) {
			if(go.hasCollider())
				for(GameObject go2 : loadedObjects)
					if(go != go2 && go2.hasCollider()) {
						if(fsh.containsKey(go2) && fsh.get(go2).contains(go)) continue;
						if(go.hasCollidedWith(go2)) {
							
							if(go.hasPhysicsBody())
								go.getPhysicsBody().applyForce(Vector.posDiff(go, go2).inverseMag(3));
							if(go2.hasPhysicsBody())
								go2.getPhysicsBody().applyForce(Vector.posDiff(go2, go).inverseMag(3));
							
							if(!fsh.containsKey(go))
								fsh.put(go, new ArrayList<GameObject>());
							fsh.get(go).add(go2);
						}
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
		
		for(GameObject go : GameObject.queuedToDestroy)
			go.getAssignedChunk().removeObject(go);
		GameObject.queuedToDestroy.clear();
		
		//TODO Handle queued chunk tradeoffs.
		for(ChunkQueue cq : chunkQueue)
			cq.execute();
		chunkQueue.clear();
		
		return System.nanoTime() - start;
	}
	
	private HashSet<ChunkQueue> chunkQueue = new HashSet<>();
	public void queueChunkTransfer(ChunkQueue cq) {
		chunkQueue.add(cq);
	}
}
