package me.vem.isle;

import static me.vem.isle.Logger.info;

import me.vem.isle.graphics.Animation;

public class GraphicsThread extends Thread{

	public static double deltaTime; //TODO Implement Delta-time
	
	private int fps;
	
	public GraphicsThread(int fps) {
		super();
		this.fps = fps;
	}
	
	public int getFPS() { return fps; }
	
	@Override
	public void run() {
		info("Graphics Thread Started...");
		
		while(true) {
			
			long start = System.currentTimeMillis();
			
			synchronized(Animation.all) {
				for(Animation anim : Animation.all)
					anim.tick();
			}
			
			App.getWindow().repaint();
			
			long deltaTime = System.currentTimeMillis() - start;
			long frameDelay = 1000/fps - deltaTime; //Graphics Thread Sleep
			if(frameDelay <= 0) continue;
			
			try {
				Thread.sleep(frameDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
