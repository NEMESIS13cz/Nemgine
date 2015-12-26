package com.nemezor.nemgine.input;

import org.lwjgl.opengl.Display;

public class Mouse {

	public static final int LEFT_MOUSE_BUTTON = 0;
	public static final int RIGHT_MOUSE_BUTTON = 1;
	
	public static int getAbsoluteX() {
		return org.lwjgl.input.Mouse.getX();
	}

	public static int getAbsoluteY() {
		return org.lwjgl.input.Mouse.getY();
	}
	
	public static float getX() {
		return org.lwjgl.input.Mouse.getX() / (float)Display.getWidth();
	}
	
	public static float getY() {
		return org.lwjgl.input.Mouse.getY() / (float)Display.getHeight();
	}
	
	public static boolean isButtonDown(int id) {
		return org.lwjgl.input.Mouse.isButtonDown(id);
	}
}
