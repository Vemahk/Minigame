package me.isle.game.world;

import java.awt.image.BufferedImage;

import me.isle.graphics.Spritesheet;
import me.isle.resources.ResourceManager;

public class Sand extends Land{

	public Sand(int x, int y) {
		super(x, y);
	}
	
	@Override
	public Spritesheet getSpriteSheet() {
		return ResourceManager.getSpritesheet("land.png");
	}
	
	@Override
	public BufferedImage getImage() {
		return getSpriteSheet().getImage(1);
	}
}
