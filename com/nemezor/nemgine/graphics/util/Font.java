package com.nemezor.nemgine.graphics.util;

import java.util.HashMap;

import com.nemezor.nemgine.misc.FontMetrics;
import com.nemezor.nemgine.misc.Registry;

public class Font {

	public HashMap<Character, GLCharacter> chars;
	public int state;
	public FontMetrics metrics;
	public int heightPx, tabWidthPx;
	
	public Font() {
		state = Registry.INVALID;
	}
	
	public void initializeFont(HashMap<Character, GLCharacter> chars, FontMetrics metrics, int heightPx, int tab) {
		this.chars = chars;
		this.state = 0;
		this.metrics = metrics;
		this.heightPx = heightPx;
		this.tabWidthPx = tab;
	}
	
	public FontMetrics getMetrics() {
		return metrics.clone();
	}
}
