package me.vem.isle.client.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import me.vem.isle.client.graphics.GameRenderer;

public abstract class InputAdapter<T extends GameRenderer> implements KeyListener, MouseListener, MouseWheelListener, MouseMotionListener{
	private final T renderer;
	
	protected InputAdapter(T renderer) {
		this.renderer = renderer;
	}
	
	protected T getRenderer() {
		return this.renderer;
	}
	
	@Override public void mouseWheelMoved(MouseWheelEvent e) {}
	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void mouseDragged(MouseEvent e) {}
	@Override public void mouseMoved(MouseEvent e) {}
	@Override public void keyTyped(KeyEvent e) {}
	@Override public void keyPressed(KeyEvent e) {}
	@Override public void keyReleased(KeyEvent e) {}
}
