package me.vem.isle.client.graphics;

import java.awt.Dimension;
import java.awt.Graphics;

public abstract class GameRenderer {
	
	private Dimension renderDimension;
	
	protected GameRenderer(Dimension size) {
		this.renderDimension = size;
	}
	
	public abstract void render(Graphics g);
	
	public Dimension getSize() {
		return renderDimension;
	}
}
