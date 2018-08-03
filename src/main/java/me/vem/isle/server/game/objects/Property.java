package me.vem.isle.server.game.objects;

import static me.vem.isle.Logger.fatalError;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import me.vem.isle.Logger;
import me.vem.isle.client.resources.Animation;
import me.vem.isle.client.resources.ResourceManager;
import me.vem.isle.client.resources.Sprite;
import me.vem.isle.server.game.controller.Controller;
import me.vem.isle.server.game.physics.BoxCollider;
import me.vem.isle.server.game.physics.Collider;
import me.vem.isle.server.game.physics.Physics;
import me.vem.utils.math.Vector;

public class Property {
	
	private static HashMap<Integer, Property> properties = new HashMap<>();
	public static Property get(int hash) { return properties.get(hash); }
	
	public static void register(String filename) throws DocumentException {
		if(!filename.endsWith(".xml")) 
			filename+=".xml";
		
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
		prop.hid = prop.id.hashCode();
		
		properties.put(prop.hid, prop);
		prop.values = new LinkedHashMap<>();
		
		for(Element e : root.elements()) {
			String name = e.getName();
			String text = e.getTextTrim();
			
			if(text.isEmpty())
				prop.values.put(name, true);
			else prop.values.put(name, text);
			
			for(Attribute a : e.attributes())
				prop.values.put(name+"."+a.getName(), a.getText().trim());

			LinkedList<String> list = new LinkedList<>();
			for(Element e2 : e.elements())
				list.add(e2.getText());
			if(list.size() > 0)
				prop.values.put(name, list);
		}
		
		//Logger.info(prop);
	}
	
	private String id;
	private int hid; // hid >> Hash ID;
	private LinkedHashMap<String, Object> values;
	
	public boolean asBoolean(String key) { return values.containsKey(key); }
	public String asString(String key) { return (String) values.get(key); }
	public int asInt(String key) { return asBoolean(key) ? Integer.parseInt(asString(key)) : 0; }
	public float asFloat(String key) { return asBoolean(key) ? Float.parseFloat(asString(key)) : 0f; }
	
	@SuppressWarnings("unchecked")
	public LinkedList<String> asList(String key){ return asBoolean(key) ? (LinkedList<String>)values.get(key) : null; }
	
	public String getId() { return id; }
	
	@Override public int hashCode() { return hid; }
	
	public float getZ() { return asFloat("z"); }
	
	public boolean isChunkLoader() { return asBoolean("loader"); }
	public int getLoadRadius() { return asInt("loader.radius"); }
	
	public boolean hasCollider() { return asBoolean("collider"); }
	public String getType() { return asString("collider.type"); }
	public Vector getCollisionBoxSize() { return hasCollider() ? new Vector(asFloat("collider.width"), asFloat("collider.height")) : null; }
	public Collider buildCollider(GameObject parent) {
		if(!hasCollider()) return null;
		String type = getType();
		
		if(type.equalsIgnoreCase("box")) {
			Vector size = getCollisionBoxSize();
			return new BoxCollider(parent, size.getX(), size.getY());
		}
		
		Logger.warningf("The collider type for %s was not recorgnized. (%s)", this.id, type);
		return null;
	}
	
	public boolean hasSprite() { return asBoolean("sprite"); }
	public String getImageId() { return asString("sprite.id"); } 
	public Sprite getSprite() { return Sprite.get(getImageId()); }
	
	public boolean hasAnimation() { return asBoolean("animation"); }
	public Animation getDefaultAnimation() { return Animation.getAnimation(asString("animation.default")); }
	public Animation[] getAnimations() {
		LinkedList<String> list = asList("animation");
		if(list==null) return null;
		
		Animation[] out = new Animation[list.size()];
		for(int i=0;i<out.length;i++)
			out[i] = Animation.getAnimation(list.get(i));
		return out;
	}
	
	public boolean isPhysics() { return asBoolean("physics"); }
	public float getMass() { return asFloat("physics.mass"); }
	public float getSpeed() { return asFloat("physics.speed"); }
	public Physics buildPhysics(GameObject parent) {
		if(!isPhysics()) return null;
		return new Physics(parent, getMass(), getSpeed());
	}
	
	public boolean hasController() { return asBoolean("controller"); }
	public String getControllerType() { return asString("controller.type"); }
	public Controller buildController(GameObject parent) {
		if(!hasController()) return null;
		
		return Controller.newInstance(getControllerType(), parent);
	}
	
	public String toString() {
		StringBuffer out = new StringBuffer("Property: "+id+"\n");
		
		for(String key : values.keySet()) 
			out.append(key + ": " + values.get(key) + "\n");
		
		return out.toString();
	}
}
