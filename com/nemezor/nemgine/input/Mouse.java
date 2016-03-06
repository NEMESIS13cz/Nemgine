package com.nemezor.nemgine.input;

import java.awt.Point;
import java.nio.DoubleBuffer;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import com.nemezor.nemgine.graphics.util.Display;

public class Mouse {

	public static final int LEFT_MOUSE_BUTTON = 0;
	public static final int RIGHT_MOUSE_BUTTON = 1;
	
	private static HashMap<Long, Integer> x = new HashMap<Long, Integer>();
	private static HashMap<Long, Integer> y = new HashMap<Long, Integer>();
	private static DoubleBuffer b1 = BufferUtils.createDoubleBuffer(1);
	private static DoubleBuffer b2 = BufferUtils.createDoubleBuffer(1);
	
	private Mouse() {}
	
	public static void update(Display window) {
		GLFW.glfwGetCursorPos(window.getGLFWId(), b1, b2);
		x.put(window.getGLFWId(), (int)Math.floor(b1.get()));
		y.put(window.getGLFWId(), (int)Math.floor(b2.get()));
		b1.clear();
		b2.clear();
	}
	
	public static Point getMousePosition(Display window) {
		return new Point(x.get(window.getGLFWId()), y.get(window.getGLFWId()));
	}
	
	public static boolean isButtonDown(Display window, int id) {
		return GLFW.glfwGetMouseButton(window.getGLFWId(), id) == GLFW.GLFW_TRUE;
	}
	
	public static boolean isInsideWindow(Display window) {
		int x = Mouse.x.get(window.getGLFWId());
		int y = Mouse.y.get(window.getGLFWId());
		return !(x < 0 || x > window.getWidth() || y < 0 || y > window.getHeight());
	}
}
