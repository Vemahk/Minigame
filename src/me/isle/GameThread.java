package me.isle;

import static me.isle.Logger.info;

import me.isle.game.objects.GameObject;
import me.isle.graphics.Camera;
import me.isle.graphics.GameWindow;

public class GameThread extends Thread{

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
			}
			
			GameWindow win = Startup.graphicsThread.getWindow();
			if(win!=null) {
				Camera cam = win.getCamera();
				if(cam != null)
					cam.follow(.05);
			}
			
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
}
