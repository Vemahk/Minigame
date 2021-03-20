package me.vem.isle.common.world;

public enum Land{
	
	WATER(0), SAND(1), GRASS(2);
	
	private byte id;
	
	private Land(int id) {
		this.id = (byte)id;
	}
	
	public byte getId() { return id; }
	
	public float getFriction() {
		switch(this) {
		case WATER: return .7f;
		default: return .3f;
		}
	}
	
	public boolean isWater() { return this==WATER; }
	public boolean isSand() { return this==SAND; }
	public boolean isGrass() { return this==GRASS; }
	
	private static final Land[] vals = Land.values();
	public static Land[] valuesStatic() {
		return vals;
	}
}