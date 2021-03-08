package me.vem.isle.client;

import java.awt.event.WindowEvent;

import me.vem.isle.client.graphics.GameFrame;
import me.vem.isle.client.graphics.RenderThread;
import me.vem.isle.client.input.InputAdapter;
import me.vem.isle.client.input.MainMenuInputAdapter;
import me.vem.isle.client.menu.MainMenu;
import me.vem.isle.common.world.WorldThread;
import me.vem.utils.logging.Logger;
import me.vem.utils.logging.Version;

public class Client extends GameFrame{
	
	private static final long serialVersionUID = -8743198429325787950L;
	public static final Version VERSION = new Version(0, 2, 0, "Dopey Survival");
	
	private static Client instance;
	public static Client getInstance() {
		if(instance == null)
			instance = new Client();
		return instance;
	}

	private Client() {
		super();
		
		MainMenu menu = new MainMenu();
		InputAdapter<MainMenu> input = new MainMenuInputAdapter(menu);
		this.setContext(menu, input);
		
		render();
	}
	
	public void shutdown() {
		WorldThread.end();
		RenderThread.end();
		
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	public static void main(String... args) {
		Logger.infof("Loading Client of %s...", VERSION);

		getInstance();
	}
}
