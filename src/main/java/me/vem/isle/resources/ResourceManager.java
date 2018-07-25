package me.vem.isle.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ResourceManager {
	
	static {
		try {
			ResourceManager.registerSpritesheet("main");
		} catch (IOException | DocumentException e) {
			e.printStackTrace();
		}
	}
	
	public static void registerSpritesheet(String filename) throws IOException, DocumentException {
		BufferedImage sheet = ImageIO.read(getResource("sprites", filename+".png"));
		
		SAXReader reader = new SAXReader();
		Document doc = reader.read(getResource("spritedata", filename+".xml"));
		
		Element root = doc.getRootElement();
		for(Element spr : root.elements()) {
			String id = spr.attributeValue("id");
			int x = Integer.parseInt(spr.attributeValue("x"));
			int y = Integer.parseInt(spr.attributeValue("y"));
			int w = Integer.parseInt(spr.attributeValue("w"));
			int h = Integer.parseInt(spr.attributeValue("h"));
			
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
