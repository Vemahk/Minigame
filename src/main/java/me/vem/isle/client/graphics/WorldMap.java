package me.vem.isle.client.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import me.vem.isle.common.objects.GameObject;
import me.vem.isle.common.world.Land;
import me.vem.isle.common.world.World;

public class WorldMap extends GameRenderer{
	
	private static WorldMap instance;
	public static WorldMap getInstance() { return instance; }
	
	public static void init(World world) {
		if(instance != null)
			return;
		
		instance = new WorldMap(world, world.requestPlayer()); // Update the map every 1.0 seconds
	}
	
	private final Dimension mapDimension = new Dimension(512, 512);
	private final World world;
	private final GameObject focus;
	private Point corner;
	
	private BufferedImage map;
	
	public WorldMap(World world, GameObject focus) {
		this(Toolkit.getDefaultToolkit().getScreenSize(), world, focus);
	}
	
	public WorldMap(Dimension size, World world, GameObject focus) {
		super(size);
		this.world = world;

		Point p = focus.getPos().floor();
		p.translate(-256, -256);
		
		this.corner = p;
		this.focus = focus;
		
		map = new BufferedImage(mapDimension.width, mapDimension.height, BufferedImage.TYPE_INT_RGB);
	}
	
	public BufferedImage getImage() { return map; }
	public int getWidth() { return map.getWidth(); }
	public int getHeight() { return map.getHeight(); }
	
	private boolean showPlayer = false;
	
	public boolean inBounds(int x, int y) {
		return !(x < corner.x || y < corner.y || x >= corner.x + mapDimension.width || y >= corner.y + mapDimension.height);
	}
	
	public void tick() {
		GameObject focus = this.focus;
		int px = (int) Math.round(focus.getX());
		int py = (int) Math.round(focus.getY());
		
		if(!inBounds(px, py))
			return;
		
		for(int dx=-10;dx<=10;dx++) {
			int x = px + dx;
			for(int dy=-10;dy<=10;dy++) {
				int y = py + dy;
				
				if(dx * dx + dy * dy >= 100)
					continue;
				
				Land land = world.getLand(x, y);
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
	
	public void render(Graphics g) {
		int mw = mapDimension.width,
			mh = mapDimension.height,
			x = (this.getSize().width - mw)/2,
			y = (this.getSize().height - mh)/2;

		g.drawImage(map, x, y, null);
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
