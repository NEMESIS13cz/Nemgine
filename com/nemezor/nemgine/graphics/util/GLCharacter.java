package com.nemezor.nemgine.graphics.util;

public class GLCharacter {

	public float[] textureCoords;
	public int x, y, width, height;
	public char character;
	
	public GLCharacter(char character, int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.character = character;
	}
	
	public void initialize(float texW, float texH) {
		textureCoords = new float[] {x / texW, y / texH, x / texW, (y + height) / texH, (x + width) / texW, (y + height) / texH, (x + width) / texW, y / texH};
	}
}
