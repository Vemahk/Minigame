package me.vem.isle.client.graphics;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

public final class BufferedCanvas extends Canvas{
	
	private static final long serialVersionUID = 2293864682604141349L;
	
	private final Frame parent;
	
	private BufferStrategy strat;
	private GameRenderer renderer;
	
	public BufferedCanvas(Frame parent) {
		super();
		
		this.parent = parent;
		setBackground(Color.BLACK);
		
		parent.add(this);
		parent.pack();
		parent.setLocationRelativeTo(null);

		createBufferStrategy(2);
		strat = getBufferStrategy();
	}
	
	/**
	 * @return the time in nanoseconds(ish) it took to render.
	 */
	public synchronized long render() {
		long start = System.nanoTime();
		do {
			do {
				Graphics graphics = strat.getDrawGraphics();
				graphics.clearRect(0, 0, getSize().width, getSize().height);
				
				/* Render Step */
				renderer.render(graphics);
				
				graphics.dispose();
			}while(strat.contentsRestored());
			strat.show();
		}while(strat.contentsLost());

		return System.nanoTime() - start; //dt
	}
	
	public synchronized BufferedCanvas setRenderer(GameRenderer renderer) {
		this.renderer = renderer;
		
		setSize(renderer.getSize());
		parent.pack();
		parent.setLocationRelativeTo(null);
		
		return this;
	}
}
