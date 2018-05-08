package me.isle.game.land;

import java.awt.image.BufferedImage;

import me.isle.resources.ResourceLoader;

public class Grass extends Land{
	
	public Grass(int x, int y) {
		super(x, y);
	}
	
	@Override
	public String getResourcePath() {
		return "grass.png";
	}

	@Override
	public BufferedImage getImage() {
		return ResourceLoader.get(getResourcePath());
	}

}
