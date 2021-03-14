package me.vem.isle.common.objects;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import me.vem.isle.client.resources.Sprite;
import me.vem.isle.common.controller.Controller;
import me.vem.isle.common.io.ResourceManager;
import me.vem.isle.common.physics.Physics;
import me.vem.isle.common.physics.collider.Collider;
import me.vem.isle.common.physics.collider.ColliderType;
import me.vem.utils.logging.Logger;

public class Property {
	
	private static HashMap<Integer, Property> properties = new HashMap<>();
	public static Property get(int hash) { return properties.get(hash); }
	
	public static boolean register() {
		try {
			Path[] paths = ResourceManager.getResourceFilePaths("/properties");
			
			for(Path path : paths) {
				String fileName = path.toString();
				if(!register(fileName))
					return false;
			}
			
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public static boolean register(String filePath) {
		if(!filePath.endsWith(".json")) 
			filePath+=".json";
		
		JSONTokener tokener = new JSONTokener(ResourceManager.getResource(filePath));
		JSONObject root = new JSONObject(tokener);
		
		//Init
		if(!root.has("id")) {
			Logger.fatalError("JSON Property File '"+filePath+"' not formatted correctly. Element requires 'id' attribute for object id.");
			return false;
		}
		
		String id = root.getString("id");
		
		properties.put(id.hashCode(), new Property(id, root));
		//Logger.info(prop);
		
		return true;
	}
	
	private Property(String id, JSONObject def) {
		this.id = id;
		this.hid = id.hashCode();
		
		//Set z
		if(def.has("z"))
			this.z = def.getFloat("z");
		
		//Set Chunkloader
		isLoader = def.has("loader");
		if(isLoader) {
			JSONObject loaderDef = def.getJSONObject("loader");
			
			radius = loaderDef.optInt("radius");
			if(radius <= 0)
				radius = 1;
		}
		
		//Set collider
		hasCollider = def.has("collider");
		if(hasCollider) {
			JSONObject colliderDef = def.getJSONObject("collider");
			
			requireChild(colliderDef, "collider", "type");
			type = ColliderType.getType(colliderDef.getString("type"));
			
			float w = colliderDef.optFloat("width");
			float h = colliderDef.optFloat("height");
			
			if(w <= 0)
				w = 1f;
			
			if(h <= 0)
				h = 1f;
			
			defCollider = type.generateDefaultCollider(w, h);
		}
		
		//Set Sprite
		hasSprite = def.has("sprite");
		if(hasSprite)
			sprite = Sprite.get(def.getString("sprite"));
		
		//Set ANIMATIONSSSSSSS TODO
		
		//Set Physics
		hasPhysics = def.has("physics");
		if(hasPhysics) {
			JSONObject physicsDef = def.getJSONObject("physics");
			
			mass = physicsDef.optFloat("mass");
			if(mass <= 0)
				mass = 1f;
			
			speed = physicsDef.getFloat("speed");
			if(speed < 0)
				speed = 0;
		}
		
		//Set Controller
		hasController = def.has("controller");
		if(hasController) {
			JSONObject controllerDef = def.getJSONObject("controller");
			
			requireChild(controllerDef, "controller", "type");
			controllerType = controllerDef.getString("type");
		}
	}
	
	private void requireChild(JSONObject parent, String parentName, String expectedChild) {
		if(!parent.has(expectedChild))
			throw new JSONException("When loading '"+parentName+"' from a .json property (id="+this.id+"), '"+expectedChild+"' was not provided.");
	}
	
	private String id;
	private int hid; // hid >> Hash ID;
	
	public String getId() { return id; }
	
	@Override public int hashCode() { return hid; }
	
	private float z;
	public float getZ() { return z; }
	
	private boolean isLoader;
	private int radius; //lRad >> Load Radius
	public boolean isChunkLoader() { return isLoader; }
	public int getLoadRadius() { return radius; }
	
	private boolean hasCollider;
	private ColliderType type;
	private Collider defCollider;
	public boolean hasCollider() { return hasCollider; }
	public ColliderType getType() { return type; }
	public Collider buildCollider(GameObject parent) {
		if(!hasCollider()) return null;
		return defCollider.copy(parent);
	}
	
	private boolean hasSprite;
	private Sprite sprite;
	public boolean hasSprite() { return hasSprite; }
	public Sprite getSprite() { return sprite; }
	
//	public boolean hasAnimation() { return hasValue("animation"); }
//	public Animation getDefaultAnimation() { return Animation.getAnimation(asString("animation.default")); }
//	public Animation[] getAnimations() {
//		LinkedList<String> list = asList("animation");
//		if(list==null) return null;
//		
//		Animation[] out = new Animation[list.size()];
//		for(int i=0;i<out.length;i++)
//			out[i] = Animation.getAnimation(list.get(i));
//		return out;
//	}
	
	private boolean hasPhysics;
	private float mass;
	private float speed;
	public boolean hasPhysics() { return hasPhysics; }
	public Physics buildPhysics(GameObject parent) {
		if(!hasPhysics()) return null;
		return new Physics(parent, mass, speed);
	}
	
	private boolean hasController;
	private String controllerType;
	public boolean hasController() { return hasController; }
	public String getControllerType() { return controllerType; }
	public Controller buildController(GameObject parent) {
		if(!hasController()) return null;
		return Controller.newInstance(controllerType, parent);
	}
}
