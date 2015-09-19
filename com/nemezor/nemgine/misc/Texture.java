package com.nemezor.nemgine.misc;

public class Texture {
	
	private int	width;
	private int	height;
	private int	id;
	
	public Texture(int id, int width, int height) {
		this.id = id;
		this.width = width;
		this.height = height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getId() {
		return id;
	}
}
