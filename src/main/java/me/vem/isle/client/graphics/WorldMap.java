package me.vem.isle.client.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import me.vem.isle.client.Client;
import me.vem.isle.common.Game;
import me.vem.isle.common.objects.GameObject;
import me.vem.isle.common.world.Land;
import me.vem.isle.common.world.World;

public class WorldMap {
	
	private static WorldMap instance;
	public static WorldMap getInstance() { return instance; }
	
	public static void init() {
		if(instance != null)
			return;
		
		Point p = Game.getPlayer().getPos().floor();
		p.translate(-256, -256);
		instance = new WorldMap(p, 1.0); // Update the map every 1.0 seconds
	}
	
	private final Dimension dim = new Dimension(512, 512);
	private Point corner;
	
	private BufferedImage map;
	private double updateRate; //Update rate -- IN SECONDS
	
	public WorldMap(Point corner, double ur) {
		this.corner = corner;
		updateRate = ur;
		map = new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB);
		t = 0;
	}
	
	public BufferedImage getImage() { return map; }
	public int getWidth() { return map.getWidth(); }
	public int getHeight() { return map.getHeight(); }
	
	private int t;
	private boolean showPlayer = false;
	
	public boolean inBounds(int x, int y) {
		return !(x < corner.x || y < corner.y || x >= corner.x + dim.width || y >= corner.y + dim.height);
	}
	
	public void tick() {
		if(++t >= updateRate * Client.FPS){
			t = 0;
			
			GameObject player = Game.getPlayer();
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
					
					Land land = World.getInstance().getLand(x, y);
					int rgb = 0x0080FF; // Default: water
					
					if(land.isSand()) rgb = 0xFFFF66; //sandy color for sand?
					else if(land.isGrass()) rgb = 0x006600; //darkish green for grass.
					
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
	
	public void drawMap(Graphics g) {
		int mw = getWidth(),
			mh = getHeight(),
			x = (this.getWidth() - mw)/2,
			y = (this.getHeight() - mh)/2;

		g.drawImage(map, x, y, Camera.getInstance());
		drawBorder(g, x, y, mw, mh, 2);
	}
	
	public void drawBorder(Graphics g, int x, int y, int w, int h, int t) {
		g.setColor(Color.WHITE);
		
		g.fillRect(x - t, y - t, w + t, t);
		g.fillRect(x - t, y, t, h + t);
		g.fillRect(x + w, y - t, t, h + t);
		g.fillRect(x, y + h, w + t, t);
	}
}
