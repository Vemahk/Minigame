package me.vem.isle.client.resources;

import static me.vem.isle.client.graphics.UnitConversion.toUnits;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Sprite {

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
