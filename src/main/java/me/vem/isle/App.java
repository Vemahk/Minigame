package me.vem.isle;

import static me.vem.isle.Logger.info;

import java.io.IOException;

import me.vem.isle.client.ClientThread;
import me.vem.isle.server.ServerThread;
import me.vem.isle.server.game.world.World;
import me.vem.utils.test.FTimer;

public class App {
	
	public static final String GAME_TITLE = "Dopey Survival";
	public static final String VERSION = "0.1.17";
	
	public static void newGame() {
		ClientThread.getInstance().start();
		ServerThread.getInstance().start();
	}
	
	public static void shutdown() {

		new FTimer("World Save", () -> {
			if(!World.save())
				Logger.error("World save failed! OOOH NOOOOO!! CONTRAC THE DEVELOPER; THIS IS PROBLEM.");
		}).test();

		ClientThread.getInstance().getWindow().dispose();
		System.exit(0);
	}
	
	public static void sleep(int hz) { sleep(hz, 0); }
	
	public static void sleep(int hz, long dt) {
		long nanodelay = 1000000000 / hz - dt;
		if(nanodelay < 0) return;
		
		try {
			Thread.sleep(nanodelay / 1000000, (int)(nanodelay % 1000000));
		} catch (InterruptedException e) {}
	}
	
	public static void main(String[] args) throws IOException{
		info("Hello World!");
		
		ClientThread.getInstance();
	}
}
