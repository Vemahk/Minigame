package me.isle.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import me.isle.Startup;
import me.isle.game.Game;
import me.isle.game.land.Land;
import me.isle.game.objects.GameObject;
import me.isle.resources.ResourceManager;

public class Camera {

	public static int SMOOTH_FOLLOW = 1;
	public static int RIGID_FOLLOW = 2;;
	
	private GameObject target;
	private int followType;
	
	private double x;
	private double y;
	
	private Map map;
	
	public Camera() {
		x = Game.game.getPlayer().getX()/2;
		y = Game.game.getPlayer().getY()/2;
		
		followType = SMOOTH_FOLLOW;
		
		setTarget(Game.game.getPlayer());
		
		map = new Map(30);
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
			g.drawImage(map.getImage(), 0, 0, 500, 500, Startup.graphicsThread.getWindow());
			return;
		}
		
		int relX = (int)x;
		int relY = (int)y;
		
		for(int dx = -5;dx<=5;dx++) {
			if(dx + relX < 0) continue;
			if(dx + relX >= Game.game.getWidth()) break;
			for(int dy = -5;dy<=5;dy++) {
				if(dy + relY < 0) continue;
				if(dy + relY >= Game.game.getHeight()) break;
				
				int drawX = (int) Math.round((relX + dx - x) * 50 + 250);
				int drawY = (int) Math.round((relY + dy - y) * 50 + 250);
				Land work = Game.game.getLand(relX + dx, relY + dy);
				
				g.drawImage(work.getImage(), drawX, drawY, null);
				
				if(Game.DEBUG_ACTIVE) {
					g.setColor(Color.RED);
					g.drawString((relX + dx) + "|" + (relY + dy), drawX, drawY+10);
				}
			}
		}
		
		synchronized(GameObject.all) {
			for(GameObject go : GameObject.all) {
				double dx = go.getX() - x;
				double dy = go.getY() - y;
				if(dx >= -5.5 && dx <= 5.5 && dy >= -5.5 && dy <= 5.5) {
					BufferedImage image = go.getImage();
					int drawX = (int) Math.round(dx * 50 + 250) - image.getWidth()/2;
					int drawY = (int) Math.round(dy * 50 + 250) - image.getHeight()/2;
					
					
					g.drawImage(image, drawX, drawY, null);
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
