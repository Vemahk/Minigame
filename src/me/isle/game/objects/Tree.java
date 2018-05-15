package me.isle.game.objects;

import java.awt.image.BufferedImage;

import me.isle.graphics.Spritesheet;
import me.isle.resources.ResourceManager;

public class Tree extends GameObject{

	public Tree(double x, double y) {
		super(x, y);
	}

	@Override
	public Spritesheet getSpriteSheet() {
		return ResourceManager.getSpritesheet("tree.png");
	}

	@Override
	public BufferedImage getImage() {
		return getSpriteSheet().getImage(0);
	}

	@Override
	public void update(int tr) {
		super.update(tr);
	}
}
