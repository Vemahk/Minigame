package me.vem.isle.game.world;

import java.awt.image.BufferedImage;

import me.vem.isle.graphics.Spritesheet;
import me.vem.isle.resources.ResourceManager;

public class Water extends Land{

	public Water(int x, int y) {
		super(x, y);
	}
	
	@Override
	public Spritesheet getSpriteSheet() {
		return ResourceManager.getSpritesheet("land.png");
	}
	
	@Override
	public BufferedImage getImage() {
		return getSpriteSheet().getImage(0);
	}

}
