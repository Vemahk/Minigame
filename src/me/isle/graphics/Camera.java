package me.isle.graphics;

import java.awt.Graphics;

import me.isle.Startup;
import me.isle.game.objects.Land;
import me.isle.resources.ResourceLoader;

public class Camera {

	private double x;
	private double y;
	
	public Camera() {
		x = Startup.game.getWidth()/2.0;
		y = Startup.game.getHeight()/2.0;
	}
	
	public void moveX(double dx) {
		x += dx;
	}
	
	public void moveY(double dy) {
		y += dy;
	}
	
	public void drawVisible(Graphics g) {
		int relX = (int)x;
		int relY = (int)y;
		
		for(int dx = -5;dx<=5;dx++) {
			for(int dy = -5;dy<=5;dy++) {

				int drawX = (int) ((relX + dx) * 50 - (x * 50) + 250);
				int drawY = (int) ((relY + dy) * 50 - (y * 50) + 250);
				Land work = Startup.game.getLand(relX + dx, relY + dy);
				
				if(work!=null)
					g.drawImage(work.getImage(), drawX, drawY, null);
				else g.drawImage(ResourceLoader.load("water.png"), drawX, drawY, null);
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
