package me.vem.isle.client.graphics;

public class UnitConversion {

	public static final float PPU = 32; //PPU --> Pixels Per Unit
	
	public static float toUnits(int pix) {
		return pix / PPU;
	}
	
	public static float toUnits(int pix, float scale) {
		return pix / PPU / scale;
	}
	
	public static int toPixels(float units) {
		return (int)Math.floor(units * PPU);
	}
	
	public static int toPixels(float units, float scale) {
		return toPixels(units * scale);
	}
}
