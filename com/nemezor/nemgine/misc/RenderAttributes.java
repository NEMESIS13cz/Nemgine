package com.nemezor.nemgine.misc;

public class RenderAttributes {

	private boolean color, texture, transparency;
	
	public RenderAttributes(Color c, boolean textured) {
		texture = textured;
		if (c != null) {
			color = true;
			transparency = c.getAlpha() != 1.0f;
		}else{
			color = false;
			transparency = false;
		}
	}
	
	public RenderAttributes(boolean colored, boolean textured, boolean transparent) {
		color = colored;
		texture = textured;
		transparency = transparent;
	}
	
	public boolean isColored() {
		return color;
	}
	
	public boolean isTextured() {
		return texture;
	}
	
	public boolean isTransparent() {
		return transparency;
	}
}
