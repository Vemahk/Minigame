package me.vem.isle.server;

import static me.vem.isle.Logger.debug;
import static me.vem.isle.Logger.info;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
		
		Game.startup();
		lastUpdateStart = System.nanoTime();
		
		while(true) {
			float dt = (System.nanoTime() - lastUpdateStart) / 1000000000f;
			long start = lastUpdateStart = System.nanoTime();

			/* --==BEGIN PHYSICS UPDATE==-- */
			
			long prUp = preUpdate();
			long up = update(dt);
			
			if(DISPLAY_TIME_INFO) {
				debug("Pre-Update time in nanoseconds: " + prUp);
				debug("Update time in nanoseconds: " + up);
			}
			
			/* --==END PHYSICS UPDATE==-- */
			
			App.sleep(UPS, System.nanoTime() - start);
		}
	}
	
	/**
	 * Part 1 of 2 of the physics update. Handles chunk loading.
	 * @param world
	 */
	private long preUpdate() {
		long start = System.nanoTime();
		
		GameObject.destroyQueue();
		Chunk.runTransfers();
		
		World.getInstance().loadChunkQueue();
		
		return System.nanoTime() - start;
	}
	
	/**
	 * Part 2 of 2 of the physics update. Handles movement & collisions.
	 * @param dt
	 */
	private long update(float dt) {
		long start = System.nanoTime();
		
		Set<GameObject> loadedObjects = World.getInstance().getLoadedObjects();
		
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
}
