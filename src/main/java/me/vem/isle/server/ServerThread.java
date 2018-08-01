package me.vem.isle.server;

import static me.vem.isle.Logger.debug;
import static me.vem.isle.Logger.info;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import me.vem.isle.App;
import me.vem.isle.server.game.Game;
import me.vem.isle.server.game.objects.GameObject;
import me.vem.isle.server.game.world.Chunk;
import me.vem.isle.server.game.world.World;

public class ServerThread extends Thread{
	
	private static final int UPS = 60;
	public static final boolean DISPLAY_TIME_INFO = false;
	
	private static ServerThread instance;
	public static ServerThread getInstance() {
		if(instance == null)
			instance = new ServerThread();
		return instance;
	}
	
	private ServerThread() {}
	
	private long lastUpdateStart;
	
	@Override
	public void run() {
		info("Game Thread Started...");
		
		Game.gameStartup();
		
		while(true) {
			float dt = (System.nanoTime() - lastUpdateStart) / 1000000000f;
			if(lastUpdateStart == 0) dt = 0;
			long start = lastUpdateStart = System.nanoTime();

			/* --==BEGIN PHYSICS UPDATE==-- */
			
			long prUp = preUpdate();
			long up = update(dt);
			long poUp = postUpdate();
			
			if(DISPLAY_TIME_INFO) {
				debug("Pre-Update time in nanoseconds: " + prUp);
				debug("Update time in nanoseconds: " + up);
				debug("Post-Update time in nanoseconds: " + poUp);
			}
			
			/* --==END PHYSICS UPDATE==-- */
			
			App.sleep(UPS, System.nanoTime() - start);
		}
	}
	
	/**
	 * Part 1 of 3 of the physics update. Handles, currently, chunk loading.
	 * @param world
	 */
	private long preUpdate() {
		long start = System.nanoTime();
		
		HashSet<Chunk> loadedChunks = World.getInstance().getLoadedChunks();
		synchronized(loadedChunks) {
			loadedChunks.clear();
			
			HashSet<GameObject> loaders = GameObject.chunkLoaders;
			synchronized(loaders){
				
				for(GameObject cl : loaders) {
					
					int px = cl.getPos().floorX() >> 4;
					int py = cl.getPos().floorY() >> 4;
					int r = cl.chunkRadius();
					
					for(int x = px - r; x<= px + r; x++)
						for(int y = py - r; y<= py + r; y++)
							loadedChunks.add(World.getInstance().getChunk(x, y));
				}
				
			}
		}
		
		return System.nanoTime() - start;
	}
	
	/**
	 * Part 2 of 3 of the physics update. Handles movement & collisions. (NOTE: collisions not yet implemented)
	 * @param world
	 */
	private long update(float dt) {
		long start = System.nanoTime();
		
		HashSet<Chunk> loadedChunks = World.getInstance().getLoadedChunks();
		TreeSet<GameObject> loadedObjects = new TreeSet<>();
		synchronized(loadedChunks) {
			for(Chunk c : loadedChunks) {
				synchronized(c) {
					for(GameObject go : c.getObjects())
						loadedObjects.add(go);
				}
			}
		}
		
		HashMap<GameObject, List<GameObject>> fsh = new HashMap<>();
		for(GameObject go : loadedObjects) {
			if(go.hasPhysics() && go.hasCollider())
				for(GameObject go2 : loadedObjects)
					if(go != go2 && go2.hasCollider() && go.collidedWith(go2)) {
						if(go2.hasPhysics()) {
							List<GameObject> f = fsh.get(go2);
							if(f != null && f.contains(go))
								continue;
							
							if(f == null)
								fsh.put(go, f = new LinkedList<>());
							f.add(go2);

							go.getPhysics().applyForce(go.getPos().sub(go2.getPos()).inverseMag(3));
							go2.getPhysics().applyForce(go2.getPos().sub(go.getPos()).inverseMag(3));
							
						}else go.getPhysics().applyForce(go.getPos().sub(go2.getPos()).inverseMag(3));
					}

			go.update(dt);
		}
		
		return System.nanoTime() - start;
	}
	
	/**
	 * Part 3 of 3 of the physics update. Handles queued tasks that could not be run during the chunk synchronization.
	 */
	private long postUpdate() {
		long start = System.nanoTime();
		
		GameObject.destroyQueue();
		Chunk.runTransfers();
		
		return System.nanoTime() - start;
	}
}
