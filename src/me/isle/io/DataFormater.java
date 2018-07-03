package me.isle.io;

public class DataFormater {

	public static void add(byte[] big, byte[] small, int i) {
		for(int x=0;x<small.length;x++)
			big[x+i] = small[x];
	}
	
	public static byte[] subArray(byte b[], int s, int len) {
		byte[] out = new byte[len];
		for(int i=0;i<len;i++)
			out[i] = b[i+s];
		return out;
	}
	
	public static int readInt(byte[] b, int s) {
		return bytesToInt(subArray(b, s, 4));
	}
	
	public static float readFloat(byte[] b, int s) {
		return bytesToFloat(subArray(b, s, 4));
	}
	
	public static byte[] intToBytes(int i) {
		byte[] out = new byte[4];
		for(int x=0;x<4;x++)
			out[x] = (byte) (i>>>(3-x)*8);
		return out;
	}
	
	public static byte[] floatToBytes(float f) {
		int x = Float.floatToRawIntBits(f);
		return intToBytes(x);
	}
	
	public static int bytesToInt(byte[] b) {
		int out = 0;
		for(int i=0;i<4;i++)
			out |= b[i] << (3-i) * 8;
		return out;
	}
	
	public static float bytesToFloat(byte[] b) {
		int i = bytesToInt(b);
		return Float.intBitsToFloat(i);
	}
	
}
