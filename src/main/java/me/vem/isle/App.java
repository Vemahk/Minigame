package me.vem.isle;

import java.io.IOException;

import me.vem.isle.client.ClientThread;
import me.vem.isle.server.ServerThread;
import me.vem.isle.server.game.Game;
import me.vem.isle.server.game.world.World;
import me.vem.utils.test.FTimer;

public class App {
	
	public static final Version VERSION = new Version(0, 1, 20);
	
	public static void main(String[] args) throws IOException{
		Logger.info("Hello World!");
		Logger.infof("Loading %s...", VERSION);
		
		ClientThread.getInstance();
	}
	
	public static void startThreads() {
		ClientThread.getInstance().start();
		ServerThread.getInstance().start();
	}
	
	public static void shutdown() {

		if(World.getInstance() != null)
			new FTimer("World Save", () -> {
				if(!Game.save())
					Logger.error("Game save failed! OOOH NOOOOO!! CONTRAC THE DEVELOPER; THIS IS PROBLEM.");
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
	
	
}
