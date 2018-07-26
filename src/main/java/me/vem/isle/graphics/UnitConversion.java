package me.vem.isle.graphics;

public class UnitConversion {

	public static final float PPU = 32; //PPU --> Pixels Per Unit
	
	public static float toFloat(int i) {
		return i / PPU;
	}
	
	public static int toPixels(float f) {
		return (int)Math.floor(f * PPU);
	}
}
