package me.isle.game.land;

import java.awt.image.BufferedImage;

import me.isle.game.objects.Drawable;
import me.isle.resources.ResourceLoader;

public class Land implements Drawable{
	
	private final int x;
	private final int y;
	
	public Land(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() { return x; }
	public int getY() { return y; }
	
	@Override
	public String getResourcePath() {
		return "land.png";
	}
	
	@Override
	public BufferedImage getImage() {
		return ResourceLoader.get(getResourcePath());
	}
}
