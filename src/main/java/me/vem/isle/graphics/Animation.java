package me.vem.isle.graphics;

import static me.vem.isle.Logger.info;

import java.awt.image.BufferedImage;
import java.util.HashSet;

public class Animation {

	public static HashSet<Animation> all = new HashSet<>();
	
	private Spritesheet sprites;
	private int[] imageOrder;
	private int frameDelay;
	
	private int nextImageIndex;
	private int curImage;
	
	public Animation(Spritesheet s, int delay, int... order) {
		
		if(order.length <= 1) { //U don guufed.
			info("Cannot create animation of one or no image order.");
			return;
		}
		
		sprites = s;
		imageOrder = order;
		frameDelay = delay;
		
		curImage = order[0];
		nextImageIndex = 1;
		
		synchronized(all) {
			all.add(this);
		}
	}
	
	private int tick = 0;
	public void tick() {
		if(++tick >= frameDelay) {
			tick = 0;
			curImage = imageOrder[nextImageIndex++];
			if(nextImageIndex == imageOrder.length)
				nextImageIndex = 0;
		}
	}
	
	public BufferedImage getCurImage() {
		return sprites.getImage(curImage);
	}
	
}
