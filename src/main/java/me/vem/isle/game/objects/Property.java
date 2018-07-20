package me.vem.isle.game.objects;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import me.vem.isle.Logger;
import me.vem.isle.game.physics.Vector;
import me.vem.isle.graphics.Animation;
import me.vem.isle.resources.ResourceManager;
import static me.vem.isle.Logger.*;



public class Property {
	
	public static void register(Class<? extends GameObject> cls, String filename) throws DocumentException {
		if(GameObject.properties.containsKey(cls)) {
			Logger.error("GameObject being registered twice: "+ cls.getName());
			return;
		}
		
		SAXReader reader = new SAXReader();
		Document doc = reader.read(ResourceManager.getResource("properties", filename));
		
		Property prop = new Property();
		
		//Init
		Element root = doc.getRootElement();
		if(!root.getName().equals("property")) 
			fatalError("XML Property File '"+filename+"' not formatted correctly. Make sure root element is named 'property'.");
		prop.id = root.attributeValue("id");
		if(prop.id == null)
			fatalError("XML Property File '"+filename+"' not formatted correctly. 'property' element requires 'id' attribute for object id.");
		
		GameObject.properties.put(cls, prop);
		
		//z coordinate
		Element zed = root.element("z");
		if(zed!=null) {
			try {
				prop.z = Float.parseFloat(zed.getStringValue());
			}catch(NumberFormatException e) {
				error("Failed to parse floating point number in 'z' field. -> " + filename);
			}
		}
		
		//ChunkLoader
		Element cl = root.element("isChunkLoader");
		if(cl != null) {
			prop.chunkLoader = true;
			String rad = cl.attributeValue("radius");
			if(rad == null) {
				warning("No 'radius' attribute in isChunkLoader element. Defaulting to 0.");
				prop.loadRadius = 0;
			}else {
				try {
					prop.loadRadius = Integer.parseInt(rad);
				}catch(NumberFormatException e) {
					error("Failed to parse integer from 'radius' attribute. Defaulting to 0.");
					prop.loadRadius = 0;
				}
			}
		}
		
		//Collider
		Element collider = root.element("collider");
		if(collider != null) {
			prop.hasCollider = true;
			String type = collider.attributeValue("type");
			if(type == null) {
				warning("Collider attribute 'type' missing. Defaulting to Box collider.");
				prop.colliderType = "box";
			}else prop.colliderType = type;
			
			float w;
			float h;
			
			String width = collider.attributeValue("width");
			if(width == null) {
				warning("Collider attribute 'width' missing. Defaulting to 1.0");
				w = 1f;
			}else {
				try {
					w = Float.parseFloat(width);
				}catch(NumberFormatException e) {
					error("Error parsing 'width' for box collider. Defaulting to 1.0");
					w = 1f;
				}
			}
			
			String height = collider.attributeValue("height");
			if(height == null) {
				warning("Collider attribute 'height' missing. Matching 'width'");
				h = w;
			}else {
				try {
					h = Float.parseFloat(height);
				}catch(NumberFormatException e) {
					error("Error parsing 'height' for box collider. Matching 'width'");
					h = w;
				}
			}
			
			prop.colliderSize = new Vector(w, h);
		}
		
		//Sprite info
		Element sprite = root.element("sprite");
		if(sprite!=null) {
			String sheetname = sprite.attributeValue("sheet");
			if(sheetname == null)
				error("No sheet name for sprite information. No sprite info will be loaded.");
			else {
				prop.hasSprite = true;
				prop.spritesheetName = sheetname;
				
				String spriteid = sprite.attributeValue("id");
				if(spriteid==null) {
					spriteid = "0";
					warning("No sprite 'id' found. Defaulting to 0.");
				}
				
				prop.imageId = spriteid;
			}
		}else
			warning("No sprite information found in '"+filename+"'");
		
		//TODO ANIMOTIONS!
		
		//Entity
		Element ent = root.element("entity");
		if(ent != null) {
			prop.isEntity = true;
			String mass = ent.attributeValue("mass");
			if(mass != null) {
				try {
					prop.mass = Float.parseFloat(mass);
				}catch(NumberFormatException e) {
					error("Error parsing 'mass' for entity. Defaulting to 1.0");
					prop.mass = 1f;
				}
			}else{
				warning("Entity attribute 'mass' missing. Defaulting to 1.0");
				prop.mass = 1f;
			}
			
			String fr = ent.attributeValue("friction");
			if(fr != null) {
				try {
					prop.friction = Float.parseFloat(fr);
				}catch(NumberFormatException e) {
					error("Error parsing 'friction' for entity. Defaulting to .3");
					prop.friction = .3f;
				}
			}else {
				warning("Entity attribute 'friction' missing. Defaulting to .3");
				prop.friction = .3f;
			}
			
			String speed = ent.attributeValue("speed");
			if(speed != null) {
				try {
					prop.speed = Float.parseFloat(speed);
				}catch(NumberFormatException e) {
					error("Error parsing 'speed' for entity. Defaulting to 1.0");
					prop.speed = 1f;
				}
			}else {
				warning("Entity attribute 'speed' missing. Defaulting to 1.0");
				prop.speed = 1f;
			}
		}
	}
	
	private String id;
	
	private float z;
	
	private boolean chunkLoader;
	private int loadRadius;
	
	private boolean hasCollider;
	private String colliderType;
	private Vector colliderSize;
	
	private boolean hasSprite;
	private String spritesheetName;
	private String imageId;
	
	private boolean hasAnimation;
	private Animation defAnimation;
	private Animation[] others; //?
	
	private boolean isEntity;
	private float mass;
	private float friction;
	private float speed;
	
	public String getId() { return id; }
	public float getZ() { return z; }
	
	public boolean isChunkLoader() { return chunkLoader; }
	public int getLoadRadius() { return isChunkLoader() ? loadRadius : 0; }
	
	public boolean hasCollider() { return hasCollider || colliderSize!= null; }
	public String getType() { return colliderType; }
	public Vector getCollisionBoxSize() { return colliderSize; }
	
	public boolean hasSprite() { return hasSprite; }
	public String getSpritesheetName() { return spritesheetName; }
	public String getImageID() { return imageId; }
	
	public boolean hasAnimation() { return hasAnimation; }
	public Animation getDefaultAnimation() { return defAnimation; }
	public Animation[] getAnimations() { return others; }
	
	public boolean isEntity() { return isEntity; }
	public float getMass() { return mass; }
	public float getFriction() { return friction; }
	public float getSpeed() { return speed; }
	
	/*public String toString() {
		StringBuffer out = new StringBuffer("Property: \n");
		
		for(Field f : this.getClass().getDeclaredFields()) {
			try {
				out.append(f.getName() + ": " + f.get(this) + "\n");
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		return out.toString();
	}*/
}
