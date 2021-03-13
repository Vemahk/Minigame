package gustavson.simplex;
import java.util.Random;

public class SimplexNoise {

	private SNOctave[] octaves;
	private double[] freq;
	private double[] amps;
	
	public SimplexNoise(int largestFeature, double persistence, int seed) {
		int numOctaves = (int) Math.ceil(Math.log(largestFeature) / Math.log(2)); //Gets the ceilinged power of 2 of largestFeature.

		octaves = new SNOctave[numOctaves];
		freq = new double[numOctaves];
		amps = new double[numOctaves];

		Random rand = new Random(seed);

		for (int i = 0; i < numOctaves; i++) {
			octaves[i] = new SNOctave(rand.nextInt());
			freq[i] = 1<<i;
			amps[i] = Math.pow(persistence, octaves.length - i);
		}
	}
	
	public double getNoise(int x, int y) {
		double result = 0;

		for (int i = 0; i < octaves.length; i++)
			result += octaves[i].noise(x / freq[i], y / freq[i]) * amps[i];

		return result;
	}
}