package me.vem.isle.common.world;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import me.vem.isle.common.Game;
import me.vem.isle.common.objects.GameObject;
import me.vem.utils.Utilities;
import me.vem.utils.logging.Logger;
import me.vem.utils.test.FTimer;

public class WorldThread extends Thread{

	private static final int UPS = 60;
	public static final boolean DISPLAY_TIME_INFO = false;
	
	private static WorldThread instance;
	
	public static void begin(World world) {
		if(instance != null)
			throw new RuntimeException("A WorldThread is already running.");
		
		if(world == null)
			return;
		
		instance = new WorldThread(world);
		instance.start();
	}
	
	public static void end() {
		if(instance != null)
			instance.shutdown();
	}
	
	private final World world;
	private long lastUpdateStart;

	private boolean shutdownRequested = false;
	
	private WorldThread(World world) {
		super("WorldThread");
		this.setDaemon(false);
		
		this.world = world;
	}
	
	@Override
	public void run() {
		Logger.info("WorldThread Started");
		
		lastUpdateStart = System.nanoTime();
		
		while(!shutdownRequested) {
			float dt = (System.nanoTime() - lastUpdateStart) / 1000000000f;
			long start = lastUpdateStart = System.nanoTime();
			
			long up = update(dt);
			
			if(DISPLAY_TIME_INFO) 
				Logger.debugf("Update steps: %.3fms", up / 1000000f);
			
			try {
				Utilities.sleepUnsafe(UPS, System.nanoTime() - start);
			} catch (InterruptedException e) {
				Logger.debugf("WorldThread > Received Interrupt Signal while waiting...");
				continue;
			}
		}
		
		if(this == instance)
			instance = null;
		
		Logger.debugf("WorldThread > Stopping...");
	}
	
	@Override
	public void interrupt() {
		super.interrupt();
		this.shutdownRequested = true;
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

							go2.getPhysics().applyForce(go2.getPos().sub(go.getPos()).inverseMag(3));
						}
						
						go.getPhysics().applyForce(go.getPos().sub(go2.getPos()).inverseMag(3));
					}

			go.update(dt);
		}
		
		return System.nanoTime() - start;
	}
	
	public void shutdown() {
		this.interrupt();
		
		final int tryShutdownCount = 5;
		for(int i=0;i<tryShutdownCount;i++) {
			try {
				this.join(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
			
			if(this.isAlive()) {
				Logger.warningf("Failed to wait for WorldThread to yield. Attempting to wait %d more seconds...", tryShutdownCount - i - 1);
			}else break;
		}
		
		if(this.isAlive()) {
			Logger.errorf("Failed to wait for WorldThread to die, even after %d repeated attempts. Attempting World Save.", tryShutdownCount);
		}
		
		if(this.world != null)
			FTimer.test("World Save", () -> {
				if(!world.saveTo(Game.getWorldFile()))
					Logger.error("Game save failed! OOOH NOOOOO!! CONTACT THE DEVELOPER; THIS IS PROBLEM.");
			});
	}
}
