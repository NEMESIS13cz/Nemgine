package com.nemezor.nemgine.graphics.util;

public class GLCharacter {

	public float[] textureCoords;
	public int x, y, width, height;
	public char character;
	public int glTex;
	
	public GLCharacter(char character, int x, int y, int width, int height, int glTexture) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.character = character;
		this.glTex = glTexture;
	}
	
	public void initialize(float texW, float texH) {
		textureCoords = new float[] {x / texW, y / texH, (x + width) / texW, (y + height) / texH};
	}
}
