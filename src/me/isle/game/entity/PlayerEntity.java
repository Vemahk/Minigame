package me.isle.game.entity;

import java.awt.image.BufferedImage;

import me.isle.Startup;
import me.isle.game.Game;
import me.isle.graphics.ArrowKeyListener;
import me.isle.resources.ResourceLoader;

public class PlayerEntity extends Entity{
	
	public PlayerEntity(double x, double y) {
		super(x, y);
	}
	
	@Override
	public String getResourcePath() {
		return "player.png";
	}

	@Override
	public BufferedImage getImage() {
		return ResourceLoader.load(getResourcePath());
	}

	@Override
	public void update(int tr) {
		ArrowKeyListener akl = Startup.game.getKeyListener();
		
		int xMove = akl == null ? 0 : akl.movementX();
		int yMove = akl == null ? 0 : akl.movementY();
		
		double speed = 30;
		if(x >= 0 && x < 512 && y >= 0 && y < 512)
			if(Game.game.getLand((int)x, (int)y) == null)		
				speed /= 3;
		double rate = (double)tr;
		this.moveBy(xMove * speed / rate, yMove * speed / rate);
	}

}
