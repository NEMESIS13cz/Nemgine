package com.nemezor.nemgine.misc;

import java.util.HashMap;
import java.util.Iterator;

public class InputParams {

	private static HashMap<String, String> data = new HashMap<String, String>();
	
	private InputParams() {}
	
	public static void resolve(String[] args) {
		for (String s : args) {
			if ((s.startsWith(Registry.PARAMS_ARG_PREFIX_1) || s.startsWith(Registry.PARAMS_ARG_PREFIX_2)) && s.contains(Registry.PARAMS_SEPARATOR)) {
				String key = s.substring(s.startsWith(Registry.PARAMS_ARG_PREFIX_2) ? Registry.PARAMS_ARG_PREFIX_2.length() : Registry.PARAMS_ARG_PREFIX_1.length(), s.indexOf(Registry.PARAMS_SEPARATOR));
				String value = s.substring(s.indexOf(Registry.PARAMS_SEPARATOR) + 1);
				
				data.put(key, value);
			}
		}
	}
	
	public static String[] getEntries() {
		return data.keySet().toArray(new String[data.size()]);
	}
	
	public static String[] getValues() {
		Iterator<String> iter = data.keySet().iterator();
		String[] keys = new String[data.size()];
		
		int i = 0;
		while (iter.hasNext()) {
			keys[i] = iter.next();
			if (i++ >= keys.length) {
				return null;
			}
		}
		return keys;
	}
	
	public static boolean containsEntry(String name) {
		return data.get(name) != null;
	}
	
	public static boolean getBoolean(String name) {
		String s = data.get(name);
		if (s == null) {
			return false;
		}
		try {
			return Boolean.parseBoolean(s);
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public static byte getByte(String name) {
		String s = data.get(name);
		if (s == null) {
			return 0;
		}
		try {
			return Byte.parseByte(s);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public static short getShort(String name) {
		String s = data.get(name);
		if (s == null) {
			return 0;
		}
		try {
			return Short.parseShort(s);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public static int getInteger(String name) {
		String s = data.get(name);
		if (s == null) {
			return 0;
		}
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public static long getLong(String name) {
		String s = data.get(name);
		if (s == null) {
			return 0;
		}
		try {
			return Long.parseLong(s);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public static float getFloat(String name) {
		String s = data.get(name);
		if (s == null) {
			return 0;
		}
		try {
			return Float.parseFloat(s);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public static double getDouble(String name) {
		String s = data.get(name);
		if (s == null) {
			return 0;
		}
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public static String getString(String name) {
		return data.get(name);
	}
}
