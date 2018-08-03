package me.vem.isle.client;

import static me.vem.isle.Logger.info;

import javax.swing.JFrame;

import me.vem.isle.App;
import me.vem.isle.Logger;
import me.vem.isle.client.graphics.Camera;
import me.vem.isle.client.input.ActionSet;
import me.vem.isle.client.input.Input;
import me.vem.isle.client.menu.MainMenu;
import me.vem.isle.client.resources.Animation;
import me.vem.isle.server.game.Game;
import me.vem.isle.server.game.controller.PlayerController;

public class ClientThread extends Thread{

	public static final int FPS = 60;
	
	private static ClientThread instance;
	public static ClientThread getInstance() {
		if(instance == null)
			instance = new ClientThread();
		return instance;
	}
	
	private JFrame window;
	private MainMenu menu;

	private ClientThread() { createWindow(); }

	public JFrame getWindow() { return window; }
	public MainMenu getMainMenu() { return menu; }
	
	@Override
	public void run() {
		info("Graphics Thread Started...");

		ActionSet.implementActionSet(ActionSet.GAME);
		Camera cam = Camera.getInstance();
		menu.setVisible(false);
		window.add(cam);
		window.pack();
		window.setLocationRelativeTo(null);
		
		while(!Game.isInitialized())
			App.sleep(FPS);
		cam.setTarget(Game.getPlayer(), true);
		
		while(true) {
			
			long start = System.nanoTime();
			
			synchronized(Animation.all) {
				for(Animation anim : Animation.all)
					anim.tick();
			}
	
			cam.follow(.05f);
			window.repaint();
			
			App.sleep(FPS, System.nanoTime() - start);
		}
	}
	
	public void createWindow() {
		window = new JFrame(App.GAME_TITLE);
		window.setUndecorated(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		ActionSet.implementActionSet(ActionSet.MAIN_MENU);
		window.add(menu = new MainMenu());
		
		window.setResizable(false);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		
		window.addKeyListener(Input.getInstance());
		window.addMouseWheelListener(Input.getInstance());
		window.requestFocus();
		
		window.repaint();
		
		if(Game.isDebugActive())
			Logger.debug("JFrame created and loaded.");
	}
}
