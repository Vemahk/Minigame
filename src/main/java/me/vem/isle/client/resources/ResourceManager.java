package me.vem.isle.client.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class ResourceManager {
	
	static {
		try {
			ResourceManager.registerSpritesheet("main");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void registerSpritesheet(String filename) throws IOException {
		BufferedImage sheet = ImageIO.read(getResource("sprites", filename+".png"));
		
		JSONTokener tokener = new JSONTokener(ResourceManager.getResource("spritedata", filename+".json"));
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
}
