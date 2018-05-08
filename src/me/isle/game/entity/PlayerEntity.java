package me.isle.game.entity;

import java.awt.image.BufferedImage;

import me.isle.game.Game;
import me.isle.game.land.Water;
import me.isle.graphics.ArrowKeyListener;
import me.isle.resources.ResourceLoader;

public class PlayerEntity extends Entity{
	
	private double speed;
	
	public PlayerEntity(double x, double y) {
		super(x, y);
		speed = 3;
	}
	
	@Override
	public String getResourcePath() {
		return "player.png";
	}

	@Override
	public BufferedImage getImage() {
		return ResourceLoader.get(getResourcePath());
	}

	@Override
	public void update(int tr) {
		ArrowKeyListener akl = Game.game.getKeyListener();
		
		int xMove = akl == null ? 0 : akl.movementX();
		int yMove = akl == null ? 0 : akl.movementY();
		
		double speedMod = 1;
		if(x >= 0 && x < 512 && y >= 0 && y < 512)
			if(Game.game.getLand((int)x, (int)y) instanceof Water)		
				speedMod /= 3;
		
		if(Game.game.DEBUG_ACTIVE)
			speedMod *= 10;
		
		double rate = (double)tr;
		this.moveBy(xMove * speed * speedMod / rate, yMove * speed * speedMod / rate);
	}

}
