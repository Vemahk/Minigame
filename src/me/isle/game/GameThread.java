package me.isle.game;

import me.isle.game.objects.GameObject;

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
			
			try {
				Thread.sleep(1000/tickrate);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
