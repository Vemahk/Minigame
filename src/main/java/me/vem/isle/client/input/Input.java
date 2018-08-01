package me.vem.isle.client.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import me.vem.isle.Logger;
import me.vem.isle.client.graphics.Camera;

public class Input implements KeyListener, MouseListener, MouseWheelListener{
	
	private static Input instance;
	public static Input getInstance() {
		if(instance == null)
			instance = new Input();
		return instance;
	}
	
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

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		Camera cam = Camera.getInstance();
		int rot = e.getWheelRotation();

		
		cam.setScale(cam.getScale() - rot);
	}
	
	@Override public void keyTyped(KeyEvent e) {}
	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
}