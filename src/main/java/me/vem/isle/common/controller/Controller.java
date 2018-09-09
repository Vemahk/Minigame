package me.vem.isle.common.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import me.vem.isle.common.Game;
import me.vem.isle.common.RIdentifiable;
import me.vem.isle.common.objects.GameObject;
import me.vem.utils.logging.Logger;

public abstract class Controller implements RIdentifiable{

	private static HashMap<Integer, Class<? extends Controller>> reg = new HashMap<>();
	public static void register(String type, Class<? extends Controller> cls) {
		reg.put(type.hashCode(), cls);
	}
	
	public static Controller newInstance(String type, GameObject parent) {
		Class<? extends Controller> cls = reg.get(type.hashCode());
		
		try {
			return cls.getDeclaredConstructor(GameObject.class).newInstance(parent);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) { }
		
		Logger.warningf("Failed to create controller type(%s) for GameObject '%s'.", type, parent);
		return null;
	}
	
	protected GameObject parent;
	protected int RUID;
	
	protected Controller(GameObject parent) {
		this.parent = parent;
		
		Game.requestRUID(this);
	}
	
	public abstract void update(float dt);
}
