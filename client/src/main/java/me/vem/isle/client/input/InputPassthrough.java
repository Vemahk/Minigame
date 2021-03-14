package me.vem.isle.client.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class InputPassthrough implements KeyListener, MouseListener, MouseWheelListener, MouseMotionListener{

	private KeyListener keyListener;
	private MouseListener mouseListener;
	private MouseWheelListener mouseWheelListener;
	private MouseMotionListener mouseMotionListener;
	
	public InputPassthrough() {}
	
	public InputPassthrough reset() {
		setInputAdapter(null);
		return this;
	}
	
	public InputPassthrough setInputAdapter(@SuppressWarnings("rawtypes") InputAdapter inputAdapter) {
		setKeyListener(inputAdapter);
		setMouseListener(inputAdapter);
		setMouseWheelListener(inputAdapter);
		setMouseMotionListener(inputAdapter);
		return this;
	}
	
	public InputPassthrough setKeyListener(KeyListener keyListener) {
		this.keyListener = keyListener;
		return this;
	}
	
	public InputPassthrough setMouseListener(MouseListener mouseListener) {
		this.mouseListener = mouseListener;
		return this;
	}
	
	public InputPassthrough setMouseWheelListener(MouseWheelListener mouseWheelListener) {
		this.mouseWheelListener = mouseWheelListener;
		return this;
	}
	
	public InputPassthrough setMouseMotionListener(MouseMotionListener mouseMotionListener) {
		this.mouseMotionListener = mouseMotionListener;
		return this;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if(mouseMotionListener == null)
			return;
		
		mouseMotionListener.mouseDragged(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(mouseMotionListener == null)
			return;
		
		mouseMotionListener.mouseMoved(e);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(mouseWheelListener == null)
			return;
		
		mouseWheelListener.mouseWheelMoved(e);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(mouseListener == null)
			return;
		
		mouseListener.mouseClicked(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(mouseListener == null)
			return;
		
		mouseListener.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(mouseListener == null)
			return;
		
		mouseListener.mouseReleased(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if(mouseListener == null)
			return;
		
		mouseListener.mouseEntered(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if(mouseListener == null)
			return;
		
		mouseListener.mouseExited(e);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if(keyListener == null)
			return;
		
		keyListener.keyTyped(e);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(keyListener == null)
			return;
		
		keyListener.keyPressed(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(keyListener == null)
			return;
		
		keyListener.keyReleased(e);
	}
}