package me.isle.game;

import me.isle.game.objects.Land;

public class Game {

	private final int WIDTH;
	private final int HEIGHT;
	
	private Land[][] land;
	
	public Game() {
		WIDTH = 512;
		HEIGHT = 512;
		
		land = new Land[WIDTH][HEIGHT];
		for(int x=0;x<WIDTH;x++) {
			for(int y=0;y<HEIGHT;y++) {
				if(Math.random() < .5)
					land[x][y] = new Land(x, y);
			}
		}
	}
	
	public int getWidth() {
		return WIDTH;
	}
	
	public int getHeight() {
		return HEIGHT;
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Land getLand(int x, int y) {
		return land[x][y];
	}
}
