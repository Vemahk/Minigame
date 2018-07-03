package me.isle.game.world;

import java.util.HashSet;
import java.util.Random;

import gustavson.simplex.SimplexNoise;
import me.isle.game.Game;
import me.isle.game.objects.GameObject;
import me.isle.game.objects.Tree;

public class World {

	private Chunk[][] chunks;
	
	private HashSet<Chunk> loaded;
	
	public HashSet<Chunk> getLoadedChunks(){
		return loaded;
	}
	
	private int WIDTH;
	private int HEIGHT;
	
	public World(int w, int h) {
		this.WIDTH = w;
		this.HEIGHT = h;
    	loaded = new HashSet<>();
    	chunks = new Chunk[WIDTH/16][HEIGHT/16];
    	
    	Random rand = Game.rand;
		SimplexNoise sn = new SimplexNoise(300, .5, rand.nextInt());
		
    	double[][] res = new double[WIDTH][HEIGHT];
    	
    	for(int x=0;x<WIDTH;x++) {
    		for(int y=0;y<HEIGHT;y++) {
                res[x][y]=0.5*(1+sn.getNoise(x,y));
    		}
    	}
    	
    	for(int x=0;x<chunks.length;x++) {
    		for(int y=0;y<chunks[x].length;y++) {
    			Chunk c = chunks[x][y] = new Chunk(x, y);
    			
				for (int sx = x * 16; sx < x * 16 + 16; sx++) {
					for (int sy = y * 16; sy < y * 16 + 16; sy++) {
    					if(res[sx][sy] >= .55 && Math.random()<.2)
    						GameObject.instantiate(new Tree(sx+.5, sy+.5).setZ(1).setCollider(.9, .9), c); //
    					if(res[sx][sy] >= .52)
    						c.setLand(sx&15, sy&15, new Grass(sx, sy));
    					else if(res[sx][sy] >= .5)
    						c.setLand(sx&15, sy&15, new Sand(sx, sy));
    					else c.setLand(sx&15, sy&15, new Water(sx, sy));
    				}
    			}
    		}
    	}
	}
	
	public int getWidth() { return WIDTH; }
	public int getHeight() { return HEIGHT; }
	
	public int getChunkWidth() { return WIDTH/16; }
	public int getChunkHeight() { return HEIGHT/16; }
	
	/**
	 * @param x
	 * @param y
	 * @return
	 */
	public Land getLand(int x, int y) {
		return chunks[x/16][y/16].getLand(x&15, y&15);
	}
	
	public boolean isWater(int x, int y) {
		return getLand(x,y) instanceof Water;
	}
	
	public boolean isSand(int x, int y) {
		return getLand(x,y) instanceof Sand;
	}
	
	public boolean isGrass(int x, int y) {
		return getLand(x,y) instanceof Grass;
	}
	
	public Chunk getChunk(int cx, int cy) {
		
		if(cx < 0 || cx >= chunks.length || cy < 0 || cy >= chunks[cx].length)
			return null;
		
		return chunks[cx][cy];
	}
}
