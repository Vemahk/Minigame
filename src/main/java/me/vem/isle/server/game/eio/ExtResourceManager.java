package me.vem.isle.server.game.eio;

import java.io.File;

public class ExtResourceManager {

	private static File worldsDir = new File("worlds/");
	public static File getWorldsDirectory() {
		if(!worldsDir.exists())
			worldsDir.mkdirs();
		
		return worldsDir;
	}
	
	private static File backupsDir = new File(getWorldsDirectory(), "backups/");
	public static File getBackupsDirectory() {
		if(!backupsDir.exists())
			backupsDir.mkdirs();
		
		return backupsDir;
	}
	
}
