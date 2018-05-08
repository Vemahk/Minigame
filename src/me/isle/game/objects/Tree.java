package me.isle.game.objects;

import java.awt.image.BufferedImage;

import me.isle.resources.ResourceLoader;

public class Tree extends GameObject{

	public Tree(double x, double y) {
		super(x, y);
	}

	@Override
	public String getResourcePath() {
		return "tree.png";
	}

	@Override
	public BufferedImage getImage() {
		return ResourceLoader.get(getResourcePath());
	}

	@Override
	public void update(int tr) {}
}
