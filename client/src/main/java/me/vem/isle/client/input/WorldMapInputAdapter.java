package me.vem.isle.client.input;

import java.awt.event.KeyEvent;

import me.vem.isle.client.graphics.WorldMap;

public class WorldMapInputAdapter extends InputAdapter<WorldMap>{

	protected WorldMapInputAdapter(WorldMap renderer) {
		super(renderer);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()) {
			case KeyEvent.VK_M:
				break;
			default:
				return;
		}
	}
}
