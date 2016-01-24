package com.nemezor.nemgine.input;

import org.lwjgl.glfw.GLFW;

import com.nemezor.nemgine.graphics.util.Display;

public class Keyboard {

	public static boolean isKeyDown(Display window, int key) {
		return GLFW.glfwGetKey(window.getGLFWId(), key) == GLFW.GLFW_PRESS;
	}
	
}
