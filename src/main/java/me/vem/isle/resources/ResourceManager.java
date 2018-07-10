package me.vem.isle.resources;

import static me.vem.isle.Logger.*;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

import me.vem.isle.graphics.Spritesheet;

public class ResourceManager {

	public static HashMap<String, Spritesheet> spritesheets = new HashMap<>();
	
	public static final int IMAGE_SIZE = 32;
	
	public static Spritesheet getSpritesheet(String fileName) {
		if(spritesheets.containsKey(fileName))
			return spritesheets.get(fileName);
		
		InputStream stream = ResourceManager.class.getClassLoader().getResourceAsStream(fileName);
		
		try {
			BufferedImage found = ImageIO.read(stream);
			if(found == null)
				warning("Failed to find item "+fileName+" in resouces folder.");
			else {
				if(found.getWidth()%IMAGE_SIZE != 0 || found.getHeight()%IMAGE_SIZE != 0)
					info("Spritesheet "+ fileName +" has odd dimensions. Proceeding with caution.");
				
				int len = (found.getWidth()/IMAGE_SIZE) * (found.getHeight()/IMAGE_SIZE);
				Spritesheet out = new Spritesheet(len);
				
				for(int x=0;x<found.getWidth()/IMAGE_SIZE;x++) {
					for(int y=0;y<found.getHeight()/IMAGE_SIZE;y++) {
						BufferedImage subImage = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, found.getType());
						
						Graphics g = subImage.getGraphics();
						g.drawImage(found, -x*IMAGE_SIZE, -y*IMAGE_SIZE, null);
						
						int imageIndex = x + y*(found.getWidth()/IMAGE_SIZE);
						while(!out.setImage(imageIndex, subImage))
							imageIndex++;
					}
				}
				
				spritesheets.put(fileName, out);
				return out;
			}
		}catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
}
