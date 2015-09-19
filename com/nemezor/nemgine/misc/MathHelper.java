package com.nemezor.nemgine.misc;

public class MathHelper {

	public static float difference(float a, float b) {
		float aa = Math.abs(a);
		float ab = Math.abs(b);
		float larger = Math.max(aa, ab);
		if (aa == larger) {
			return aa - ab;
		}else{
			return ab - aa;
		}
	}
}
