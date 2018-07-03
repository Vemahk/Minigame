package me.isle.game.entity;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import me.isle.game.ArrowKeyListener;
import me.isle.game.Game;
import me.isle.game.objects.ChunkLoader;
import me.isle.game.objects.GameObject;
import me.isle.game.physics.Vector;
import me.isle.graphics.Spritesheet;
import me.isle.io.DataFormater;
import me.isle.resources.ResourceManager;

public class PlayerEntity extends Entity implements ChunkLoader{
	
	private double speed;
	
	public PlayerEntity(double x, double y) {
		super(x, y);
		speed = 3;
	}
	
	@Override
	public Spritesheet getSpriteSheet() {
		return ResourceManager.getSpritesheet("player.png");
	}
	
	@Override
	public BufferedImage getImage() {
		return getSpriteSheet().getImage(0);
	}
	
	@Override
	public void update(int tr) {
		super.update(tr);
		
		ArrowKeyListener akl = Game.getKeyListener();
		
		int xMove = akl == null ? 0 : akl.movementX();
		int yMove = akl == null ? 0 : akl.movementY();
		
		double speedMod = 1;
		if(x >= 0 && x < Game.getWidth() && y >= 0 && y < Game.getHeight())
			if(Game.getWorld().isWater((int)x, (int)y))		
				this.pBody.setFrictionMod(3);
			else this.pBody.setFrictionMod(1);
		
		/*if(Game.DEBUG_ACTIVE)
			speedMod *= 10;*/
		
		double Fx = xMove * speed * speedMod;
		double Fy = yMove * speed * speedMod;
		this.pBody.applyForce(new Vector(Fx, Fy));
	}
	
	public int chunkRadius() { return 1; }

	@Override public int writeSize() { return 8; }

	@Override
	public byte[] compress() {
		byte[] write = new byte[writeSize()];
		DataFormater.add(write, DataFormater.floatToBytes((float)x), 0);
		DataFormater.add(write, DataFormater.floatToBytes((float)y), 4);
		return write;
	}

	@Override
	public GameObject load(FileInputStream fis) throws IOException {
		byte[] read = new byte[writeSize()];
		fis.read(read);
		
		float rx = DataFormater.readFloat(read, 0);
		float ry = DataFormater.readFloat(read, 4);
		
		this.x = rx;
		this.y = ry;
		
		return this;
	}
}