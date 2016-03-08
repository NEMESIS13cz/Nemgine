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
	
	public static final int CURSOR_NORMAL = 0x0;
	public static final int CURSOR_TEXT = 0x1;
	public static final int CURSOR_VRESIZE = 0x2;
	public static final int CURSOR_HRESIZE = 0x3;
	public static final int CURSOR_HAND = 0x4;
	public static final int CURSOR_CROSSHAIR = 0x5;
	
	private static HashMap<Long, Integer> x = new HashMap<Long, Integer>();
	private static HashMap<Long, Integer> y = new HashMap<Long, Integer>();
	private static HashMap<Long, Integer> cursor = new HashMap<Long, Integer>();
	private static DoubleBuffer b1 = BufferUtils.createDoubleBuffer(1);
	private static DoubleBuffer b2 = BufferUtils.createDoubleBuffer(1);
	private static boolean initialized = false;
	private static long normalCursor;
	private static long textCursor;
	private static long vresizeCursor;
	private static long hresizeCursor;
	private static long handCursor;
	private static long crosshairCursor;
	
	private Mouse() {}
	
	public static void initialize() {
		if (initialized) {
			return;
		}
		normalCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
		textCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_IBEAM_CURSOR);
		vresizeCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_VRESIZE_CURSOR);
		hresizeCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HRESIZE_CURSOR);
		handCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR);
		crosshairCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_CROSSHAIR_CURSOR);
	}
	
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
	
	public static void setCursor(Display window, int cursor) {
		if (Mouse.cursor.get(window.getGLFWId()) == null) {
			if (cursor == Mouse.CURSOR_NORMAL) {
				return;
			}
		}else{
			if (Mouse.cursor.get(window.getGLFWId()) == cursor) {
				return;
			}
		}
		GLFW.glfwSetCursor(window.getGLFWId(), getGLFWCursor(cursor));
		Mouse.cursor.put(window.getGLFWId(), cursor);
	}
	
	private static long getGLFWCursor(int cursor) {
		switch (cursor) {
		case CURSOR_TEXT:
			return textCursor;
		case CURSOR_VRESIZE:
			return vresizeCursor;
		case CURSOR_HRESIZE:
			return hresizeCursor;
		case CURSOR_HAND:
			return handCursor;
		case CURSOR_CROSSHAIR:
			return crosshairCursor;
		default:
			return normalCursor;
		}
	}
	
	public static void disposeAll() {
		GLFW.glfwDestroyCursor(normalCursor);
		GLFW.glfwDestroyCursor(textCursor);
		GLFW.glfwDestroyCursor(vresizeCursor);
		GLFW.glfwDestroyCursor(hresizeCursor);
		GLFW.glfwDestroyCursor(crosshairCursor);
		GLFW.glfwDestroyCursor(handCursor);
	}
}
