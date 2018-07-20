package me.vem.isle.game.world;

import java.awt.image.BufferedImage;

import me.vem.isle.graphics.Spritesheet;

public abstract class Land{
	
	private final int x;
	private final int y;
	
	protected Land(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() { return x; }
	public int getY() { return y; }
	
	public abstract Spritesheet getSpriteSheet();
	public abstract BufferedImage getImage();
}