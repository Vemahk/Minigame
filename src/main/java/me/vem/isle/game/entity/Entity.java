package me.vem.isle.game.entity;

import me.vem.isle.game.objects.GameObject;
import me.vem.isle.game.physics.PhysicsBody;
import me.vem.isle.game.world.Chunk;

public abstract class Entity extends GameObject{

	protected PhysicsBody pBody;
	protected double moveSpeed;
	
	public Entity(float x, float y) {
		super(x, y);
		moveSpeed = 10.0;
		pBody = new PhysicsBody(this);
	}
	
	public PhysicsBody getPhysicsBody() {
		return pBody;
	}
	
	public boolean hasPhysicsBody() { return pBody != null; }
	
	@Override
	public void update(int tr) {
		if(pBody != null)
			pBody.update(tr);
		
		Chunk nChunk = getPresumedChunk();
		if(chunk != nChunk)
			chunk.queueTransfer(nChunk, this);
	}
}
