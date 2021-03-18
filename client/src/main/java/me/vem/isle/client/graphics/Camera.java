package me.vem.isle.client.graphics;

import static me.vem.isle.client.graphics.UnitConversion.toPixels;
import static me.vem.isle.client.graphics.UnitConversion.toUnits;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

import me.vem.isle.client.resources.Animation;
import me.vem.isle.client.resources.Sprite;
import me.vem.isle.common.objects.GameObject;
import me.vem.isle.common.physics.collider.BoxCollider;
import me.vem.isle.common.world.Chunk;
import me.vem.isle.common.world.Land;
import me.vem.isle.common.world.World;
import me.vem.utils.math.Vector;

public class Camera extends GameRenderer {
	
	private final CameraAnchor anchor;
	
	private float tarScale;
	private float scale;

	public Camera(World world) throws URISyntaxException, IOException {
		this(world, 2);
	}
	
	private Camera(World world, float scale) throws URISyntaxException, IOException {
		this(world, Toolkit.getDefaultToolkit().getScreenSize(), scale);
	}
	
	private Camera(World world, Dimension dim, float scale) throws URISyntaxException, IOException {
		super(dim);

		Sprite.registerSpritesheets();
		
		this.tarScale = scale;
		this.scale = scale;
		this.anchor = new CameraAnchor(world, 0, 0);
	}
		
	public CameraAnchor getAnchor() {
		return anchor;
	}
	
	public boolean hasTarget() { return anchor != null; }
	
	public void setScale(float f) {
		if(f < .5f) return;
		if(f > 10f) return;
		this.tarScale = f;
	}
	
	public float getScale() { return tarScale; }

	@Override public void render(Graphics g) {
		if(anchor == null)
			return;
		
		this.anchor.follow();
		
		scale += (tarScale - scale) / RenderThread.fps();

		Animation.tickAll();
		Vector pos = anchor.getPos();
		
		// Draw visible land
		float USW = toUnits(getSize().width, scale),
			  USH = toUnits(getSize().height, scale);
		int DW = (int)Math.floor(USW) + 2,
			DH = (int)Math.floor(USH) + 2;

		BufferedImage display = new BufferedImage(toPixels(DW), toPixels(DH), BufferedImage.TYPE_INT_ARGB);
		Graphics dg = display.getGraphics();

		int rdx = pos.floorX() - DW / 2;
		int rdy = pos.floorY() - DH / 2;

		World world = anchor.getWorld();
		for (int x = 0; x < DW; x++) {
			for (int y = 0; y < DH; y++) {
				Land land = world.getLand(rdx + x, rdy + y);

				int drawX = toPixels(x);
				int drawY = toPixels(y);

				Image lSprite = Sprite.get(land.toString().toLowerCase()).getImage();
				dg.drawImage(lSprite, drawX, drawY, null);
			}
		}

		//Draw loaded objects
		Set<GameObject> loadedObjects = Chunk.getLoadedObjects();
		synchronized(loadedObjects) {
			for (GameObject go : loadedObjects) {
				Sprite sprite = Sprite.getByType(go.getTypeId());
				if(sprite == null)
					continue;
				
				float rx = go.getX() - rdx, ry = go.getY() - rdy,
						wb = sprite.getWidth(),
						hb = sprite.getHeight();

				if (rx + wb <= 0 || ry + hb <= 0 || rx - wb >= DW || ry - hb >= DH)
					continue;

				dg.drawImage(sprite.getImage(), toPixels(rx - wb / 2), toPixels(ry - hb / 2), null);

				if (debugActive && go.hasCollider()) {
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
							 toPixels(DW, scale), toPixels(DH, scale), null);
		
		if(debugActive) {
			String s = String.format("FPS: %.3f", RenderThread.realFps());
			
			g.setColor(Color.WHITE);
			g.drawString(s, 5, this.getSize().height - 5);
		}
	}
	
	private boolean debugActive;
	public void toggleDebugMode() {
		debugActive = !debugActive;
	}
}