package com.nemezor.nemgine.graphics.util;

import org.lwjgl.util.vector.Vector3f;

import com.nemezor.nemgine.misc.Color;

public class LightSource {

	private Vector3f position;
	private Color color;
	
	public LightSource(Vector3f position, Color color) {
		this.position = position;
		this.color = color;
	}

	public Vector3f getPosition() {
		return this.position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}
	
	public void move(Vector3f offset) {
		Vector3f.add(position, offset, position);
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color newColor) {
		color = newColor;
	}
}
