package me.isle.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import me.isle.Startup;
import me.isle.game.Game;
import me.isle.game.entity.PlayerEntity;
import me.isle.game.land.Grass;
import me.isle.game.land.Land;
import me.isle.game.land.Landmass;
import me.isle.game.land.Water;

public class Map {
	
	private BufferedImage map;
	private double updateRate; //Update rate -- IN SECONDS
	
	public Map(double ur) {
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
		if(++t >= updateRate * Startup.graphicsThread.getFPS()) {
			t = 0;
			
			PlayerEntity player = Game.game.getPlayer();
			int px = (int) Math.round(player.getX());
			int py = (int) Math.round(player.getY());
			
			for(int dx=-10;dx<=10;dx++) {
				int x = px + dx;
				if(x < 0) continue;
				if(x >= 512) break;
				for(int dy=-10;dy<=10;dy++) {
					int y = py + dy;
					if(y < 0) continue;
					if(y >= 512) break;
					
					if(dx * dx + dy * dy >= 100)
						continue;
					
					Landmass lm = Game.game.getLandmass();
					int rgb = 0xFFFF66; // Default: sand
					
					if(lm.isWater(x, y)) rgb = 0x0080FF; //light blue fo water
					else if(lm.isGrass(x, y)) rgb = 0x006600; //darkish green for grass.
					
					map.setRGB(x, y, rgb);
				}
			}
			
			if(showPlayer = !showPlayer) {
				Graphics g = map.getGraphics();
				g.setColor(Color.RED);
				g.fillOval(px-2, py-2, 5, 5);
			}
		}
	}
	
}
