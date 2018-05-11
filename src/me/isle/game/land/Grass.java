package me.isle.game.land;

import java.awt.image.BufferedImage;

import me.isle.graphics.Spritesheet;
import me.isle.resources.ResourceManager;

public class Grass extends Land{
	
	public Grass(int x, int y) {
		super(x, y);
	}
	
	@Override
	public Spritesheet getSpriteSheet() {
		return ResourceManager.getSpritesheet("land.png");
	}

	@Override
	public BufferedImage getImage() {
		return getSpriteSheet().getImage(2);
	}

}
