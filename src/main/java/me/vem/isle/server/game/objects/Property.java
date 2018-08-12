package me.vem.isle.server.game.objects;

import static me.vem.isle.Logger.fatalError;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.regex.Pattern;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import me.vem.isle.client.resources.Animation;
import me.vem.isle.client.resources.ResourceManager;
import me.vem.isle.client.resources.Sprite;
import me.vem.isle.server.game.controller.Controller;
import me.vem.isle.server.game.physics.Physics;
import me.vem.isle.server.game.physics.collider.Collider;
import me.vem.isle.server.game.physics.collider.ColliderType;

public class Property {
	
	private static HashMap<Integer, Property> properties = new HashMap<>();
	public static Property get(int hash) { return properties.get(hash); }
	
	public static void register(String... files) {
		for(String s : files)
			try {
				register(s);
			}catch (DocumentException e) {
				e.printStackTrace();
			}
	}
	
	public static void register(String filename) throws DocumentException {
		if(!filename.endsWith(".xml")) 
			filename+=".xml";
		
		SAXReader reader = new SAXReader();
		Document doc = reader.read(ResourceManager.getResource("properties", filename));
		
		//Property prop = new Property();
		
		//Init
		Element root = doc.getRootElement();
		if(!root.getName().equals("property")) 
			fatalError("XML Property File '"+filename+"' not formatted correctly. Make sure root element is named 'property'.");
		String id = root.attributeValue("id");
		if(id == null)
			fatalError("XML Property File '"+filename+"' not formatted correctly. 'property' element requires 'id' attribute for object id.");
		
		LinkedHashMap<String, Object> values = new LinkedHashMap<>();
		
		for(Element e : root.elements()) {
			String name = e.getName();
			String text = e.getTextTrim();
			
			if(text.isEmpty())
				values.put(name, true);
			else values.put(name, text);
			
			for(Attribute a : e.attributes())
				values.put(name+"."+a.getName(), a.getText().trim());

			LinkedList<String> list = new LinkedList<>();
			for(Element e2 : e.elements())
				list.add(e2.getText());
			if(list.size() > 0)
				values.put(name, list);
		}
		
		properties.put(id.hashCode(), new Property(id, values));
		//Logger.info(prop);
	}
	
	public boolean hasValue(String key) { return values.containsKey(key); }
	public String asString(String key) { return (String) values.get(key); }
	public int asInt(String key) { return hasValue(key) ? Integer.parseInt(asString(key)) : 0; }
	public float asFloat(String key) { return hasValue(key) ? Float.parseFloat(asString(key)) : 0f; }
	
	@SuppressWarnings("unchecked")
	public LinkedList<String> asList(String key){ return hasValue(key) ? (LinkedList<String>)values.get(key) : null; }
	
	private Property(String id, LinkedHashMap<String, Object> values) {
		this.id = id;
		this.hid = id.hashCode();
		this.values = values;
		
		//Set z
		if(hasValue("z"))
			this.z = asFloat("z");
		
		//Set Chunkloader
		isLoader = hasValue("loader");
		if(isLoader)
			radius = asInt("loader.radius");
		
		//Set collider
		hasCollider = hasValue("collider");
		if(hasCollider) {
			type = ColliderType.getType(asString("collider.type"));
			
			float w = 1;
			float h = 1;
			
			if(hasValue("collider.width"))
				w = asFloat("collider.width");
			if(hasValue("collider.height"))
				h = asFloat("collider.height");
			
			defCollider = type.generateDefaultCollider(w, h);
		}
		
		//Set Sprite
		hasSprite = hasValue("sprite");
		if(hasSprite)
			sprite = Sprite.get(asString("sprite.id"));
		
		//Set ANIMATIONSSSSSSS
		//TODO When they matter
		
		//Set Physics
		hasPhysics = hasValue("physics");
		if(hasPhysics) {
			mass = asFloat("physics.mass");
			speed = asFloat("physics.speed");
		}
		
		//Set Controller
		hasController = hasValue("controller");
		if(hasController)
			controllerType = asString("controller.type");
		
		purge("z", "loader", "collider", "sprite", "physics", "controller");
	}
	
	public void purge(String... args) {
		for(String s : args) {
			Pattern p = Pattern.compile(s + "(\\..+)*");
			values.entrySet().removeIf(e -> p.matcher(e.getKey()).matches());
		}
	}
	
	private String id;
	private int hid; // hid >> Hash ID;
	private LinkedHashMap<String, Object> values;
	
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
	
	public boolean hasAnimation() { return hasValue("animation"); }
	public Animation getDefaultAnimation() { return Animation.getAnimation(asString("animation.default")); }
	public Animation[] getAnimations() {
		LinkedList<String> list = asList("animation");
		if(list==null) return null;
		
		Animation[] out = new Animation[list.size()];
		for(int i=0;i<out.length;i++)
			out[i] = Animation.getAnimation(list.get(i));
		return out;
	}
	
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
	
	public String toString() {
		StringBuffer out = new StringBuffer("Property: "+id+"\n");
		
		for(String key : values.keySet()) 
			out.append(key + ": " + values.get(key) + "\n");
		
		return out.toString();
	}
}
