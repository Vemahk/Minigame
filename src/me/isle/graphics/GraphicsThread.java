package me.isle.graphics;

public class GraphicsThread extends Thread{

	public GraphicsThread() {
		super();
	}
	
	@Override
	public void run() {
		new GameWindow();
	}
	
}
