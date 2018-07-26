package me.vem.isle;

import static me.vem.isle.Logger.info;

import java.io.IOException;

public class App {
	
	public static final String GAME_TITLE = "Dopey Survival";
	public static final String VERSION = "0.1.15";
	
	public static void newGame() {
		ClientThread.getInstance().start();
		ServerThread.getInstance().start();
	}
	
	public static void shutdown() {
		ClientThread.getInstance().getWindow().dispose();
		System.exit(0);
	}
	
	public static void main(String[] args) throws IOException{
		info("Hello World!");
		
		ClientThread.getInstance();
	}
}
