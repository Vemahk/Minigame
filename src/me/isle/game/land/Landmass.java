package me.isle.game.land;

import java.util.Random;

import gustavson.simplex.SimplexNoise;
import me.isle.game.Game;
import me.isle.game.objects.GameObject;
import me.isle.game.objects.Tree;
import me.isle.game.physics.BoxCollider;

public class Landmass {

	private Land[][] land;
	private int WIDTH;
	private int HEIGHT;
	
	public Landmass(int w, int h) {
		Random rand = Game.rand;
		SimplexNoise sn = new SimplexNoise(300, .5, rand.nextInt());
		
		this.WIDTH = w;
		this.HEIGHT = h;
		
    	double[][] res = new double[WIDTH][HEIGHT];
    	
    	for(int x=0;x<WIDTH;x++) {
    		for(int y=0;y<HEIGHT;y++) {
                res[x][y]=0.5*(1+sn.getNoise(x,y));
    		}
    	}
		
		land = new Land[WIDTH][HEIGHT];
		for(int x=0;x<WIDTH;x++) {
			for(int y=0;y<HEIGHT;y++) {
				if(res[x][y] >= .55 && Math.random()<.2)
					GameObject.instantiate(new Tree(x+.5, y+.5).setZ(1).setCollider(.9, .9));
				if(res[x][y] >= .52)
					land[x][y] = new Grass(x, y);
				else if(res[x][y] >= .5)
					land[x][y] = new Sand(x, y);
				else land[x][y] = new Water(x, y);
			}
		}
		
	}
	
	public int getWidth() { return WIDTH; }
	public int getHeight() { return HEIGHT; }
	
	/**
	 * @param x
	 * @param y
	 * @return
	 */
	public Land getLand(int x, int y) {
		return land[x][y];
	}
	
	public boolean isWater(int x, int y) {
		return land[x][y] instanceof Water;
	}
	
	public boolean isSand(int x, int y) {
		return land[x][y] instanceof Sand;
	}
	
	public boolean isGrass(int x, int y) {
		return land[x][y] instanceof Grass;
	}
}
