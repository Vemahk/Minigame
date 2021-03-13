package me.vem.isle.client.input;

import java.awt.event.KeyEvent;

import me.vem.isle.client.menu.MainMenu;
import me.vem.utils.logging.Logger;

public class MainMenuInputAdapter extends InputAdapter<MainMenu> {

	public MainMenuInputAdapter(MainMenu renderer) {
		super(renderer);
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		Logger.debugf(1, "Key Released: %d", e.getKeyCode());
		
		switch(e.getKeyCode()) {
		case KeyEvent.VK_UP:
			this.getRenderer().moveUp();
			break;
		case KeyEvent.VK_DOWN:
			this.getRenderer().moveDown();
			break;
		case KeyEvent.VK_ENTER:
			this.getRenderer().select();
			break;
		}
	}
}
