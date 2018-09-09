package me.vem.isle.server;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import me.vem.isle.common.Game;
import me.vem.isle.common.objects.GameObject;
import me.vem.isle.common.world.Chunk;
import me.vem.isle.common.world.World;
import me.vem.utils.Utilities;
import me.vem.utils.logging.Logger;
import me.vem.utils.logging.Version;
import me.vem.utils.test.FTimer;

//TODO Investigate LocalServer subclass...

public class Server extends Thread{
	
	public static final Version VERSION = new Version(0, 1, 23, "Dopey Survival");
	
	private static final int UPS = 60;
	public static final boolean DISPLAY_TIME_INFO = false;
	
	private static Server instance;
	public static Server getInstance() {
		return instance;
	}
	
	public static Server init(int seed) {
		return instance = new Server(seed);
	}
	
	public static Server init(File worldFile) {
		return instance = new Server(worldFile);
	}
	
	private Server(int seed) {
		super("Dopey Survival Server Thread");
		Game.newGame(seed);
	}
	
	public Server(File worldFile) {
		super("Dopey Survival Server Thread");
		Game.loadGame(worldFile);
	}
	
	private long lastUpdateStart;
	
	@Override
	public void run() {
		Logger.info("Server Thread Started");
		
		lastUpdateStart = System.nanoTime();
		
		while(!isKilled) {
			float dt = (System.nanoTime() - lastUpdateStart) / 1000000000f;
			long start = lastUpdateStart = System.nanoTime();
			
			long up = update(dt);
			
			if(DISPLAY_TIME_INFO) 
				Logger.debugf("Update steps: %.3fms", up / 1000000f);
			
			Utilities.sleep(UPS, System.nanoTime() - start);
		}
	}
	
	/**
	 * Part 1 of 2 of the physics update. Handles chunk loading.
	 * @param world
	 */
	private void preUpdate() {
		Chunk.runTransfers();
		GameObject.destroyQueue();
		Chunk.loadChunkQueue();
	}
	
	/**
	 * @param dt
	 */
	private long update(float dt) {
		long start = System.nanoTime();
		
		preUpdate();
		
		Set<GameObject> loadedObjects = Chunk.getLoadedObjects();
		
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
	
	private boolean isKilled;
	
	public void shutdown() {
		if(World.getInstance() != null)
			new FTimer("World Save", () -> {
				if(!Game.save())
					Logger.error("Game save failed! OOOH NOOOOO!! CONTACT THE DEVELOPER; THIS IS PROBLEM.");
			}).test();
		
		isKilled = true;
	}
	
	public static void main(String... args) {
		for(int i=0;i<args.length;i++) {
			
		}
		
		
	}
}
