package me.vem.isle.game.entity;

import java.io.FileInputStream;
import java.io.IOException;

import me.vem.isle.game.Game;
import me.vem.isle.game.objects.ChunkLoader;
import me.vem.isle.game.objects.GameObject;
import me.vem.isle.game.objects.Property;
import me.vem.isle.game.physics.Vector;
import me.vem.isle.io.DataFormater;
import me.vem.isle.menu.Setting;

public class PlayerEntity extends Entity implements ChunkLoader{
	
	public PlayerEntity(float x, float y) {
		super(x, y);
		Property prop = this.getProperties();
		this.pBody.setMass(prop.getMass());
		this.pBody.setFriction(prop.getFriction());
		this.speed = prop.getSpeed();
	}
	
	@Override
	public void update(int tr) {
		super.update(tr);
		
		int ix = pos.floorX();
		int iy = pos.floorY();
		
		boolean left = Setting.MOVE_LEFT.isPressed();
		boolean right = Setting.MOVE_RIGHT.isPressed();
		boolean up = Setting.MOVE_UP.isPressed();
		boolean down = Setting.MOVE_DOWN.isPressed();
		
		int xMove = left ^ right ? (left ? -1 : 1) : 0;
		int yMove = up ^ down ? (up ? -1 : 1) : 0;
		
		if(Game.getWorld().isWater(ix, iy))		
			this.getPhysicsBody().setFriction(1 - pBody.getDefaultFriction());
		else this.getPhysicsBody().setFriction(pBody.getDefaultFriction());
		
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
