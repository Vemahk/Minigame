package me.isle;

import static me.isle.Logger.info;
import me.isle.graphics.Animation;
import me.isle.graphics.GameWindow;

public class GraphicsThread extends Thread{
	
	private GameWindow window;
	private int fps;
	
	public GraphicsThread(int fps) {
		super();
		this.fps = fps;
	}
	
	public int getFPS() { return fps; }
	
	@Override
	public void run() {
		info("Graphics Thread Started...");
		
		window = new GameWindow();
		
		while(true) {
			
			long start = System.currentTimeMillis();
			
			synchronized(Animation.all) {
				for(Animation anim : Animation.all)
					anim.tick();
			}
			
			window.repaint();
			
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
	
	public GameWindow getWindow() {
		return window;
	}
}
