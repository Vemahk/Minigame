package me.vem.isle.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.TreeSet;

import javax.swing.JPanel;

import me.vem.isle.game.Game;
import me.vem.isle.game.entity.Player;
import me.vem.isle.game.objects.GameObject;
import me.vem.isle.game.physics.BoxCollider;
import me.vem.isle.game.physics.Vector;
import me.vem.isle.game.world.Chunk;
import me.vem.isle.game.world.Land;
import me.vem.isle.game.world.World;
import me.vem.isle.menu.Setting;
import me.vem.isle.resources.ResourceManager;

public class Camera extends JPanel{
	
	private static final long serialVersionUID = 988250869004283965L;
	public static int SMOOTH_FOLLOW = 1;
	public static int RIGID_FOLLOW = 2;;
	
	private static Camera instance;
	public static Camera getInstance() {
		if(instance == null)
			instance = new Camera(512, 512, 2);
		return instance;
	}
	
	private GameObject target;
	private int followType;
	
	private final int scale;
	
	private Vector pos;
	
	private Map map;
	
	private Camera(int w, int h, int scale) {
		this.setBackground(Color.BLACK);
		this.setPreferredSize(new Dimension(w, h));
		pos = new Vector();
		
		this.scale = scale;
		
		followType = SMOOTH_FOLLOW;
	}
	
	public synchronized void follow(float... param) {
		if(target == null)
			return;
		
		if(followType == RIGID_FOLLOW) {
			setPos(target.getX(), target.getY());
		}else if(followType == SMOOTH_FOLLOW) {
			float damping = .1f;
			if(param.length > 0)
				damping = param[0];
			
			pos.offset(pos.sub(target.getPos()).scale(-damping));
		}
	}

	public void setTarget(GameObject go) {
		setTarget(go, false);
	}
	
	public void setTarget(GameObject go, boolean snapToTarget) {
		if(snapToTarget) 
			setPos(go.getX(), go.getY());
		target = go;
	}
	
	public synchronized void setX(float x) { pos.setX(x); }
	public synchronized void setY(float y) { pos.setY(y); }
	
	public synchronized void setPos(float x, float y) {
		pos.set(x, y);
	}
	
	public void moveX(float dx) { pos.offsetX(dx); }
	public void moveY(float dy) { pos.offsetY(dy); }
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawVisible(g);
	}
	
	public synchronized void drawVisible(Graphics g) {
		if(!Game.initialized)
			return;
		
		if(map == null) {
			Point p = Player.getInstance().getPos().floor();
			p.translate(-256, -256);
			map = new Map(p, 1.0); //Update the map every 1.0 seconds
		}
		
		//Draw Map
		map.tick();
		
		if(Setting.TOGGLE_MAP.isToggled()) {
			g.drawImage(map.getImage(), 0, 0, getWidth(), getHeight(), this);
			return;
		}
		
		//Draw Land
		int TIS = ResourceManager.IMAGE_SIZE * scale; //TIS --> True Image Size
		int DR = Math.max(getWidth(), getHeight()) / TIS / 2; //DR --> Display Radius
		
		int relX = pos.floorX();
		int relY = pos.floorY();
		
		for(int dx = -DR;dx<=DR;dx++) {
			for(int dy = -DR;dy<=DR;dy++) {
				int drawX = (int) Math.round((relX + dx - pos.getX()) * TIS + getWidth()/2);
				int drawY = (int) Math.round((relY + dy - pos.getY()) * TIS + getHeight()/2);
				Land land = World.getInstance().getLand(relX + dx, relY + dy);
				
				g.drawImage(land.getImage(), drawX, drawY, TIS, TIS, null);
				
				if(Game.isDebugActive()) {
					g.setColor(Color.RED);
					g.drawString((relX + dx) + "|" + (relY + dy), drawX, drawY+10);
				}
			}
		}

		//Draw Objects
		double dDR = DR + .5; //dDR --> double Display Radius
		
		HashSet<Chunk> loaded = World.getInstance().getLoadedChunks();
		synchronized(loaded) {
			for(Chunk c : loaded) {
				TreeSet<GameObject> objs = c.getObjects();
				synchronized(objs) {
					for(GameObject go : objs) {
						double dx = go.getX() - pos.getX();
						double dy = go.getY() - pos.getY();
						if(dx >= -dDR && dx <= dDR && dy >= -dDR && dy <= dDR) {
							BufferedImage image = go.getImage();
							int drawX = (int) Math.round(dx * TIS + getWidth()/2) - image.getWidth() / 2 * scale;
							int drawY = (int) Math.round(dy * TIS + getHeight()/2) - image.getHeight() / 2 * scale;
							
							g.drawImage(image, drawX, drawY, TIS, TIS, null);
							
							if(Game.isDebugActive() && go.hasCollider()) {
								BoxCollider coll = (BoxCollider) go.getCollider();
								g.setColor(Color.GREEN);
								
								int w = (int) Math.round(coll.getWidth() * TIS);
								int h = (int) Math.round(coll.getHeight() * TIS);
								g.drawRect(drawX + TIS/2 - w / 2, drawY + TIS/2 - h / 2, w, h);
							}
						}
					}
				}
			}
		}
	} //Draw visible end
}