package me.isle.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class ResourceLoader {

	public static HashMap<String, BufferedImage> images = new HashMap<>();
	
	public static BufferedImage load(String fileName) {
		if(images.containsKey(fileName))
			return images.get(fileName);
		
		InputStream stream = ResourceLoader.class.getResourceAsStream(fileName);
		
		try {
			BufferedImage foundImage = ImageIO.read(stream);
			if(foundImage == null)
				System.err.printf("Failed to find item %s in resouces folder.", fileName);
			else {
				images.put(fileName, foundImage);
			}
			return foundImage;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
