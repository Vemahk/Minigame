package me.vem.isle;

import static me.vem.isle.Logger.info;

import java.io.IOException;

import javax.swing.JFrame;

import me.vem.isle.game.Game;
import me.vem.isle.game.Input;
import me.vem.isle.graphics.Camera;
import me.vem.isle.menu.ActionSet;
import me.vem.isle.menu.MainMenu;

public class App {
	
	public static final String GAME_TITLE = "Dopey Survival";
	public static final String VERSION = "0.1.13";
	
	private static JFrame window;
	private static MainMenu menu;
	
	public static void createWindow() {
		window = new JFrame(GAME_TITLE);
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

	public static JFrame getWindow() { return window; }
	public static MainMenu getMainMenu() { return menu; }
	
	public static void newGame() {
		window.add(Camera.getInstance());
		
		ClientThread.getInstance().start();
		ServerThread.getInstance().start();
	}
	
	public static void shutdown() {
		getWindow().dispose();
		System.exit(0);
	}
	
	public static void main(String[] args) throws IOException{
		info("Hello World!");
		
		createWindow();
	}
}
