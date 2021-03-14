package me.vem.isle.server;

import java.io.File;
import java.util.Random;

import me.vem.isle.common.Game;
import me.vem.utils.logging.Version;

public class Server{
	
	public static final Version VERSION = new Version(0, 1, 23, "Dopey Survival");
	
	private static Server instance;
	public static Server getInstance() {
		return instance;
	}
	
	private Server() {
		this(new Random().nextInt());
	}
	
	private Server(int seed) {
		Game.newWorld(seed);
	}
	
	public Server(File worldFile) {
		Game.loadWorld(worldFile);
	}
	
	public static void main(String... args) {
		for(int i=0;i<args.length;i++) {
			
		}
		
		instance = new Server();
	}
}
