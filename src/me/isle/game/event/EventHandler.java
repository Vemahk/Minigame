package me.isle.game.event;

import java.util.HashSet;

public class EventHandler {

	private HashSet<EventListener> listeners;
	
	public EventHandler() {
		listeners = new HashSet<>();
	}
	
	public void registerListener(EventListener el) {
		listeners.add(el);
	}
	
	public void eventThrown(Event e) {
		for(EventListener el : listeners)
			if(el.listeningFor(e))
				el.handleEvent(e);
	}
}
