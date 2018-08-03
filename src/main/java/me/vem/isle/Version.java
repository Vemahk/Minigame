package me.vem.isle;

public final class Version {

	private static final String GAME_NAME = "Dopey Survival";
	
	private final int major, minor, rev;
	
	public Version(int major, int minor, int rev) {
		this.major = major;
		this.minor = minor;
		this.rev = rev;
	}
	
	public String toString() {
		return String.format("%s[%d.%d.%d]", GAME_NAME, major, minor, rev);
	}
}
