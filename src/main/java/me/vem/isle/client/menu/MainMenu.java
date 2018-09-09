package me.vem.isle.client.menu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import me.vem.isle.client.Client;
import me.vem.isle.client.input.Input;
import me.vem.isle.client.resources.ResourceManager;
import me.vem.isle.common.Game;
import me.vem.isle.common.eio.ExtResourceManager;
import me.vem.isle.server.Server;
import me.vem.utils.Utilities;
import me.vem.utils.logging.Logger;

public class MainMenu extends JPanel{
	private static final long serialVersionUID = -5689029521190126878L;
	
	private int selected = 0;
	
	public MainMenu() {
		this.setBackground(Color.BLACK);
		this.setPreferredSize(new Dimension(512, 512));
		
		try {
			Font font = Font.createFont(Font.TRUETYPE_FONT, ResourceManager.getResource("8bitbold.ttf"));
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(font);
			
			this.setFont(font.deriveFont(Font.TRUETYPE_FONT, 24));
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private LinkedList<String> credits;
	private boolean creditsActive = false;
	private float creditsOffset = 0f;
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.WHITE);
		
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
				y = drawStringCenter(g, fm, s, 0, y, this.getWidth()) + 10;
			
			creditsOffset += 1;
			if(creditsOffset > 620)
				creditsActive = false;
		}
	}
	
	private final LRunnable[] opt = {
		new LRunnable("New Game", () -> {

			if(Game.isInitialized()) return;
			
			Input.suspend();
			
			String res = JOptionPane.showInputDialog(Client.getInstance().getWindow(), "Enter seed", "Custom Seed", JOptionPane.QUESTION_MESSAGE);
			int seed = 0;
			if(res != null && res.length() > 0)
				seed = res.hashCode();

			Client.getInstance().setLocalServer(Server.init(seed));
			
			Input.resume();
			
			Client.getInstance().start();
		}),
		new LRunnable("Load Game", () -> {

			if(Game.isInitialized()) return;
			
			Input.suspend();
			
			JFileChooser chooser = new JFileChooser(ExtResourceManager.getWorldsDirectory());
			FileNameExtensionFilter filter = new FileNameExtensionFilter("World Files", "dat", "bck");
			chooser.setFileFilter(filter);

			int returnVal = chooser.showOpenDialog(Client.getInstance().getWindow());
			if (returnVal == JFileChooser.APPROVE_OPTION && chooser.getSelectedFile() != null) {
				Client.getInstance().setLocalServer(Server.init(chooser.getSelectedFile()));
			}else{
				Logger.errorf("No/improper file chosen. Shutting down.");
				Client.getInstance().shutdown();
			}
			
			Input.resume();
			
			Client.getInstance().start();
		}),
		new LRunnable("Settings", () -> {
			//TODO Implement Settings Menu
		}),
		null,
		new LRunnable("Credits", () -> {
			creditsActive = true;
			creditsOffset = 0f;
			
			if(credits == null) {
				credits = new LinkedList<>();
				BufferedReader reader = new BufferedReader(new InputStreamReader(ResourceManager.getResource("credits.txt")));
				
				try {
					String read;
					while((read = reader.readLine()) != null)
						credits.add(read);
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
			
			new Thread(() -> {
				while(creditsActive) {
					repaint();
					Utilities.sleep(100);
				}
				
				repaint();
			}).start();
		}),
		new LRunnable("Exit", () -> {
			Client.getInstance().shutdown();
		})
	};
	
	public void select() {
		if(!isVisible()) return;
		opt[selected].run();
	}
	
	public void moveUp() {
		if(!isVisible()) return;
		
		int sel = selected;
		
		do {
			if(--sel < 0) return;
		}while(opt[sel] == null);
		
		selected = sel;
		repaint();
	}
	
	public void moveDown() {
		if(!isVisible()) return;
		
		int sel = selected;
		do {
			if(++sel >= opt.length) return;
		}while(opt[sel] == null);
		
		selected = sel;
		repaint();
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