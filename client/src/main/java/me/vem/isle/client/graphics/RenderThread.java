package me.vem.isle.client.graphics;

import java.util.LinkedList;
import java.util.Queue;

import me.vem.isle.client.Client;
import me.vem.utils.Utilities;
import me.vem.utils.logging.Logger;

public class RenderThread extends Thread {
	
	private static final Object PAUSE_LOCK = new Object();
	
	private static RenderThread instance;
	
	public static void fps(int fps) {
		if(instance == null)
			return;
		
		instance.setFps(fps);
	}
	
	public static int fps() {
		if(instance == null)
			return -1;
		
		return instance.getDesiredFps();
	}
	
	public static float realFps() {
		if(instance == null)
			return 0;
		
		return instance.getRealFps();
	}
	
	public static void begin(Client client, int fps) {
		if(instance != null)
			return;
		
		instance = new RenderThread(client).setFps(fps);
		instance.start();
	}
	
	public static void pauseRender() {
		if(instance == null)
			return;
		
		instance.pause();
	}
	
	public static void unpauseRender() {
		if(instance == null)
			return;
		
		instance.unpause();
	}
	
	public static void end() {
		if(instance == null)
			return;
		
		instance.shutdown();
	}
	
	private final Client client;
	
	private boolean stopped = false;
	private boolean paused = false;
	
	private int fps = 60;
	
	private RenderThread(Client client) {
		super("RenderThread");
		this.setDaemon(true);
		
		this.client = client;
	}
	
	private RenderThread setFps(int fps) {
		this.fps = fps;
		return this;
	}
	
	private int getDesiredFps() {
		return this.fps;
	}
	
	private Queue<Long> renderTimes = new LinkedList<Long>();
	
	@Override
	public void run() {
		Logger.info("RenderThread > Started");
		
		while(!stopped) {
			if(paused) {
				synchronized(PAUSE_LOCK) {
					while(paused) {
						try {
							PAUSE_LOCK.wait();
						} catch (InterruptedException e) {
							break;
						}
					}
				}
				
				if(stopped)
					break;
			}
			
			long timeToRender = client.render();
			renderTimes.offer(timeToRender);
			
			if(renderTimes.size() > getDesiredFps())
				renderTimes.poll();
			
			Utilities.sleep(this.fps, timeToRender);
		}
		
		Logger.debugf("RenderThread > Stopped");
	}
	
	public float getRealFps() {
		double avg = 0;
		int size = renderTimes.size();
		
		for(Long l : renderTimes)
			avg += l / 1000000000.0;
		
		return (float) (size / avg);
	}
	
	public void pause() {
		paused = true;
	}
	
	public void unpause() {
		paused = false;
		synchronized(PAUSE_LOCK) {
			PAUSE_LOCK.notifyAll();
		}
	}
	
	public void shutdown() {
		this.interrupt();
	}
	
	@Override
	public void interrupt() {
		super.interrupt();
		stopped = true;
	}
}
