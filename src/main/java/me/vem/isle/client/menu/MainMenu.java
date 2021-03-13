package me.vem.isle.client.menu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import me.vem.isle.client.Client;
import me.vem.isle.client.graphics.Camera;
import me.vem.isle.client.graphics.GameRenderer;
import me.vem.isle.client.graphics.RenderThread;
import me.vem.isle.client.input.CameraInputAdapter;
import me.vem.isle.client.resources.ResourceManager;
import me.vem.isle.common.Game;
import me.vem.isle.common.eio.ExtResourceManager;
import me.vem.isle.common.objects.GameObject;
import me.vem.isle.common.world.World;
import me.vem.isle.common.world.WorldThread;
import me.vem.utils.logging.Logger;

public class MainMenu extends GameRenderer{
	
	private final Font menuFont;
	
	private int selected = 0;
	
	public MainMenu() {
		super(new Dimension(512,512));
		
		Font fontCandidate = new Font("Arial", Font.TRUETYPE_FONT, 24);
		
		try {
			Font font = Font.createFont(Font.TRUETYPE_FONT, ResourceManager.getResource("8bitbold.ttf"));
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(font);
			
			fontCandidate = font.deriveFont(Font.TRUETYPE_FONT, 24);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		
		menuFont = fontCandidate;
	}
	
	private LinkedList<String> credits;
	private boolean creditsActive = false;
	private float creditsOffset = 0f;

	@Override public void render(Graphics g) {
		g.setColor(Color.WHITE);
		
		g.setFont(menuFont);
		FontMetrics fm = g.getFontMetrics();	

		if(!creditsActive) {
			int y = 50;
			int x = 50;
			for(int i=0;i<opt.length;i++) {
				if(i == selected)
					drawString(g, fm, ">", x-15, y);
				y = drawString(g, fm, opt[i] == null ? "" : opt[i].getLabel(), x, y) + 50;
			}
		}else {
			
			int y = 50 - (int)creditsOffset;
			for(String s : credits)
				y = drawStringCenter(g, fm, s, 0, y, this.getSize().width) + 10;
			
			creditsOffset += 1;
			if(creditsOffset > 620)
				creditsActive = false;
		}
	}
	
	private final LRunnable[] opt = {
		new LRunnable("New World", () -> {
			String res = JOptionPane.showInputDialog(Client.getInstance(), "Enter seed", "Custom Seed", JOptionPane.QUESTION_MESSAGE);
			int seed = 0;
			if(res != null && res.length() > 0)
				seed = res.hashCode();

			World world = Game.newWorld(seed);
			if(!this.switchToWorld(world))
				Logger.errorf("Failed to switch to world.");
			//TODO Switch to Camera
		}),
		new LRunnable("Load Game", () -> {
			JFileChooser chooser = new JFileChooser(ExtResourceManager.getWorldsDirectory());
			FileNameExtensionFilter filter = new FileNameExtensionFilter("World Files", "dat", "bck");
			chooser.setFileFilter(filter);

			int returnVal = chooser.showOpenDialog(Client.getInstance());
			if (returnVal == JFileChooser.APPROVE_OPTION && chooser.getSelectedFile() != null) {
				
				World world = Game.loadWorld(chooser.getSelectedFile());
				if(!this.switchToWorld(world))
					Logger.errorf("Failed to load and switch to world.");
			}else {
				Logger.errorf("No file was chosen.");
			}
		}),
		new LRunnable("Settings", () -> {
			//TODO Implement Settings Menu
		}),
		null,
		new LRunnable("Credits", () -> {
			//TODO (re)Implement Credits
		}),
		new LRunnable("Exit", () -> {
			Client.getInstance().shutdown();
		})
	};
	
	public void select() {
		opt[selected].run();
	}
	
	public void moveUp() {
		int sel = selected;
		
		do {
			if(--sel < 0) return;
		}while(opt[sel] == null);
		
		selected = sel;
		
		Client.getInstance().render();
	}
	
	public void moveDown() {
		int sel = selected;
		do {
			if(++sel >= opt.length) return;
		}while(opt[sel] == null);
		
		selected = sel;
		
		Client.getInstance().render();
	}
	
	private boolean switchToWorld(World world) {
		if(world == null)
			return false;

		WorldThread.begin(world);
		
		GameObject player = world.requestPlayer();
		Camera camera = new Camera(world);
		camera.getAnchor().linkTo(player);
		
		CameraInputAdapter input = new CameraInputAdapter(camera);
		
		Client.getInstance().setContext(camera, input);
		RenderThread.begin(Client.getInstance(), 60);
		
		return true;
	}
	
	private int drawString(Graphics g, FontMetrics fm, String s, int x, int y) {
		y += fm.getAscent() - fm.getDescent();
		g.drawString(s, x, y);
		
		return y;
	}
	
	private int drawStringCenter(Graphics g, FontMetrics fm, String s, int x, int y, float w) {
		y += fm.getAscent() - fm.getDescent();
		int strWidth = fm.stringWidth(s);
		
		g.drawString(s, (int)(x + w/2 - strWidth/2), y);
		
		return y;
	}
	
	/**
	 * @author Vemahk
	 * LRunnable > Labeled Runnable
	 */
	private static class LRunnable implements Runnable{
		
		private final String label;
		private final Runnable action;
		
		public LRunnable(String label, Runnable action) {
			this.label = label;
			this.action = action;
		}
		
		public String getLabel() { return label; }

		@Override
		public void run() {
			action.run();
		}
	}
}