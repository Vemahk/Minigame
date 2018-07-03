package me.isle;

import static me.isle.Logger.info;

import java.io.IOException;

import javax.swing.JFrame;

import me.isle.game.ArrowKeyListener;
import me.isle.game.Game;
import me.isle.graphics.Camera;

public class Startup {
	
	public static final String GAME_TITLE = "Dopey Survival";
	public static final String VERSION = "0.1.9";
	
	public static GraphicsThread graphicsThread;
	public static UpdateThread updateThread;
	
	private static final int fps = 60; //fps --> frames per second
	private static final int ups = 60; //ups --> updates per second
	
	private static final int BOARD_WIDTH = 512;
	private static final int BOARD_HEIGHT = 512;
	
	private static JFrame window;
	private static Camera camera;
	
	public static void createWindow() {
		window = new JFrame(GAME_TITLE);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		window.add(camera = new Camera(512, 512, 2));

		window.setResizable(false);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		
		window.addKeyListener(Game.setKeyListener(new ArrowKeyListener()));
		window.requestFocus();
		
		if(Game.DEBUG_ACTIVE)
			Logger.debug("JFrame created and loaded.");
	}
	
	public static JFrame getWindow() { return window; }
	public static Camera getCamera() { return camera; }
	
	public static void main(String[] args) throws IOException{
		info("Hello World!");
		
		createWindow();
		
		Game.gameStartup(BOARD_WIDTH, BOARD_HEIGHT);
		info("Game started.");
		
		graphicsThread = new GraphicsThread(fps);		
		graphicsThread.start();
		
		updateThread = new UpdateThread(ups);
		updateThread.start();
	}
}
