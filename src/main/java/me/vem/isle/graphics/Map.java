package me.vem.isle.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import me.vem.isle.App;
import me.vem.isle.game.Game;
import me.vem.isle.game.entity.PlayerEntity;
import me.vem.isle.game.world.Grass;
import me.vem.isle.game.world.Land;
import me.vem.isle.game.world.Water;
import me.vem.isle.game.world.World;

public class Map {
	
	private BufferedImage map;
	private double updateRate; //Update rate -- IN SECONDS
	
	public Map(double ur) {
		updateRate = ur;
		map = new BufferedImage(Game.getWidth(), Game.getHeight(), BufferedImage.TYPE_INT_RGB);
		t = 0;
	}
	
	public BufferedImage getImage() {
		return map;
	}
	
	private int t;
	private boolean showPlayer = false;
	
	public void tick() {
		if(++t >= updateRate * App.graphicsThread.getFPS()) {
			t = 0;
			
			PlayerEntity player = Game.getPlayer();
			int px = (int) Math.round(player.getX());
			int py = (int) Math.round(player.getY());
			
			for(int dx=-10;dx<=10;dx++) {
				int x = px + dx;
				if(x < 0) continue;
				if(x >= Game.getWidth()) break;
				for(int dy=-10;dy<=10;dy++) {
					int y = py + dy;
					if(y < 0) continue;
					if(y >= Game.getHeight()) break;
					
					if(dx * dx + dy * dy >= 100)
						continue;
					
					World lm = Game.getWorld();
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
