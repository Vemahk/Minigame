package me.vem.isle;

import static me.vem.isle.Logger.info;

import javax.swing.JFrame;

import me.vem.isle.game.Game;
import me.vem.isle.game.Input;
import me.vem.isle.game.entity.Player;
import me.vem.isle.graphics.Animation;
import me.vem.isle.graphics.Camera;
import me.vem.isle.menu.ActionSet;
import me.vem.isle.menu.MainMenu;

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
		
		while(true) {
			
			long start = System.currentTimeMillis();
			
			if(!cam.hasTarget() && Player.getInstance() != null)
				cam.setTarget(Player.getInstance(), true);
			
			synchronized(Animation.all) {
				for(Animation anim : Animation.all)
					anim.tick();
			}
	
			cam.follow(.05f);
			getWindow().repaint();
			
			long deltaTime = System.currentTimeMillis() - start;
			long frameDelay = 1000/FPS - deltaTime; //Graphics Thread Sleep
			if(frameDelay <= 0) continue;
			
			try {
				Thread.sleep(frameDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
		
		window.addKeyListener(new Input());
		window.requestFocus();
		
		window.repaint();
		
		if(Game.isDebugActive())
			Logger.debug("JFrame created and loaded.");
	}
}
