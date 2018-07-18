package me.vem.isle.game.objects;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import me.vem.isle.Logger;
import me.vem.isle.game.physics.Vector;
import me.vem.isle.graphics.Animation;
import me.vem.isle.resources.ResourceManager;

public class Property {
	
	public static void register(Class<? extends GameObject> cls, String filename) throws DocumentException {
		if(GameObject.properties.containsKey(cls)) {
			Logger.error("GameObject being registered twice: "+ cls.getName());
			return;
		}
		
		SAXReader reader = new SAXReader();
		Document doc = reader.read(ResourceManager.getResource("properties", filename));
		
		
	}
	
	private float z;
	
	private boolean chunkLoader;
	private int loadRadius;
	
	private boolean hasCollider;
	private Vector colliderSize;
	
	private boolean hasSprite;
	private String spritesheetName;
	private String imageId;
	
	private boolean hasAnimation;
	private Animation defAnimation;
	
	private boolean isEntity;
	private float mass;
	private float friction;
	private float speed;
	
	public float getZ() { return z; }
	
	public boolean isChunkLoader() { return chunkLoader; }
	public int getLoadRadius() { return isChunkLoader() ? loadRadius : 0; }
	
	public boolean hasCollider() { return hasCollider || colliderSize!= null; }
	public Vector getCollisionBoxSize() { return colliderSize; }
	
	public boolean hasSprite() { return hasSprite; }
	public String getSpritesheetName() { return spritesheetName; }
	public String getImageID() { return imageId; }
	
	public boolean hasAnimation() { return hasAnimation; }
	public Animation getDefaultAnimation() { return defAnimation; }
	
	public boolean isEntity() { return isEntity; }
	public float getMass() { return mass; }
	public float getFriction() { return friction; }
	public float getSpeed() { return speed; }
}
