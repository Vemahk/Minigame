package me.vem.isle.game.world;

import static me.vem.isle.Logger.debug;

import java.util.LinkedList;
import java.util.Queue;

import me.vem.isle.game.Game;
import me.vem.isle.game.objects.GameObject;

public class ChunkQueue{
	
	private static Queue<ChunkQueue> chunkQueue = new LinkedList<>();
	public static void emptyQueue() {
		while(!chunkQueue.isEmpty())
			chunkQueue.poll().execute();
	}
	
	public static void queue(Chunk from, Chunk to, GameObject go) {
		chunkQueue.add(new ChunkQueue(from, to, go));
	}
	
	private final Chunk from;
	private final Chunk to;
	
	private final GameObject go;
	
	private ChunkQueue(Chunk from, Chunk to, GameObject go) {
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
			
			if(Game.isDebugActive())
				debug(String.format("Object %s moved from chunk %s to %s.", go, from, to));
			
			return out;
		}
		return false;
	}
}