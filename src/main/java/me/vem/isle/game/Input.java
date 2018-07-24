package me.vem.isle.game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import me.vem.isle.menu.Setting;

public class Input implements KeyListener, MouseListener{
	
	@Override
	public void keyPressed(KeyEvent e) {
		for(Setting setting : Setting.all)
			if(setting.getKeyCode() == e.getKeyCode())
				setting.setPressed();
		
		//Logger.debug("Key pressed! " + KeyEvent.getKeyText(e.getKeyCode()));
	}

	@Override
	public void keyReleased(KeyEvent e) {
		for(Setting setting : Setting.all)
			if(setting.getKeyCode() == e.getKeyCode()) {
				setting.setReleased();
				setting.toggle();
				setting.run();
			}
		
		//System.out.printf("Key %d released.%n", key);
	}

	@Override public void keyTyped(KeyEvent e) {}
	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
}