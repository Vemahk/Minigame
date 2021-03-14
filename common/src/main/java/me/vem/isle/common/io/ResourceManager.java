package me.vem.isle.common.io;

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

public class ResourceManager {

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
	
	public static InputStream getResource(Path pathToFile) {
		return getResource(pathToFile.toString());
	}
	
	public static InputStream getResource(String path, String fileName) {
		return getResource(path + "/" + fileName);
	}
	
	public static InputStream getResource(String filePath) {
		return ResourceManager.class.getClassLoader().getResourceAsStream(filePath);
	}
	
	private static FileSystem fs;
	public static Path[] getResourceFilePaths(String folderPath) throws URISyntaxException, IOException {
		URI rootUri = ResourceManager.class.getResource("").toURI();
		
        if (rootUri.getScheme().equals("jar")) {
        	//Project is packaged in a jar file... Should be agnostic to true location of jar.
        	if(fs == null)
        		fs = FileSystems.newFileSystem(rootUri, Collections.<String, Object>emptyMap());
        	
            Path myPath = fs.getPath(folderPath);
            return getResourceFilePathsInternal(myPath, 0);
        } else {
        	//Project is in an IDE...
        	// Tempted to do some custom logic to get the absolute path trimmed to a relative resource path, but gotta do some looking at it first.

    		rootUri = ResourceManager.class.getResource("/").toURI();
        	
    		URI uri = ResourceManager.class.getResource(folderPath).toURI();
    		Path rootPath = Paths.get(rootUri);
            Path myPath = Paths.get(uri);
            return getResourceFilePathsInternal(myPath, rootPath.getNameCount());
        }
	}
	
	private static Path[] getResourceFilePathsInternal(Path resourcePath, int trimLength) throws IOException {
        Iterator<Path> it = Files.walk(resourcePath, 1).iterator();
        it.next(); //skip the directory path.
        
        List<Path> paths = new ArrayList<>();
        
        for (; it.hasNext();) {
        	Path targetResource = it.next();
        	
        	targetResource = targetResource.subpath(trimLength, targetResource.getNameCount());
        	
        	paths.add(targetResource);
        }
        
        return paths.toArray(new Path[0]);
	}
}
