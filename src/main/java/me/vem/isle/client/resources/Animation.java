package me.vem.isle.client.resources;

import java.util.HashSet;

import me.vem.utils.logging.Logger;

public class Animation {

	private static HashSet<Animation> all = new HashSet<>();
	public static void tickAll() {
		synchronized(all) {
			for(Animation anim : all)
				anim.tick();
		}
	}
	
	public static Animation getAnimation(String str) {
		return null;//TODO Implement getAnimation();
	}
	
	private Sprite[] sprites;
	private int frameDelay;
	
	private int cur;
	
	public Animation(int delay, Sprite... sprites) {
		
		if(sprites.length <= 1) { //U don guufed.
			Logger.info("Cannot create animation of one or no image order.");
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
