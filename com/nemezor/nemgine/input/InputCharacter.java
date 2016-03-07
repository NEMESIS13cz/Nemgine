package com.nemezor.nemgine.input;

import org.lwjgl.glfw.GLFW;

import com.nemezor.nemgine.misc.Registry;

public class InputCharacter {

	public static final int ESCAPE = 0x1;
	public static final int ENTER = 0x2;
	public static final int BACKSPACE = 0x3;
	public static final int DELETE = 0x4;
	public static final int INSERT = 0x5;
	
	private char c;
	private int code;
	
	public InputCharacter(char c) {
		this.c = c;
		code = Registry.INVALID;
	}
	
	public InputCharacter(int glfwCode) {
		switch (glfwCode) {
		case GLFW.GLFW_KEY_ESCAPE:
			code = ESCAPE;
			break;
		case GLFW.GLFW_KEY_INSERT:
			code = INSERT;
			break;
		case GLFW.GLFW_KEY_ENTER:
		case GLFW.GLFW_KEY_KP_ENTER:
			code = ENTER;
			break;
		case GLFW.GLFW_KEY_BACKSPACE:
			code = BACKSPACE;
			break;
		case GLFW.GLFW_KEY_DELETE:
			code = DELETE;
			break;
		}
	}
	
	public char getChar() {
		return c;
	}
	
	public int getCode() {
		return code;
	}
}
