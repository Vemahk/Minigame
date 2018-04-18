package me.isle.graphics;

import java.awt.Color;
import java.awt.Graphics;

import me.isle.Startup;
import me.isle.game.Game;
import me.isle.game.objects.GameObject;
import me.isle.game.objects.Land;
import me.isle.resources.ResourceLoader;

public class Camera {

	public static int SMOOTH_FOLLOW = 1;
	public static int RIGID_FOLLOW = 2;;
	
	private GameObject target;
	private int followType;
	
	private double x;
	private double y;
	
	public Camera() {
		x = Startup.game.getWidth()/2.0;
		y = Startup.game.getHeight()/2.0;
		
		followType = 1;
		
		setTarget(Startup.game.getPlayer());
	}
	
	public void follow(double... param) {
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
	public void setX(double x) { this.x = x; }
	public void setY(double y) { this.y = y; }
	
	public void moveX(double dx) { x += dx; }
	public void moveY(double dy) { y += dy; }
	
	public void drawVisible(Graphics g) {
		int relX = (int)x;
		int relY = (int)y;
		
		for(int dx = -5;dx<=5;dx++) {
			if(dx + relX < 0) continue;
			if(dx + relX >= Startup.game.getWidth()) break;
			for(int dy = -5;dy<=5;dy++) {
				if(dy + relY < 0) continue;
				if(dy + relY >= Startup.game.getHeight()) break;
				
				int drawX = (int) Math.round((relX + dx - x) * 50 + 250);
				int drawY = (int) Math.round((relY + dy - y) * 50 + 250);
				Land work = Startup.game.getLand(relX + dx, relY + dy);
				
				if(work!=null)
					g.drawImage(work.getImage(), drawX, drawY, null);
				else g.drawImage(ResourceLoader.load("water.png"), drawX, drawY, null);
				
				
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
				if(dx >= -6 && dx <= 5 && dy >= -6 && dx <= 5) {
					int drawX = (int) Math.round(dx * 50 + 250);
					int drawY = (int) Math.round(dy * 50 + 250);
					
					g.drawImage(go.getImage(), drawX, drawY, null);
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
