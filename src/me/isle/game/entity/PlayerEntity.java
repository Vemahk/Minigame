package me.isle.game.entity;

import java.awt.image.BufferedImage;

import me.isle.game.ArrowKeyListener;
import me.isle.game.Game;
import me.isle.game.objects.ChunkLoader;
import me.isle.game.physics.Vector;
import me.isle.game.world.World;
import me.isle.graphics.Spritesheet;
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
		
		ArrowKeyListener akl = Game.game.getKeyListener();
		
		int xMove = akl == null ? 0 : akl.movementX();
		int yMove = akl == null ? 0 : akl.movementY();
		
		double speedMod = 1;
		if(x >= 0 && x < Game.game.getWidth() && y >= 0 && y < Game.game.getHeight())
			if(Game.game.getWorld().isWater((int)x, (int)y))		
				this.pBody.setFrictionMod(3);
			else this.pBody.setFrictionMod(1);
		
		if(Game.DEBUG_ACTIVE)
			speedMod *= 10;
		
		double Fx = xMove * speed * speedMod;
		double Fy = yMove * speed * speedMod;
		this.pBody.applyForce(new Vector(Fx, Fy));
	}
	
	public int chunkRadius() { return 1; }
}
