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
		land[0][0] = new Land(0, 0);
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
