package me.isle.game.land;

import me.isle.game.objects.Drawable;

public abstract class Land implements Drawable{
	
	private final int x;
	private final int y;
	
	protected Land(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() { return x; }
	public int getY() { return y; }
}