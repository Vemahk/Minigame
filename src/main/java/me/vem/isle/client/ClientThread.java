package me.vem.isle.client;

import javax.swing.JFrame;
import javax.swing.JPanel;

import me.vem.isle.App;
import me.vem.isle.Logger;
import me.vem.isle.client.graphics.Camera;
import me.vem.isle.client.input.ActionSet;
import me.vem.isle.client.input.Input;
import me.vem.isle.client.menu.MainMenu;
import me.vem.isle.client.resources.Animation;
import me.vem.isle.server.game.Game;

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
		Logger.info("Client Thread Started");
		
		Camera cam = Camera.getInstance();
		setWindowContent(cam, ActionSet.GAME);
		
		while(!Game.isInitialized())
			App.sleep(FPS);
		cam.setTarget(Game.getPlayer(), true);
		
		while(true) {
			long start = System.nanoTime();
			
			Animation.tickAll();
			cam.follow(.05f);
			window.repaint();
			
			App.sleep(FPS, System.nanoTime() - start);
		}
	}
	
	public void createWindow() {
		window = new JFrame(App.VERSION.toString());
		window.setUndecorated(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setWindowContent(menu = new MainMenu(), ActionSet.MAIN_MENU);
		
		window.setResizable(false);
		window.setVisible(true);
		
		window.addKeyListener(Input.getInstance());
		window.addMouseWheelListener(Input.getInstance());
		window.requestFocus();
		
		Logger.debug("Window created");
	}
	
	public void setWindowContent(JPanel pane, int actionSet) {
		ActionSet.implementActionSet(actionSet);
		
		window.setContentPane(pane);
		window.pack();
		window.setLocationRelativeTo(null);
		window.repaint();
	}
	
}
