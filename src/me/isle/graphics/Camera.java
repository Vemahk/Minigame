package me.isle.graphics;

import java.awt.Graphics;

import me.isle.Startup;
import me.isle.game.objects.Land;

public class Camera {

	private double x;
	private double y;
	
	public Camera() {
		x = Startup.game.getWidth()/2.0;
		y = Startup.game.getHeight()/2.0;
		
		
	}
	
	public void drawVisible(Graphics g) {
		int relX = (int)x;
		int relY = (int)y;
		
		Land[][] tmp = new Land[11][11];
		
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
}
