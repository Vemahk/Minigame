package me.isle.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import me.isle.Startup;
import me.isle.game.Game;
import me.isle.game.land.Land;
import me.isle.game.objects.GameObject;
import me.isle.game.physics.BoxCollider;
import me.isle.resources.ResourceManager;

public class Camera {

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
		x = Game.game.getPlayer().getX();
		y = Game.game.getPlayer().getY();
		
		this.WIDTH = w;
		this.HEIGHT = h;
		this.scale = scale;
		
		followType = SMOOTH_FOLLOW;
		
		setTarget(Game.game.getPlayer());
		
		map = new Map(1.0); //Update the map every 1.0 seconds
	}
	
	public synchronized void follow(double... param) {
		if(target == null)
			return;
		
		if(followType == RIGID_FOLLOW) {
			setX(target.getX());
			setY(target.getY());
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

	public void setTarget(GameObject go) { target = go; }
	public synchronized void setX(double x) { this.x = x; }
	public synchronized void setY(double y) { this.y = y; }
	
	public void moveX(double dx) { x += dx; }
	public void moveY(double dy) { y += dy; }
	
	public synchronized void drawVisible(Graphics g) {
		
		map.tick();
		
		if(Game.game.getKeyListener().showMap()) {
			g.drawImage(map.getImage(), 0, 0, WIDTH, HEIGHT, Startup.graphicsThread.getWindow());
			return;
		}
		
		int TIS = ResourceManager.IMAGE_SIZE * scale; //TIS --> True Image Size
		int DR = Math.max(WIDTH, HEIGHT) / TIS / 2; //DR --> Display Radius
		
		int relX = (int)x;
		int relY = (int)y;
		
		for(int dx = -DR;dx<=DR;dx++) {
			if(dx + relX < 0) continue;
			if(dx + relX >= Game.game.getWidth()) break;
			for(int dy = -DR;dy<=DR;dy++) {
				if(dy + relY < 0) continue;
				if(dy + relY >= Game.game.getHeight()) break;
				
				int drawX = (int) Math.round((relX + dx - x) * TIS + WIDTH/2);
				int drawY = (int) Math.round((relY + dy - y) * TIS + HEIGHT/2);
				Land work = Game.game.getLandmass().getLand(relX + dx, relY + dy);
				
				g.drawImage(work.getImage(), drawX, drawY, TIS, TIS, null);
				
				if(Game.DEBUG_ACTIVE) {
					g.setColor(Color.RED);
					g.drawString((relX + dx) + "|" + (relY + dy), drawX, drawY+10);
				}
			}
		}
		
		double dDR = DR + .5; //dDR --> double Display Radius
		synchronized(GameObject.all) {
			for(GameObject go : GameObject.all) {
				double dx = go.getX() - x;
				double dy = go.getY() - y;
				if(dx >= -dDR && dx <= dDR && dy >= -dDR && dy <= dDR) {
					BufferedImage image = go.getImage();
					int drawX = (int) Math.round(dx * TIS + WIDTH/2) - image.getWidth() / 2 * scale;
					int drawY = (int) Math.round(dy * TIS + HEIGHT/2) - image.getHeight() / 2 * scale;
					
					g.drawImage(image, drawX, drawY, TIS, TIS, null);
					
					if(Game.DEBUG_ACTIVE && go.hasCollider()) {
						BoxCollider c = (BoxCollider) go.getCollider();
						g.setColor(Color.GREEN);
						
						int w = (int) Math.round(c.getWidth() * TIS);
						int h = (int) Math.round(c.getHeight() * TIS);
						g.drawRect(drawX + TIS/2 - w / 2, drawY + TIS/2 - h / 2, w, h);
					}
				}
			}
		}
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
}