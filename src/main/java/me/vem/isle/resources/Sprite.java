package me.vem.isle.resources;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import static me.vem.isle.graphics.UnitConversion.toFloat;

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
	
	public float getWidth() { return toFloat(image.getWidth()); }
	public float getHeight() { return toFloat(image.getHeight()); }
	public int getPixelWidth() { return image.getWidth(); }
	public int getPixelHeight() { return image.getHeight(); }
	
}
