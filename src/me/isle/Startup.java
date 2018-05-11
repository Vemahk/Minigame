package me.isle;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.isle.game.Game;
import me.isle.game.GameThread;
import me.isle.graphics.GraphicsThread;

public class Startup {
	
	public static GraphicsThread graphicsThread;
	public static GameThread gameThread;
	
	private static final int gTickrate = 50;
	
	public static void main(String[] args) throws IOException{
		info("Hello World!");
		
		graphicsThread = new GraphicsThread();
		gameThread = new GameThread(gTickrate);
		
		Game.game = new Game();
		
		graphicsThread.start();
		gameThread.start();
	}
	
	public static void info(String out) {
		System.out.printf("[%s][%s]> %s%n", "INFO", timeFormat(), out);
	}
	
	private static String timeFormat() {
		return new SimpleDateFormat("HH.mm.ss").format(new Date());
	}
}
