package me.vem.isle.common.physics.collider;

import me.vem.utils.logging.Logger;

public enum ColliderType {
	BOX;
	
	public static ColliderType getType(String type) {
		if("box".equalsIgnoreCase(type))
			return BOX;
		return null;
	}
	
	public Collider generateDefaultCollider(float... args) {
		if(this == BOX) {
			if(args.length < 2) {
				Logger.error("Default Collider generation failed. Type: BOX, Reason: Not enough args.");
				return null;
			}
			
			return new BoxCollider(null, args[0], args[1]);
		}
		
		return null;
	}
}
