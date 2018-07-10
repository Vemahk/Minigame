package me.vem.isle.game.event;

public class CollisionListener implements EventListener{

	@Override
	public void handleEvent(Event e) {
		CollisionEvent ce = (CollisionEvent) e;
		
		
	}

	@Override
	public boolean listeningFor(Event e) {
		return e instanceof CollisionEvent;
	}
}