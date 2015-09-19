package com.nemezor.nemgine.tests;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.nemezor.nemgine.debug.ImmediateGraphics;

/**
 * 
 * First prototype of a physics object
 * 
 * @author nemes
 */
public class Cube {
	
	private float		size;
	private Vector3f	location;
	private Vector3f	rotation;
	private Vector3f	velocity;
	
	protected boolean wasChecked = false;
	
	public Cube(Vector3f location, float size) {
		this.setLocation(location);
		this.setRotation(new Vector3f(0, 0, 0));
		this.setVelocity(new Vector3f(0, 0, 0));
		this.size = size;
	}
	
	public Cube(Vector3f location, Vector3f velocity, float size) {
		this.setLocation(location);
		this.setVelocity(velocity);
		this.setRotation(new Vector3f(0, 0, 0));
		this.size = size;
	}
	
	public float getSize() {
		return size;
	}
	
	public void setSize(float size) {
		this.size = size;
	}
	
	public Vector3f getLocation() {
		return location;
	}
	
	public void setLocation(Vector3f location) {
		this.location = location;
	}
	
	public Vector3f getRotation() {
		return rotation;
	}
	
	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}
	
	public Vector3f getVelocity() {
		return velocity;
	}
	
	public void setVelocity(Vector3f velocity) {
		this.velocity = velocity;
	}
	
	public void render() {
		// float halfSize = size / 2.0f;
		float x = location.getX();
		float y = location.getY();
		float z = location.getZ();
		
		// GL11.glTranslatef(x - halfSize, y + halfSize, z + halfSize);
		
		GL11.glBegin(GL11.GL_QUADS);
		
		GL11.glColor4f(1, 0, 1, 1);
		ImmediateGraphics.vertex3f(x, y, z);
		ImmediateGraphics.vertex3f(x, y - size, z);
		ImmediateGraphics.vertex3f(x + size, y - size, z);
		ImmediateGraphics.vertex3f(x + size, y, z);
		
		GL11.glColor4f(1, 0, 0, 1);
		ImmediateGraphics.vertex3f(x, y - size, z - size);
		ImmediateGraphics.vertex3f(x, y, z - size);
		ImmediateGraphics.vertex3f(x + size, y, z - size);
		ImmediateGraphics.vertex3f(x + size, y - size, z - size);
		
		GL11.glColor4f(0, 1, 0, 1);
		ImmediateGraphics.vertex3f(x, y, z);
		ImmediateGraphics.vertex3f(x, y, z - size);
		ImmediateGraphics.vertex3f(x, y - size, z - size);
		ImmediateGraphics.vertex3f(x, y - size, z);
		
		GL11.glColor4f(0, 0, 1, 1);
		ImmediateGraphics.vertex3f(x + size, y - size, z);
		ImmediateGraphics.vertex3f(x + size, y - size, z - size);
		ImmediateGraphics.vertex3f(x + size, y, z - size);
		ImmediateGraphics.vertex3f(x + size, y, z);
		
		GL11.glColor4f(0, 1, 1, 1);
		ImmediateGraphics.vertex3f(x, y, z);
		ImmediateGraphics.vertex3f(x + size, y, z);
		ImmediateGraphics.vertex3f(x + size, y, z - size);
		ImmediateGraphics.vertex3f(x, y, z - size);
		
		GL11.glColor4f(1, 1, 0, 1);
		ImmediateGraphics.vertex3f(x, y - size, z - size);
		ImmediateGraphics.vertex3f(x + size, y - size, z - size);
		ImmediateGraphics.vertex3f(x + size, y - size, z);
		ImmediateGraphics.vertex3f(x, y - size, z);
		
		GL11.glEnd();
		
		// GL11.glTranslatef(-(x - halfSize), -(y + halfSize), -(z + halfSize));
	}
}
