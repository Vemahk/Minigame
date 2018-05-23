package me.isle;

import static me.isle.Logger.info;

import java.io.IOException;

import me.isle.game.Game;

public class Startup {
	
	public static GraphicsThread graphicsThread;
	public static GameThread gameThread;
	
	private static final int fps = 60; //fps --> frames per second
	private static final int ups = 60; //ups --> updates per second
	
	private static final int BOARD_WIDTH = 512;
	private static final int BOARD_HEIGHT = 512;
	
	public static void main(String[] args) throws IOException{
		info("Hello World!");
		
		graphicsThread = new GraphicsThread(fps);
		gameThread = new GameThread(ups);
		
		Game.game = new Game(BOARD_WIDTH, BOARD_HEIGHT);
		Game.game.build();
		
		graphicsThread.start();
		gameThread.start();
	}
}
