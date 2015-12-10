package com.nemezor.nemgine.misc;

import org.lwjgl.util.vector.Vector4f;

public class Color {

	private Vector4f color;
	
	public Color(float red, float green, float blue) {
		this(red, green, blue, 1.0f);
	}
	
	public Color(float red, float green, float blue, float alpha) {
		color = new Vector4f(red, green, blue, alpha);
	}
	
	public Color(int color) {
		this((color >> 24) & 0xFF, (color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF);
	}
	
	public Color(int red, int green, int blue) {
		this(red, green, blue, Registry.COLOR_NORMALIZER_VALUE);
	}
	
	public Color(int red, int green, int blue, int alpha) {
		color = new Vector4f((float)red / (float)Registry.COLOR_NORMALIZER_VALUE, (float)green / (float)Registry.COLOR_NORMALIZER_VALUE, (float)blue / (float)Registry.COLOR_NORMALIZER_VALUE, (float)alpha / (float)Registry.COLOR_NORMALIZER_VALUE);
	}
	
	public Vector4f getColorAsVector() {
		return color;
	}
	
	public float getRed() {
		return color.x;
	}
	
	public float getGreen() {
		return color.y;
	}
	
	public float getBlue() {
		return color.z;
	}
	
	public float getAlpha() {
		return color.w;
	}
	
	public int getRedAsInt() {
		return (int) (color.x * Registry.COLOR_NORMALIZER_VALUE);
	}
	
	public int getGreenAsInt() {
		return (int) (color.y * Registry.COLOR_NORMALIZER_VALUE);
	}
	
	public int getBlueAsInt() {
		return (int) (color.z * Registry.COLOR_NORMALIZER_VALUE);
	}
	
	public int getAlphaAsInt() {
		return (int) (color.w * Registry.COLOR_NORMALIZER_VALUE);
	}
}
