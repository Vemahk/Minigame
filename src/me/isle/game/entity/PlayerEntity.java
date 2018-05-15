package me.isle.game.entity;

import java.awt.image.BufferedImage;

import me.isle.game.Game;
import me.isle.game.land.Water;
import me.isle.graphics.ArrowKeyListener;
import me.isle.graphics.Spritesheet;
import me.isle.resources.ResourceManager;

public class PlayerEntity extends Entity{
	
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
		ArrowKeyListener akl = Game.game.getKeyListener();
		
		int xMove = akl == null ? 0 : akl.movementX();
		int yMove = akl == null ? 0 : akl.movementY();
		
		double speedMod = 1;
		if(x >= 0 && x < 512 && y >= 0 && y < 512)
			if(Game.game.getLandmass().isWater((int)x, (int)y))		
				speedMod /= 3;
		
		if(Game.DEBUG_ACTIVE)
			speedMod *= 10;
		
		double rate = (double)tr;
		this.moveBy(xMove * speed * speedMod / rate, yMove * speed * speedMod / rate);
	}

}
