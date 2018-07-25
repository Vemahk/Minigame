package me.vem.isle;

import static me.vem.isle.Logger.info;

import java.io.IOException;

import org.dom4j.DocumentException;

import me.vem.isle.graphics.Animation;
import me.vem.isle.graphics.Camera;
import me.vem.isle.resources.ResourceManager;

public class ClientThread extends Thread{

	public static final int FPS = 60;
	
	private static ClientThread instance;
	public static ClientThread getInstance() {
		if(instance == null)
			instance = new ClientThread();
		return instance;
	}
	
	private ClientThread() {}
	
	@Override
	public void run() {
		info("Graphics Thread Started...");
		
		while(true) {
			
			long start = System.currentTimeMillis();
			
			synchronized(Animation.all) {
				for(Animation anim : Animation.all)
					anim.tick();
			}

			Camera.getInstance().follow(.05f);
			App.getWindow().repaint();
			
			long deltaTime = System.currentTimeMillis() - start;
			long frameDelay = 1000/FPS - deltaTime; //Graphics Thread Sleep
			if(frameDelay <= 0) continue;
			
			try {
				Thread.sleep(frameDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
