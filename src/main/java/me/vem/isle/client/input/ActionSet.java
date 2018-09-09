package me.vem.isle.client.input;

import me.vem.isle.client.Client;
import me.vem.isle.common.Game;

public class ActionSet{
	
	public static final int MAIN_MENU = 0;
	public static final int GAME = 1;
	public static final int SETTINGS = 2;
	public static final int INGAME_MENU = 3;
	
	public static final Runnable MAINMENU_UP = () -> Client.getInstance().getMainMenu().moveUp();
	public static final Runnable MAINMENU_DOWN = () -> Client.getInstance().getMainMenu().moveDown();
	public static final Runnable MAINMENU_SELECT = () -> Client.getInstance().getMainMenu().select();
	
	public static final Runnable SHUTDOWN = () -> Client.getInstance().shutdown();
	public static final Runnable TOGGLE_DEBUG = () -> Game.toggleDebugMode();
	
	public static void implementActionSet(int set) {
		switch(set) {
		case 0:
			Setting.MOVE_UP.setAction(MAINMENU_UP);
			Setting.MOVE_DOWN.setAction(MAINMENU_DOWN);
			Setting.SELECT.setAction(MAINMENU_SELECT);
			break;
		case 1:
			Setting.MOVE_UP.resetAction();
			Setting.MOVE_DOWN.resetAction();
			Setting.SELECT.resetAction();
			break;
		}
	}
}
