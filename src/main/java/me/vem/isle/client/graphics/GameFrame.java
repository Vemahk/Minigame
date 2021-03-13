package me.vem.isle.client.graphics;

import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import me.vem.isle.client.Client;
import me.vem.isle.client.input.InputAdapter;
import me.vem.isle.client.input.InputPassthrough;
import me.vem.utils.logging.Logger;

public class GameFrame extends JFrame {
	private static final long serialVersionUID = -6055946412533816167L;
	
	private final BufferedCanvas canvas;
	private final InputPassthrough input;
	
	public GameFrame() {
		super(Client.VERSION.toString());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent e) { }
		});
		
		this.setUndecorated(true);
		
		canvas = new BufferedCanvas(this);
		canvas.setFocusable(true);
		
		this.setResizable(false);
		this.setVisible(true);
		
		canvas.requestFocus();
		
		input = new InputPassthrough();
		canvas.addKeyListener(input);
		canvas.addMouseWheelListener(input);
		canvas.addMouseListener(input);
		canvas.addMouseMotionListener(input);
		
		Logger.debug("Window created");
	}
	
	public long render() {
		return canvas.render();
	}
	
	@Override public void paintComponents(Graphics g) {}
	
	public <T extends GameRenderer> void setContext(T renderer, InputAdapter<T> inputAdapter) {
		canvas.setRenderer(renderer);
		input.setInputAdapter(inputAdapter);
		
		render();
	}
}
