package me.vem.isle.game.world;

import java.awt.image.BufferedImage;

import me.vem.isle.resources.ResourceManager;

public enum Land{
	
	WATER(0), SAND(1), GRASS(2);
	
	private byte id;
	
	private Land(int id) {
		this.id = (byte)id;
	}
	
	public byte getId() { return id; }
	
	public BufferedImage getImage() {
		return ResourceManager.getSpritesheet("land.png").getImage(id);
	}
	
	public boolean isWater() { return this==WATER; }
	public boolean isSand() { return this==SAND; }
	public boolean isGrass() { return this==GRASS; }
}