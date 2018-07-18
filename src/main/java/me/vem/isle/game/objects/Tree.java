package me.vem.isle.game.objects;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import me.vem.isle.graphics.Spritesheet;
import me.vem.isle.io.DataFormater;
import me.vem.isle.resources.ResourceManager;

public class Tree extends GameObject{

	public Tree(float x, float y) {
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
	public void update(int tr) {}

	@Override public int writeSize() { return 8; }

	@Override
	public byte[] compress() {
		byte[] write = new byte[writeSize()];
		DataFormater.add(write, DataFormater.floatToBytes(pos.getX()), 0);
		DataFormater.add(write, DataFormater.floatToBytes(pos.getY()), 4);
		return write;
	}

	@Override
	public GameObject load(FileInputStream fis) throws IOException {
		byte[] read = new byte[writeSize()];
		fis.read(read);
		
		float rx = DataFormater.readFloat(read, 0);
		float ry = DataFormater.readFloat(read, 4);

		this.pos.set(rx, ry);
		
		return this;
	}
}
