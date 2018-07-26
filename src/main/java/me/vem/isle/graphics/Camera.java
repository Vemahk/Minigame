package me.vem.isle.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.TreeSet;

import javax.swing.JPanel;

import me.vem.isle.Logger;
import me.vem.isle.game.Game;
import me.vem.isle.game.entity.Player;
import me.vem.isle.game.objects.GameObject;
import me.vem.isle.game.physics.BoxCollider;
import me.vem.isle.game.world.Chunk;
import me.vem.isle.game.world.Land;
import me.vem.isle.game.world.World;
import me.vem.isle.menu.Setting;
import me.vem.utils.math.Vector;

import static me.vem.isle.graphics.UnitConversion.*;

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

	private float scale;

	private Vector pos;

	private Map map;

	private Camera(float scale) {
		this(Toolkit.getDefaultToolkit().getScreenSize().width,
			 Toolkit.getDefaultToolkit().getScreenSize().height, scale);
	}
	
	private Camera(int w, int h, float scale) {
		this.setBackground(Color.BLACK);
		this.setPreferredSize(new Dimension(w, h));
		pos = new Vector();

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

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		synchronized (this) {
			if (!Game.initialized)
				return;

			if (map == null) {
				Point p = Player.getInstance().getPos().floor();
				p.translate(-256, -256);
				map = new Map(p, 1.0); // Update the map every 1.0 seconds
			}

			// Draw Map
			map.tick();

			if (Setting.TOGGLE_MAP.isToggled()) {
				drawMap(g);
				return;
			}

			// Draw Land
			int TS = toPixels(scale); // TS --> True Scale

			int DW = getWidth() / TS + 1;
			int DH = getHeight() / TS + 2;

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

			HashSet<Chunk> lc = World.getInstance().getLoadedChunks();// lc >> Loaded Chunks
			synchronized (lc) {
				for (Chunk c : lc) {
					TreeSet<GameObject> objs = c.getObjects();
					synchronized (objs) {
						for (GameObject go : objs) {
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
				}
			}

			g.drawImage(display, toPixels((pos.floorX() - pos.getX()) * scale),
								 toPixels((pos.floorY() - pos.getY()) * scale),
								 DW * TS, DH * TS, this);
		}
	}
	
	public void drawMap(Graphics g) {
		int mw = map.getWidth(),
			mh = map.getHeight(),
			x = (this.getWidth() - mw)/2,
			y = (this.getHeight() - mh)/2;

		g.drawImage(map.getImage(), x, y, this);
		g.setColor(Color.WHITE);
		
		g.fillRect(x - 2, y - 2, mw + 4, 2);
		g.fillRect(x - 2, y - 2, 2, mh + 4);
		g.fillRect(x + mw, y, 2, mh + 2);
		g.fillRect(x, y + mh, mw + 2, 2);
	}
}