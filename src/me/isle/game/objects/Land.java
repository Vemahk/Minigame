package me.isle.game.objects;

import java.awt.image.BufferedImage;

import me.isle.resources.ResourceLoader;

public class Land implements Drawable{
	
	private int x;
	private int y;
	
	public Land(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String getResourcePath() {
		return "land.png";
	}
	
	@Override
	public BufferedImage getImage() {
		return ResourceLoader.load(getResourcePath());
	}
	
}
