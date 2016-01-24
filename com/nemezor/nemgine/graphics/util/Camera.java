package com.nemezor.nemgine.graphics.util;

import org.lwjgl.util.vector.Vector3f;

public class Camera {

	private Vector3f rotation;
	private Vector3f position;
	
	public Camera(Vector3f position, Vector3f rotation) {
		this.rotation = rotation;
		this.position = position;
	}

	public Vector3f getRotation() {
		return this.rotation;
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
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
	
	public String toString() {
		return "R-" + rotation.toString() + "\nP-" + position.toString();
	}
}
