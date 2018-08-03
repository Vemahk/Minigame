package me.vem.isle.client.graphics;

import static me.vem.isle.client.graphics.UnitConversion.toPixels;
import static me.vem.isle.client.graphics.UnitConversion.toUnits;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.Set;

import javax.swing.JPanel;

import me.vem.isle.client.ClientThread;
import me.vem.isle.client.input.Setting;
import me.vem.isle.server.game.Game;
import me.vem.isle.server.game.objects.GameObject;
import me.vem.isle.server.game.physics.BoxCollider;
import me.vem.isle.server.game.world.Chunk;
import me.vem.isle.server.game.world.Land;
import me.vem.isle.server.game.world.World;
import me.vem.utils.math.Vector;

public class Camera extends JPanel {

	private static final long serialVersionUID = 988250869004283965L;
	public static int SMOOTH_FOLLOW = 1;
	public static int RIGID_FOLLOW = 2;;

	private static Camera instance;

	public static Camera getInstance() {
		if (instance == null) {
			instance = new Camera(2);
			//instance = new Camera(512, 512, 2);
		}
		return instance;
	}

	private GameObject target;
	private int followType;

	private float tarScale;
	private float scale;

	private Vector pos;

	private Map map;

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
	
	public void setScale(float f) {
		if(f < .5f) return;
		if(f > 10f) return;
		this.tarScale = f;
	}
	
	public float getScale() { return tarScale; }
	
	public boolean hasTarget() { return target != null; }

	@Override
	public void paintComponent(Graphics g) {
		if(!Game.isInitialized())
			return;
		
		super.paintComponent(g);
		
		synchronized (this) {	

			if (map == null && target != null) {
				Point p = target.getPos().floor();
				p.translate(-256, -256);
				map = new Map(p, 1.0); // Update the map every 1.0 seconds
			}

			scale += (tarScale - scale) / ClientThread.FPS;
			
			// Draw Map
			map.tick();

			if (Setting.TOGGLE_MAP.isToggled()) {
				drawMap(g);
				return;
			}

			// Draw Land
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

					dg.drawImage(land.getSprite().getImage(), drawX, drawY, null);

					if (Game.isDebugActive()) {
						dg.setColor(Color.RED);
						dg.drawString((rdx + x) + "," + (rdy + y), drawX, drawY);
					}
				}
			}

			//Draw objects
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

			//Draw to screen.
			g.drawImage(display, toPixels(pos.floorX() - pos.getX() - (DW/2 - USW/2), scale),
								 toPixels(pos.floorY() - pos.getY() - (DH/2 - USH/2), scale),
								 toPixels(DW, scale), toPixels(DH, scale), this);
			
			g.setColor(Color.WHITE);
			drawCircle(g, getWidth()/2, getHeight()/2, 5);
		}
	}
	
	public void drawCircle(Graphics g, int x, int y, int r) {
		g.fillOval(x - r + 1, y - r + 1, r * 2 - 1, r * 2 - 1);
	}
	
	public void drawMap(Graphics g) {
		int mw = map.getWidth(),
			mh = map.getHeight(),
			x = (this.getWidth() - mw)/2,
			y = (this.getHeight() - mh)/2;

		g.drawImage(map.getImage(), x, y, this);
		drawBorder(g, x, y, mw, mh, 2);
	}
	
	public void drawBorder(Graphics g, int x, int y, int w, int h, int t) {
		g.setColor(Color.WHITE);
		
		g.fillRect(x - t, y - t, w + t, t);
		g.fillRect(x - t, y, t, h + t);
		g.fillRect(x + w, y - t, t, h + t);
		g.fillRect(x, y + h, w + t, t);
	}
}