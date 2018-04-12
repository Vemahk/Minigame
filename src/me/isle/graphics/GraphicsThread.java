package me.isle.graphics;

public class GraphicsThread extends Thread{

	private GameWindow window;
	
	public GraphicsThread() {
		super();
	}
	
	@Override
	public void run() {
		window = new GameWindow();
		
		while(true) {
			
			int xMove = window.getKeyListener().movementX();
			int yMove = window.getKeyListener().movementY();
			
			window.getCamera().moveX(xMove * 3 / 30.0);
			window.getCamera().moveY(yMove * 3 / 30.0);
			
			window.repaint();
			
			try {
				Thread.sleep(33);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
