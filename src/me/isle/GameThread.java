package me.isle;

import static me.isle.Logger.info;

import java.util.HashSet;
import java.util.TreeSet;

import me.isle.game.Game;
import me.isle.game.objects.ChunkLoader;
import me.isle.game.objects.GameObject;
import me.isle.game.world.Chunk;
import me.isle.game.world.World;
import me.isle.graphics.Camera;
import me.isle.graphics.GameWindow;

public class GameThread extends Thread{

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
			
			World world = Game.game.getWorld();
			HashSet<Chunk> loadedChunks = Game.game.getWorld().getLoadedChunks();
			synchronized(loadedChunks) {
				
				HashSet<Chunk> shouldBeLoaded = new HashSet<Chunk>();
				
				HashSet<ChunkLoader> loaders = GameObject.chunkLoaders;
				synchronized(loaders){
					
					for(ChunkLoader cl : loaders) {
						GameObject go = (GameObject) cl;
						
						int px = (int)(go.getX()/16);
						int py = (int)(go.getY()/16);
						int r = cl.chunkRadius();
						
						for(int x = px - r; x<= px + r; x++)
							for(int y = py - r; y<= py + r; y++)
								shouldBeLoaded.add(world.getChunk(x, y));
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
				
				for(Chunk c : loadedChunks) {
					TreeSet<GameObject> objs = c.getObjects();
					synchronized(objs) {
						for(GameObject go : objs)
							go.update(ups);
					}
					
					/*for(GameObject go : GameObject.all) {
						if(go.hasCollider()) {
							for(GameObject go2 : GameObject.all) {
								if(go != go2 && go2.hasCollider() && go.withinDistanceOf(go2, 3))
									if(go.hasCollidedWith(go2))
										debug("Collision occured!");
							}
						}
					}*/
					
				}
			}
			
			for(GameObject go : GameObject.queuedToDestroy)
				go.getAssignedChunk().removeObject(go);
			GameObject.queuedToDestroy.clear();
			
			GameWindow win = Startup.graphicsThread.getWindow();
			if(win!=null) {
				Camera cam = win.getCamera();
				if(cam != null)
					cam.follow(.05);
			}
			
			long deltaTime = System.currentTimeMillis() - start;
			
			/*if(Game.DEBUG_ACTIVE)
				debug("UPS: "+ deltaTime);*/
			
			long tickDelay = 1000/ups - deltaTime;
			if(tickDelay<=0) continue;
			
			try {
				Thread.sleep(tickDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
