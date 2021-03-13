package me.vem.isle.client.input;

import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;

import me.vem.isle.client.Client;
import me.vem.isle.client.graphics.Camera;
import me.vem.isle.common.controller.Controller;
import me.vem.isle.common.controller.PlayerController;

public class CameraInputAdapter extends InputAdapter<Camera>{
	
	private final Controller controller;
	
	public CameraInputAdapter(Camera renderer) {
		super(renderer);
		
		this.controller = this.getRenderer().getAnchor().getPlayerController();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		Camera cam = this.getRenderer();
		cam.setScale(cam.getScale() - e.getWheelRotation());
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(this.controller == null)
			return;
		
		switch(e.getKeyCode()) {
		case KeyEvent.VK_UP:
			this.controller.setAction(PlayerController.MOVE_UP, true);
			break;
		case KeyEvent.VK_DOWN:
			this.controller.setAction(PlayerController.MOVE_DOWN, true);
			break;
		case KeyEvent.VK_LEFT:
			this.controller.setAction(PlayerController.MOVE_LEFT, true);
			break;
		case KeyEvent.VK_RIGHT:
			this.controller.setAction(PlayerController.MOVE_RIGHT, true);
			break;
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_UP:
			this.controller.setAction(PlayerController.MOVE_UP, false);
			break;
		case KeyEvent.VK_DOWN:
			this.controller.setAction(PlayerController.MOVE_DOWN, false);
			break;
		case KeyEvent.VK_LEFT:
			this.controller.setAction(PlayerController.MOVE_LEFT, false);
			break;
		case KeyEvent.VK_RIGHT:
			this.controller.setAction(PlayerController.MOVE_RIGHT, false);
			break;
		case KeyEvent.VK_ESCAPE:
			Client.getInstance().shutdown();
			break;
		case KeyEvent.VK_F3:
			this.getRenderer().toggleDebugMode();
			break;
		}
	}
}
