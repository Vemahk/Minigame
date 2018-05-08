package me.isle.game.land;

import java.awt.image.BufferedImage;

import me.isle.resources.ResourceLoader;

public class Water extends Land{

	public Water(int x, int y) {
		super(x, y);
	}
	
	@Override
	public String getResourcePath() {
		return "water.png";
	}
	
	@Override
	public BufferedImage getImage() {
		return ResourceLoader.get(getResourcePath());
	}

}
