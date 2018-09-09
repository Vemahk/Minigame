package me.vem.isle.client.graphics;

import static me.vem.isle.client.graphics.UnitConversion.toPixels;
import static me.vem.isle.client.graphics.UnitConversion.toUnits;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.Set;

import javax.swing.JPanel;

import me.vem.isle.client.Client;
import me.vem.isle.client.input.ActionSet;
import me.vem.isle.client.input.Setting;
import me.vem.isle.client.resources.Animation;
import me.vem.isle.client.resources.Sprite;
import me.vem.isle.common.Game;
import me.vem.isle.common.objects.GameObject;
import me.vem.isle.common.physics.collider.BoxCollider;
import me.vem.isle.common.world.Chunk;
import me.vem.isle.common.world.Land;
import me.vem.isle.common.world.World;
import me.vem.utils.Utilities;
import me.vem.utils.math.Vector;

public class Camera extends JPanel {

	private static final long serialVersionUID = 988250869004283965L;
	public static int SMOOTH_FOLLOW = 1;
	public static int RIGID_FOLLOW = 2;;

	private static Camera instance;

	public static Camera getInstance() { return instance; }
	
	public static void init() {
		if(instance != null)
			return;
		
		while(!Game.isInitialized())
			Utilities.sleep(Client.FPS);
		instance = new Camera(2);
		instance.setTarget(Game.getPlayer(), true);
		WorldMap.init();

		Client.getInstance().setWindowContent(instance, ActionSet.GAME);
	}

	private GameObject target;
	private int followType;

	private float tarScale;
	private float scale;

	private Vector pos;

	private Camera(float scale) {
		this(Toolkit.getDefaultToolkit().getScreenSize(), scale);
	}
	
	private Camera(Dimension dim, float scale) {
		this(dim.width, dim.height, scale);
	}
	
	private Camera(int w, int h, float scale) {
		this.setBackground(Color.BLACK);
		this.setPreferredSize(new Dimension(w, h));
		pos = new Vector();

		this.tarScale = scale;
		this.scale = scale;

		followType = SMOOTH_FOLLOW;
	}

	public synchronized void follow(float... param) {
		if (target == null)
			return;
		
		if (followType == RIGID_FOLLOW) {
			pos.set(target.getPos());
		} else if (followType == SMOOTH_FOLLOW) {
			float damping = .1f;
			if (param.length > 0)
				damping = param[0];
			
			pos.offset(target.getPos().sub(pos).scale(damping));
		}
	}

	public void setTarget(GameObject go, boolean snapToTarget) {
		if (snapToTarget)
			pos.set(go.getPos());
		target = go;
	}
	
	public boolean hasTarget() { return target != null; }
	
	public void setScale(float f) {
		if(f < .5f) return;
		if(f > 10f) return;
		this.tarScale = f;
	}
	
	public float getScale() { return tarScale; }

	@Override
	public void paintComponent(Graphics g) {
		if(!Game.isInitialized())
			return;
		
		super.paintComponent(g);
		
		synchronized (this) {

			scale += (tarScale - scale) / Client.FPS;

			if (Setting.TOGGLE_MAP.isToggled()) {
				WorldMap.getInstance().drawMap(g);
				return;
			}

			Animation.tickAll();
			follow(.05f);
			
			// Draw visible land
			float USW = toUnits(getWidth(), scale),
				  USH = toUnits(getHeight(), scale);
			int DW = (int)Math.floor(USW) + 2,
				DH = (int)Math.floor(USH) + 2;

			BufferedImage display = new BufferedImage(toPixels(DW), toPixels(DH), BufferedImage.TYPE_INT_ARGB);
			Graphics dg = display.getGraphics();

			int rdx = pos.floorX() - DW / 2;
			int rdy = pos.floorY() - DH / 2;

			for (int x = 0; x < DW; x++) {
				for (int y = 0; y < DH; y++) {
					Land land = World.getInstance().getLand(rdx + x, rdy + y);

					int drawX = toPixels(x);
					int drawY = toPixels(y);

					Image lSprite = Sprite.get(land.toString().toLowerCase()).getImage();
					dg.drawImage(lSprite, drawX, drawY, null);

					if (Game.isDebugActive()) {
						dg.setColor(Color.RED);
						dg.drawString((rdx + x) + "," + (rdy + y), drawX, drawY);
					}
				}
			}

			//Draw loaded objects
			Set<GameObject> loadedObjects = Chunk.getLoadedObjects();
			synchronized(loadedObjects) {
				for (GameObject go : loadedObjects) {
					float rx = go.getX() - rdx, ry = go.getY() - rdy, wb = go.getSprite().getWidth(),
							hb = go.getSprite().getHeight();
	
					if (rx + wb <= 0 || ry + hb <= 0 || rx - wb >= DW || ry - hb >= DH)
						continue;
	
					dg.drawImage(go.getSprite().getImage(), toPixels(rx - wb / 2), toPixels(ry - hb / 2), null);
	
					if (Game.isDebugActive() && go.hasCollider()) {
						BoxCollider bc = (BoxCollider) go.getCollider();
						float cw = bc.getWidth(), ch = bc.getHeight();
	
						dg.setColor(Color.GREEN);
						dg.drawRect(toPixels(rx - cw / 2), toPixels(ry - ch / 2), toPixels(cw), toPixels(ch));
					}
				}
			}

			//Draw buffer to screen
			g.drawImage(display, toPixels(pos.floorX() - pos.getX() - (DW/2 - USW/2), scale),
								 toPixels(pos.floorY() - pos.getY() - (DH/2 - USH/2), scale),
								 toPixels(DW, scale), toPixels(DH, scale), this);
		}
	}
}