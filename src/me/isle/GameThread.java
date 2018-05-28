package me.isle;

import static me.isle.Logger.debug;
import static me.isle.Logger.info;

import java.util.HashSet;
import java.util.TreeSet;

import me.isle.game.Game;
import me.isle.game.objects.ChunkLoader;
import me.isle.game.objects.GameObject;
import me.isle.game.world.Chunk;
import me.isle.game.world.ChunkQueue;
import me.isle.game.world.World;
import me.isle.graphics.Camera;
import me.isle.graphics.GameWindow;

public class GameThread extends Thread{
	
	public static final boolean DISPLAY_TIME_INFO = true;

	public static double deltaTime; //TODO Implement Delta-time
	
	private int ups; //ups --> Updates per Second
	
	public GameThread(int tickrate) {
		super();
		this.ups = tickrate;
	}
	
	@Override
	public void run() {
		info("Game Thread Started...");
		
		while(true) {
			long start = System.currentTimeMillis();

			/* --==BEGIN PHYSICS UPDATE==-- */
			
			World world = Game.game.getWorld();
			
			long prUp = preUpdate(world);
			long up = update(world);
			long poUp = postUpdate(world);
			
			if(DISPLAY_TIME_INFO) {
				debug("Pre-Update time in nanoseconds: " + prUp);
				debug("Update time in nanoseconds: " + up);
				debug("Post-Update time in nanoseconds: " + poUp);
			}
			
			GameWindow win = Startup.graphicsThread.getWindow();
			if(win!=null) {
				Camera cam = win.getCamera();
				if(cam != null)
					cam.follow(.05);
			}
			
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
		
		for(GameObject go : loadedObjects) {
			go.update(ups);
			if(go.hasCollider())
				for(GameObject go2 : loadedObjects)
					if(go != go2 && go2.hasCollider())
						if(go.hasCollidedWith(go2))
							System.out.println("Collision detected!");
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
