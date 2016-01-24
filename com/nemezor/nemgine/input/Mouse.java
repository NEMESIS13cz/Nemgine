package com.nemezor.nemgine.input;

import java.nio.DoubleBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import com.nemezor.nemgine.graphics.util.Display;

public class Mouse {

	public static final int LEFT_MOUSE_BUTTON = 0;
	public static final int RIGHT_MOUSE_BUTTON = 1;
	
	public static double[] getMousePosition(Display window) {
		DoubleBuffer b1 = BufferUtils.createDoubleBuffer(1);
		DoubleBuffer b2 = BufferUtils.createDoubleBuffer(1);
		GLFW.glfwGetCursorPos(window.getGLFWId(), b1, b2);
		return new double[] {b1.get(), b2.get()};
	}
	
	public static boolean isButtonDown(Display window, int id) {
		return GLFW.glfwGetMouseButton(window.getGLFWId(), id) == GLFW.GLFW_TRUE;
	}
	
	public static boolean isInsideWindow(Display window) {
		double[] pos = getMousePosition(window);
		return !(pos[0] < 0 || pos[0] > window.getWidth() || pos[1] < 0 || pos[1] > window.getHeight());
	}
}
