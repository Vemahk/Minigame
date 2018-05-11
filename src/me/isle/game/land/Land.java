package me.isle.game.land;

import java.awt.image.BufferedImage;

import me.isle.game.objects.Drawable;
import me.isle.graphics.Animation;
import me.isle.graphics.Spritesheet;
import me.isle.resources.ResourceManager;

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
	public Spritesheet getSpriteSheet() {
		return ResourceManager.getSpritesheet("land.png");
	}
	
	@Override
	public BufferedImage getImage() {
		return getSpriteSheet().getImage(1);
	}
}
