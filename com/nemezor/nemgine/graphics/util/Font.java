package com.nemezor.nemgine.graphics.util;

import java.util.ArrayList;
import java.util.HashMap;

import com.nemezor.nemgine.misc.FontMetrics;
import com.nemezor.nemgine.misc.Registry;

public class Font {

	public HashMap<Character, GLCharacter> chars = new HashMap<Character, GLCharacter>();
	public ArrayList<Integer> textures = new ArrayList<Integer>();
	public FontMetrics metrics;
	public java.awt.Font font;
	public int state;
	public int heightPx, tabWidthPx;
	public int currX = 0;
	public int currY = 0;
	public int highest = 0;
	public int width = 0;
	public int charCount = 0;
	public int widest = 0;
	public int height = 0;
	public int currentTexture;
	
	public Font() {
		state = Registry.INVALID;
	}
	
	public void initializeFont(FontMetrics metrics, int heightPx, int tab) {
		this.state = 0;
		this.metrics = metrics;
		this.heightPx = heightPx;
		this.tabWidthPx = tab;
	}
	
	public FontMetrics getMetrics() {
		return metrics.clone();
	}
}
