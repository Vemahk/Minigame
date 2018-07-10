package me.vem.isle.game.world;

import static me.vem.isle.Logger.debug;

import me.vem.isle.game.Game;
import me.vem.isle.game.objects.GameObject;

public class ChunkQueue{
	
	private final Chunk from;
	private final Chunk to;
	
	private final GameObject go;
	
	public ChunkQueue(Chunk from, Chunk to, GameObject go) {
		this.from = from;
		this.to = to;
		this.go = go;
	}

	public Chunk getFrom() { return from; }
	public Chunk getTo() { return to; }
	public GameObject getGameObject() { return go; }
	
	public boolean execute() { //ORDAH 66!
		if(from.removeObject(go)) {
			boolean out = to.addObject(go);
			
			if(Game.DEBUG_ACTIVE)
				debug(String.format("Object %s moved from chunk %s to %s.", go, from, to));
			
			return out;
		}
		return false;
	}
}