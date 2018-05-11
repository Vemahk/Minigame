package me.isle.game.objects;

import java.awt.image.BufferedImage;

import me.isle.graphics.Spritesheet;

public interface Drawable {

	public abstract Spritesheet getSpriteSheet();
	public abstract BufferedImage getImage();
	
}
