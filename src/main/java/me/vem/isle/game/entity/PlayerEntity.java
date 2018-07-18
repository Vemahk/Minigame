package me.vem.isle.game.entity;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import me.vem.isle.game.Input;
import me.vem.isle.game.Game;
import me.vem.isle.game.objects.ChunkLoader;
import me.vem.isle.game.objects.GameObject;
import me.vem.isle.game.physics.Vector;
import me.vem.isle.graphics.Spritesheet;
import me.vem.isle.io.DataFormater;
import me.vem.isle.menu.Setting;
import me.vem.isle.resources.ResourceManager;

public class PlayerEntity extends Entity implements ChunkLoader{
	
	private float speed;
	
	public PlayerEntity(float x, float y) {
		super(x, y);
		this.pBody.setMass(2.5f);
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
		
		int ix = pos.floorX();
		int iy = pos.floorY();
		
		boolean left = Setting.MOVE_LEFT.getState();
		boolean right = Setting.MOVE_RIGHT.getState();
		boolean up = Setting.MOVE_UP.getState();
		boolean down = Setting.MOVE_DOWN.getState();
		
		int xMove = left ^ right ? (left ? -1 : 1) : 0;
		int yMove = up ^ down ? (up ? -1 : 1) : 0;
		
		if(Game.getWorld().isWater(ix, iy))		
			this.getPhysicsBody().setFriction(.6f);
		else this.getPhysicsBody().setFriction(.3f);
		
		/*if(Game.DEBUG_ACTIVE)
			speedMod *= 10;*/
		
		float Fx = xMove * speed;
		float Fy = yMove * speed;
		this.pBody.applyForce(new Vector(Fx, Fy));
	}
	
	public int chunkRadius() { return 1; }

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
