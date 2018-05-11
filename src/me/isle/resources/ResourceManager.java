package me.isle.resources;

import static me.isle.Startup.info;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

import me.isle.graphics.Spritesheet;

public class ResourceManager {

	public static HashMap<String, Spritesheet> spritesheets = new HashMap<>();
	
	public static Spritesheet getSpritesheet(String fileName) {
		if(spritesheets.containsKey(fileName))
			return spritesheets.get(fileName);
		
		InputStream stream = ResourceManager.class.getResourceAsStream(fileName);
		
		try {
			BufferedImage found = ImageIO.read(stream);
			if(found == null)
				System.err.printf("Failed to find item %s in resouces folder.", fileName);
			else {
				if(found.getWidth()%50 != 0 || found.getHeight()%50 != 0)
					info("Spritesheet "+ fileName +" has odd dimensions. Proceeding with caution.");
				
				int len = (found.getWidth()/50) * (found.getHeight()/50);
				Spritesheet out = new Spritesheet(len);
				
				for(int x=0;x<found.getWidth()/50;x++) {
					for(int y=0;y<found.getHeight()/50;y++) {
						BufferedImage subImage = new BufferedImage(50, 50, found.getType());
						
						Graphics g = subImage.getGraphics();
						g.drawImage(found, -x*50, -y*50, null);
						
						int imageIndex = x + y*(found.getWidth()/50);
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
