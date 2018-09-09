package me.vem.isle.client;

import javax.swing.JFrame;
import javax.swing.JPanel;

import me.vem.isle.client.graphics.Camera;
import me.vem.isle.client.graphics.WorldMap;
import me.vem.isle.client.input.ActionSet;
import me.vem.isle.client.input.Input;
import me.vem.isle.client.menu.MainMenu;
import me.vem.isle.server.Server;
import me.vem.utils.Utilities;
import me.vem.utils.logging.Logger;
import me.vem.utils.logging.Version;

public class Client extends Thread{

	public static final Version VERSION = new Version(0, 1, 23, "Dopey Survival");
	
	public static final int FPS = 60;
	
	private static Client instance;
	public static Client getInstance() {
		if(instance == null)
			instance = new Client();
		return instance;
	}
	
	
	private Server localServer;
	public boolean isLocalServer() { return localServer != null; }
	
	public void setLocalServer(Server s) {
		this.localServer = s;
	}
	
	private JFrame window;
	private MainMenu menu;

	private Client() {
		super("Dopey Survival Client Thread");
		createWindow();
	}

	public JFrame getWindow() { return window; }
	public MainMenu getMainMenu() { return menu; }
	
	@Override
	public void run() {
		Logger.info("Client Started");
		
		if(isLocalServer())
			localServer.start();
		
		Camera.init();
		
		while(!isKilled) {
			long start = System.nanoTime();
			
			WorldMap.getInstance().tick();
			window.repaint();
			
			Utilities.sleep(FPS, System.nanoTime() - start);
		}
	}
	
	public void createWindow() {
		window = new JFrame(VERSION.toString());
		window.setUndecorated(true);
		
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

	private boolean isKilled;
	
	public void shutdown() {
		if(isLocalServer())
			localServer.shutdown();
		
		Client.getInstance().getWindow().dispose();
		isKilled = true;
	}
	
	public static void main(String... args) {
		Logger.infof("Loading Client of %s...", VERSION);
		
		getInstance();
	}
	
}
