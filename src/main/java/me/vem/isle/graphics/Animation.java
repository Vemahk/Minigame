package me.vem.isle.graphics;

import static me.vem.isle.Logger.info;

import java.awt.image.BufferedImage;
import java.util.HashSet;

import me.vem.isle.resources.Sprite;

public class Animation {

	public static HashSet<Animation> all = new HashSet<>();

	public static Animation getAnimation(String str) {
		return null;//TODO Implement this
	}
	
	private Sprite[] sprites;
	private int frameDelay;
	
	private int cur;
	
	public Animation(int delay, Sprite... sprites) {
		
		if(sprites.length <= 1) { //U don guufed.
			info("Cannot create animation of one or no image order.");
			return;
		}
		
		this.sprites = sprites;
		frameDelay = delay;
		
		cur = 0;
		
		synchronized(all) {
			all.add(this);
		}
	}
	
	private int tick = 0;
	public void tick() {
		if(++tick >= frameDelay) {
			tick = 0;
			if(++cur >= sprites.length)
				cur = 0;
		}
	}
	
	public Sprite getCurrent() {
		return sprites[cur];
	}
}
