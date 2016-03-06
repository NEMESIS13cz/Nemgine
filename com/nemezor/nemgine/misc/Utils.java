package com.nemezor.nemgine.misc;

import java.util.ArrayList;

public class Utils {

	private Utils() {}
	
	public static boolean[] toPrimitiveArrayb(ArrayList<Boolean> data) {
		boolean[] ret = new boolean[data.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = data.get(i);
		}
		return ret;
	}

	public static short[] toPrimitiveArrays(ArrayList<Short> data) {
		short[] ret = new short[data.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = data.get(i);
		}
		return ret;
	}
	
	public static int[] toPrimitiveArrayi(ArrayList<Integer> data) {
		int[] ret = new int[data.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = data.get(i);
		}
		return ret;
	}
	
	public static long[] toPrimitiveArrayl(ArrayList<Long> data) {
		long[] ret = new long[data.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = data.get(i);
		}
		return ret;
	}
	
	public static float[] toPrimitiveArrayf(ArrayList<Float> data) {
		float[] ret = new float[data.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = data.get(i);
		}
		return ret;
	}
	
	public static double[] toPrimitiveArrayd(ArrayList<Double> data) {
		double[] ret = new double[data.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = data.get(i);
		}
		return ret;
	}

	public static String[] toPrimitiveArraystr(ArrayList<String> data) {
		String[] ret = new String[data.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = data.get(i);
		}
		return ret;
	}

	public static Object[] toPrimitiveArrayobj(ArrayList<Object> data) {
		Object[] ret = new Object[data.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = data.get(i);
		}
		return ret;
	}
	
	public static int[] range(int begin, int end) {
		int[] ret = new int[Math.abs(end - begin)];
		int index = 0;
		for (int i = begin; i < end; i++) {
			ret[index] = i;
			index++;
		}
		return ret;
	}
	
	public static <T> String arrayToString(T[] array) {
		String s = "list[";
		for (int i = 0; i < array.length - 1; i++) {
			s += array[i] + ", ";
		}
		s += array[array.length - 1];
		return s + "]";
	}
}
