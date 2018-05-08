package me.isle.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import me.isle.game.Game;
import me.isle.game.entity.PlayerEntity;
import me.isle.game.land.Grass;
import me.isle.game.land.Land;
import me.isle.game.land.Water;

public class Map {
	
	private BufferedImage map;
	private int updateRate;
	
	public Map(int ur) {
		updateRate = ur;
		map = new BufferedImage(Game.game.getWidth(), Game.game.getHeight(), BufferedImage.TYPE_INT_RGB);
		t = 0;
	}
	
	public BufferedImage getImage() {
		return map;
	}
	
	private int t;
	private boolean showPlayer = false;
	
	public void tick() {
		if(++t == updateRate) {
			t = 0;
			
			for(int x=0;x<map.getWidth();x++) {
				for(int y=0;y<map.getHeight();y++) {
					Land land = Game.game.getLand(x, y);
					int rgb = 0xFFFF66;
					
					if(land instanceof Water) rgb = 0x0080FF;
					else if(land instanceof Grass) rgb = 0x006600;
					
					map.setRGB(x, y, rgb);
				}
			}
			
			PlayerEntity player = Game.game.getPlayer();
			int drX = (int)player.getX()-2;
			int drY = (int)player.getY()-2;
			
			if(showPlayer = !showPlayer) {
				Graphics g = map.getGraphics();
				g.setColor(Color.RED);
				g.fillOval(drX, drY, 5, 5);
			}
		}
	}
	
}
