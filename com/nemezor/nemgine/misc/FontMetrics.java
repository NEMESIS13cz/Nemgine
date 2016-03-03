package com.nemezor.nemgine.misc;

public class FontMetrics {

	private String familyName, fontName;
	private int height, style;
	
	public FontMetrics(String familyName, String fontName, int height, int style) {
		this.familyName = familyName;
		this.fontName = fontName;
		this.height = height;
		this.style = style;
	}

	public String getFamilyName() {
		return familyName;
	}

	public String getFontName() {
		return fontName;
	}

	public int getHeight() {
		return height;
	}

	public int getStyle() {
		return style;
	}
	
	public FontMetrics clone() {
		return new FontMetrics(familyName, fontName, height, style);
	}
	
	public String toString() {
		return "FontMetrics[family: '" + familyName + "', name: '" + fontName + "', height: " + height + "px, style: 0x" + Integer.toHexString(style) + "]";
	}
}
