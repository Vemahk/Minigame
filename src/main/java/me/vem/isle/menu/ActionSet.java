package me.vem.isle.menu;

import me.vem.isle.App;

public class ActionSet{
	
	public static final int MAIN_MENU = 0;
	public static final int GAME = 1;
	public static final int SETTINGS = 2;
	public static final int INGAME_MENU = 3;
	
	public static final Runnable MAINMENU_UP = () -> App.getMainMenu().moveUp();
	public static final Runnable MAINMENU_DOWN = () -> App.getMainMenu().moveDown();
	public static final Runnable MAINMENU_SELECT = () -> App.getMainMenu().select();
	
	public static final Runnable SHUTDOWN = () -> App.shutdown();
	
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
