package me.isle.game;

import me.isle.Startup;
import me.isle.game.objects.GameObject;
import me.isle.graphics.Camera;
import me.isle.graphics.GameWindow;

public class GameThread extends Thread{

	private int tickrate;
	
	public GameThread(int tickrate) {
		super();
		this.tickrate = tickrate;
	}
	
	@Override
	public void run() {
		while(true) {
			
			synchronized(GameObject.all) {
				for(GameObject go : GameObject.all)
					go.update(tickrate);
			}
			
			GameWindow win = Startup.graphicsThread.getWindow();
			if(win!=null) {
				Camera cam = win.getCamera();
				if(cam != null)
					cam.follow(.05);
			}
			
			try {
				Thread.sleep(1000/tickrate);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
