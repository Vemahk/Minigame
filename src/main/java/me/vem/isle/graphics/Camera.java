package me.vem.isle.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.TreeSet;

import javax.swing.JPanel;

import me.vem.isle.game.Game;
import me.vem.isle.game.objects.GameObject;
import me.vem.isle.game.physics.BoxCollider;
import me.vem.isle.game.world.Chunk;
import me.vem.isle.game.world.Land;
import me.vem.isle.resources.ResourceManager;

public class Camera extends JPanel{

	public static int SMOOTH_FOLLOW = 1;
	public static int RIGID_FOLLOW = 2;;
	
	private GameObject target;
	private int followType;

	private final int WIDTH;
	private final int HEIGHT;
	
	private final int scale;
	
	private double x;
	private double y;
	
	private Map map;
	
	public Camera(int w, int h, int scale) {
		this.setBackground(Color.BLACK);
		x = y = 0;
		
		this.WIDTH = w;
		this.HEIGHT = h;
		this.scale = scale;
		
		followType = SMOOTH_FOLLOW;
	}
	
	public synchronized void follow(double... param) {
		if(target == null)
			return;
		
		if(followType == RIGID_FOLLOW) {
			setPos(target.getX(), target.getY());
		}else if(followType == SMOOTH_FOLLOW) {
			double damping = .1;
			if(param.length > 0)
				damping = param[0];
			
			double dx = x - target.getX();
			double dy = y - target.getY();
			
			double dist = Math.sqrt(dx * dx + dy * dy);
			double angle = Math.atan2(dy, dx);
			dist -= dist * damping;
			
			double ndx = dist * Math.cos(angle);
			double ndy = dist * Math.sin(angle);
			
			this.setX(target.getX() + ndx);
			this.setY(target.getY() + ndy);
		}
	}

	public void setTarget(GameObject go) {
		setTarget(go, false);
	}
	
	public void setTarget(GameObject go, boolean snapToTarget) {
		if(snapToTarget) 
			setPos(go.getX(), go.getY());
		target = go;
	}
	
	public synchronized void setX(double x) { this.x = x; }
	public synchronized void setY(double y) { this.y = y; }
	
	public synchronized void setPos(double x, double y) {
		setX(x); setY(y);
	}
	
	public void moveX(double dx) { x += dx; }
	public void moveY(double dy) { y += dy; }
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawVisible(g);
	}
	
	public synchronized void drawVisible(Graphics g) {
		if(!Game.initialized)
			return;
		
		if(map == null)
			map = new Map(new Point((int)Game.getPlayer().getX() - 256, (int)Game.getPlayer().getY() - 256), 1.0); //Update the map every 1.0 seconds
		
		//Draw Map
		map.tick();
		
		if(Game.getInput().showMap()) {
			g.drawImage(map.getImage(), 0, 0, WIDTH, HEIGHT, this);
			return;
		}
		
		//Draw Land
		int TIS = ResourceManager.IMAGE_SIZE * scale; //TIS --> True Image Size
		int DR = Math.max(WIDTH, HEIGHT) / TIS / 2; //DR --> Display Radius
		
		int relX = (int)Math.round(x);
		int relY = (int)Math.round(y);
		
		for(int dx = -DR-1;dx<=DR;dx++) {
			for(int dy = -DR-1;dy<=DR;dy++) {
				int drawX = (int) Math.round((relX + dx - x) * TIS + WIDTH/2);
				int drawY = (int) Math.round((relY + dy - y) * TIS + HEIGHT/2);
				Land work = Game.getWorld().getLand(relX + dx, relY + dy);
				
				g.drawImage(work.getImage(), drawX, drawY, TIS, TIS, null);
				
				if(Game.DEBUG_ACTIVE) {
					g.setColor(Color.RED);
					g.drawString((relX + dx) + "|" + (relY + dy), drawX, drawY+10);
				}
			}
		}

		//Draw Objects
		double dDR = DR + .5; //dDR --> double Display Radius
		
		HashSet<Chunk> loaded = Game.getWorld().getLoadedChunks();
		synchronized(loaded) {
			for(Chunk c : loaded) {
				TreeSet<GameObject> objs = c.getObjects();
				synchronized(objs) {
					for(GameObject go : objs) {
						double dx = go.getX() - x;
						double dy = go.getY() - y;
						if(dx >= -dDR && dx <= dDR && dy >= -dDR && dy <= dDR) {
							BufferedImage image = go.getImage();
							int drawX = (int) Math.round(dx * TIS + WIDTH/2) - image.getWidth() / 2 * scale;
							int drawY = (int) Math.round(dy * TIS + HEIGHT/2) - image.getHeight() / 2 * scale;
							
							g.drawImage(image, drawX, drawY, TIS, TIS, null);
							
							if(Game.DEBUG_ACTIVE && go.hasCollider()) {
								BoxCollider coll = (BoxCollider) go.getCollider();
								g.setColor(Color.GREEN);
								
								int w = (int) Math.round(coll.getWidth() * TIS);
								int h = (int) Math.round(coll.getHeight() * TIS);
								g.drawRect(drawX + TIS/2 - w / 2, drawY + TIS/2 - h / 2, w, h);
							}
						}
					}
				}
			}
		}
	} //Draw visible end
	
	public double getCamX() {
		return x;
	}
	
	public double getCamY() {
		return y;
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(WIDTH, HEIGHT);
	}
}