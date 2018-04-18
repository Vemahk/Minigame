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
			window.repaint();
			window.getCamera().follow(.05);
			try {
				Thread.sleep(33);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public GameWindow getWindow() {
		return window;
	}
}
