package me.vem.isle.graphics;

import static me.vem.isle.Logger.info;

import java.awt.image.BufferedImage;

public class Spritesheet {

	private BufferedImage[] sprites;
	
	public Spritesheet(int size) {
		sprites = new BufferedImage[size];
	}
	
	public void setSize(int i) {
		if(sprites.length == i) return;
		if(sprites.length < i) {
			BufferedImage[] nSprites = new BufferedImage[i];
			
			for(int x=0;x<sprites.length;x++)
				nSprites[x] = sprites[x];
			
			sprites = nSprites;
		}else {
			info("Attempting spritesheet truncation... May lose some sprites.");
			BufferedImage[] nSprites = new BufferedImage[i];
			
			for(int x=0;x<nSprites.length && sprites[x] != null; x++)
				nSprites[x] = sprites[x];
			
			sprites = nSprites;
		}
	}
	
	public boolean setImage(int i, BufferedImage image) {
		if(sprites[i] != null)
			return false;
		
		sprites[i] = image;
		return true;
	}
	
	public BufferedImage getImage(int i) {
		return sprites[i];
	}
}
