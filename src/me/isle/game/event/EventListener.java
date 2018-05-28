package me.isle.game.event;

public interface EventListener {

	public void handleEvent(Event e);
	public boolean listeningFor(Event e);
	
}
