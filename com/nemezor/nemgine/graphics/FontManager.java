package com.nemezor.nemgine.graphics;

import java.util.HashMap;

import com.nemezor.nemgine.graphics.util.Font;
import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.misc.Registry;

public class FontManager {

	private static HashMap<Integer, Font> fonts = new HashMap<Integer, Font>();
	private static int fontCounter = 0;
	
	public static synchronized int generateFonts() {
		if (Nemgine.getSide().isServer()) {
			return Registry.INVALID;
		}
		fontCounter++;
		fonts.put(fontCounter, new Font());
		return fontCounter;
	}
	
	public static boolean initializeFont(int id, java.awt.Font font) {
		if (Nemgine.getSide().isServer()) {
			return false;
		}
		
		
		
		return false;
	}
}
