/**
 * 
 */
package org.societies.privacytrust.privacyprotection.dataobfuscation.obfuscator.util;

import java.util.Random;

/**
 * Tools to manage more useful random values
 * 
 * @author Olivier Maridat (Trialog)
 * @date 7 sept. 2011
 */
public class RandomBetween extends Random {
	private static final long serialVersionUID = -804773800792085641L;

	/**
	 * Return a random float between ]min(a,b), max(a,b)[
	 * nextFloatBetween(a, a) will return a
	 * @param a
	 * @param b
	 * @return
	 */
	public float nextFloatBetween(float a, float b) {
		if (a == b) 
			return a;
		float min = a;
		float max = b;
		if (b < a) {
			min = b;
			max = a;
		}
		float f = nextFloat()*(max-min)+min;
		if (a == f || b == f) {
			return nextFloatBetween(min, max);
		}
		return f;
	}
	public double nextDoubleBetween(double a, double b) {
		if (a == b) 
			return a;
		double min = a;
		double max = b;
		if (b < a) {
			min = b;
			max = a;
		}
		double f = nextDouble()*(max-min)+min;
		if (a == f || b == f) {
			return nextDoubleBetween(min, max);
		}
		return f;
	}
}
