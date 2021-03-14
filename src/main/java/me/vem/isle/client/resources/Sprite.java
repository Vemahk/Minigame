package me.vem.isle.client.resources;

import static me.vem.isle.client.graphics.UnitConversion.toUnits;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import me.vem.isle.common.io.ResourceManager;

public class Sprite {

	public static void registerSpritesheets() throws URISyntaxException, IOException {
		Path[] resources = ResourceManager.getResourceFilePaths("/spritedata");
		
		for(Path resourcePath : resources) {
			registerSpritesheet(resourcePath);
		}
	}
	
	public static void registerSpritesheet(Path resourcePath) throws IOException {
		String fileName = resourcePath.getFileName().toString();
		String fileNameWithoutExtension = FilenameUtils.removeExtension(fileName);
		
		BufferedImage sheet = ImageIO.read(ResourceManager.getResource("sprites", fileNameWithoutExtension + ".png"));
		
		JSONTokener tokener = new JSONTokener(ResourceManager.getResource(resourcePath.toString()));
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
	
	private static HashMap<String, Sprite> sprites = new HashMap<>();
	public static Sprite get(String id) { return sprites.get(id); }
	
	private final BufferedImage image;
	private final String id;
	
	public Sprite(String id, BufferedImage image) {
		this.id = id;
		this.image = image;
		
		sprites.put(id, this);
	}
	
	public String getId() { return id; }
	public BufferedImage getImage() { return image; }
	
	public float getWidth() { return toUnits(image.getWidth()); }
	public float getHeight() { return toUnits(image.getHeight()); }
	public int getPixelWidth() { return image.getWidth(); }
	public int getPixelHeight() { return image.getHeight(); }
	
}
