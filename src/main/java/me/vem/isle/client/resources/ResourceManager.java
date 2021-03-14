package me.vem.isle.client.resources;

import java.awt.image.BufferedImage;
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

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class ResourceManager {
	
	public static void registerSpritesheets() throws URISyntaxException, IOException {
		Path[] resources = getResourceFilePaths("/spritedata");
		
		for(Path resourcePath : resources) {
			registerSpritesheet(resourcePath);
		}
	}
	
	public static void registerSpritesheet(Path resourcePath) throws IOException {
		String fileName = resourcePath.getFileName().toString();
		String fileNameWithoutExtension = fileName.substring(0, fileName.indexOf('.'));
		
		String folderName = resourcePath.getParent().getFileName().toString();
		String resourceName = resourcePath.getFileName().toString();
		
		BufferedImage sheet = ImageIO.read(getResource("sprites", fileNameWithoutExtension + ".png"));
		
		JSONTokener tokener = new JSONTokener(ResourceManager.getResource(folderName, resourceName));
		JSONArray root = new JSONArray(tokener);

		for(int i=0;i<root.length();i++) {
			JSONObject spriteDef = root.getJSONObject(i);
			
			String id = spriteDef.getString("id");
			int x = spriteDef.getInt("x");
			int y = spriteDef.getInt("y");
			int w = spriteDef.getInt("w");
			int h = spriteDef.getInt("h");
			
			BufferedImage spriteImage = new BufferedImage(w, h, sheet.getType());
			spriteImage.getGraphics().drawImage(sheet, -x, -y, null);
			
			new Sprite(id, spriteImage);
		}
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
