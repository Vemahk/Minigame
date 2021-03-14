package me.vem.isle.common.eio;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import me.vem.isle.client.resources.ResourceManager;

public class ExtResourceManager {

	private static File worldsDir = new File("worlds/");
	public static File getWorldsDirectory() {
		if(!worldsDir.exists())
			worldsDir.mkdirs();
		
		return worldsDir;
	}
	
	public static File getDefaultWorldFile() {
		return new File(getWorldsDirectory(), "world.dat");
	}
	
	private static File backupsDir = new File(getWorldsDirectory(), "backups/");
	public static File getBackupsDirectory() {
		if(!backupsDir.exists())
			backupsDir.mkdirs();
		
		return backupsDir;
	}
	
	public static InputStream getResource(String fileName) {
		return ResourceManager.class.getClassLoader().getResourceAsStream(fileName);
	}
	
	public static InputStream getResource(String path, String fileName) {
		return ResourceManager.class.getClassLoader().getResourceAsStream(path + "/" + fileName);
	}
	
	private static FileSystem fs;
	public static Path[] getResourceFilePaths(String folderPath) throws URISyntaxException, IOException {
		URI uri = ResourceManager.class.getResource(folderPath).toURI();
        Path myPath;
        
        if (uri.getScheme().equals("jar")) {
        	if(fs == null)
        		fs = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
        	
            myPath = fs.getPath(folderPath);
        } else {
            myPath = Paths.get(uri);
        }
        
        Iterator<Path> it = Files.walk(myPath, 1).iterator();
        it.next(); //skip the directory path.
        
        List<Path> paths = new ArrayList<>();
        
        for (; it.hasNext();) {
        	paths.add(it.next());
        }
        
        return paths.toArray(new Path[0]);
	}
}
