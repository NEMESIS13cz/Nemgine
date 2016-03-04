package com.nemezor.nemgine.misc;

import java.util.ArrayList;

public class Utils {

	public static float[] toPrimitiveArray(ArrayList<Float> data) {
		float[] ret = new float[data.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = data.get(i);
		}
		return ret;
	}
	
	public static int[] range(int begin, int end) {
		int[] ret = new int[end - begin];
		int index = 0;
		for (int i = begin; i < end; i++) {
			ret[index] = i;
			index++;
		}
		return ret;
	}
	
	public static String arrayToString(float[] array) {
		String s = "list[";
		for (int i = 0; i < array.length - 1; i++) {
			s += array[i] + ", ";
		}
		s += array[array.length - 1];
		return s + "]";
	}
}
