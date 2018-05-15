package me.isle;

import static me.isle.Logger.info;

import me.isle.game.Game;
import me.isle.game.objects.GameObject;
import me.isle.graphics.Camera;
import me.isle.graphics.GameWindow;

import static me.isle.Logger.debug;

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

			synchronized(GameObject.all) {
				for(GameObject go : GameObject.all)
					go.update(ups);
				
				/*for(GameObject go : GameObject.all) {
					if(go.hasCollider()) {
						for(GameObject go2 : GameObject.all) {
							if(go != go2 && go2.hasCollider() && go.withinDistanceOf(go2, 3))
								if(go.hasCollidedWith(go2))
									debug("Collision occured!");
						}
					}
				}*/
				
				GameObject.all.removeAll(GameObject.queuedToDestroy);
				GameObject.queuedToDestroy.clear();
			}
			
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
