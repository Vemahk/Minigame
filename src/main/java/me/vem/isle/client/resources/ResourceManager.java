package me.vem.isle.client.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import me.vem.isle.common.eio.ExtResourceManager;

public class ResourceManager {
	
	public static void registerSpritesheets() throws URISyntaxException, IOException {
		Path[] resources = ExtResourceManager.getResourceFilePaths("/spritedata");
		
		for(Path resourcePath : resources) {
			registerSpritesheet(resourcePath);
		}
	}
	
	public static void registerSpritesheet(Path resourcePath) throws IOException {
		String fileName = resourcePath.getFileName().toString();
		String fileNameWithoutExtension = fileName.substring(0, fileName.indexOf('.'));
		
		String folderName = resourcePath.getParent().getFileName().toString();
		String resourceName = resourcePath.getFileName().toString();
		
		BufferedImage sheet = ImageIO.read(ExtResourceManager.getResource("sprites", fileNameWithoutExtension + ".png"));
		
		JSONTokener tokener = new JSONTokener(ExtResourceManager.getResource(folderName, resourceName));
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
}
