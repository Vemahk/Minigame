package me.isle;

import java.io.IOException;

import me.isle.game.Game;
import me.isle.game.GameThread;
import me.isle.graphics.GraphicsThread;

public class Startup {

	public static Game game;
	
	public static GraphicsThread graphicsThread;
	public static GameThread gameThread;
	
	public static void main(String[] args) throws IOException{
		System.out.println("Hello World!");
		
		graphicsThread = new GraphicsThread();
		gameThread = new GameThread();
		
		game = new Game();
		
		graphicsThread.start();
		gameThread.start();
		
		
	}
}
