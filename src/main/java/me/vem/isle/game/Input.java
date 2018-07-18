package me.vem.isle.game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import me.vem.isle.Logger;
import me.vem.isle.menu.Setting;

public class Input implements KeyListener, MouseListener{
	
	@Override
	public void keyPressed(KeyEvent e) {
		for(Setting setting : Setting.all)
			if(setting.getKeyCode() == e.getKeyCode()) {
				if(!setting.isType(Setting.TYPE_TOGGLE))
					setting.setOn();
				if(Setting.actionPressed.contains(setting))
				if(setting.isType(Setting.TYPE_ONPRESS))
					setting.run();
			}
		
		//Logger.debug("Key pressed! " + KeyEvent.getKeyText(e.getKeyCode()));
	}

	@Override
	public void keyReleased(KeyEvent e) {
		for(Setting setting : Setting.all)
			if(setting.getKeyCode() == e.getKeyCode()) {
				if(!setting.isType(Setting.TYPE_TOGGLE))
					setting.setOff();
				if(Setting.actionReleased.contains(setting))
					if(setting.isType(Setting.TYPE_TOGGLE))
						setting.toggleState();
					else if(setting.isType(Setting.TYPE_ONRELEASE))
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