package me.vem.isle.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import me.vem.isle.App;
import me.vem.isle.game.Game;
import me.vem.isle.game.entity.PlayerEntity;
import me.vem.isle.game.world.World;

public class Map {
	
	private final Dimension dim = new Dimension(512, 512);
	private Point corner;
	
	private BufferedImage map;
	private double updateRate; //Update rate -- IN SECONDS
	
	public Map(Point corner, double ur) {
		this.corner = corner;
		updateRate = ur;
		map = new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB);
		t = 0;
	}
	
	public BufferedImage getImage() {
		return map;
	}
	
	private int t;
	private boolean showPlayer = false;
	
	public boolean inBounds(int x, int y) {
		return !(x < corner.x || y < corner.y || x >= corner.x + dim.width || y >= corner.y + dim.height);
	}
	
	public void tick() {
		if(++t >= updateRate * App.graphicsThread.getFPS()) {
			t = 0;
			
			PlayerEntity player = Game.getPlayer();
			int px = (int) Math.round(player.getX());
			int py = (int) Math.round(player.getY());
			
			if(!inBounds(px, py))
				return;
			
			for(int dx=-10;dx<=10;dx++) {
				int x = px + dx;
				for(int dy=-10;dy<=10;dy++) {
					int y = py + dy;
					
					if(dx * dx + dy * dy >= 100)
						continue;
					
					World lm = Game.getWorld();
					int rgb = 0xFFFF66; // Default: sand
					
					if(lm.isWater(x, y)) rgb = 0x0080FF; //light blue fo water
					else if(lm.isGrass(x, y)) rgb = 0x006600; //darkish green for grass.
					
					map.setRGB(x - corner.x, y - corner.y, rgb);
				}
			}
			
			if(showPlayer = !showPlayer) {
				Graphics g = map.getGraphics();
				g.setColor(Color.RED);
				g.fillOval(px-2 - corner.x, py-2 - corner.y, 5, 5);
			}
		}
	}
	
}
