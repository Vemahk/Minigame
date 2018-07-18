package me.vem.isle;

import static me.vem.isle.Logger.info;

import java.io.IOException;

import javax.swing.JFrame;

import me.vem.isle.game.Game;
import me.vem.isle.game.Input;
import me.vem.isle.graphics.Camera;
import me.vem.isle.menu.MainMenu;

public class App {
	
	public static final String GAME_TITLE = "Dopey Survival";
	public static final String VERSION = "0.1.9";
	
	public static GraphicsThread graphicsThread;
	public static UpdateThread updateThread;
	
	private static final int fps = 60; //fps --> frames per second
	private static final int ups = 60; //ups --> updates per second
	
	private static JFrame window;
	public static MainMenu menu;
	private static Camera camera;
	
	public static void createWindow() {
		window = new JFrame(GAME_TITLE);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//window.add(camera = new Camera(512, 512, 2));

		window.add(menu = new MainMenu());
		
		window.setResizable(false);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		
		window.addKeyListener(Game.setInput(new Input()));
		window.requestFocus();
		
		window.repaint();
		
		if(Game.isDebugActive())
			Logger.debug("JFrame created and loaded.");
	}
	
	public static JFrame getWindow() { return window; }
	
	public static void newGame() {
		window.add(camera = new Camera(512, 512, 2));
		
		Game.gameStartup();
		info("Game started.");
		
		graphicsThread = new GraphicsThread(fps);		
		graphicsThread.start();
		
		updateThread = new UpdateThread(ups);
		updateThread.start();
	}
	
	public static Camera getCamera() { return camera; }
	
	public static void shutdown() {
		getWindow().dispose();
		System.exit(0);
	}
	
	public static void main(String[] args) throws IOException{
		info("Hello World!");
		
		createWindow();
	}
}
