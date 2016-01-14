package com.nemezor.nemgine.graphics;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.misc.Color;
import com.nemezor.nemgine.misc.Side;

public class DisplayManager {
	
	private DisplayManager() {}

	public static void setOpenGLConfiguration(float fieldOfView, float zNear, float zFar) {
		GLHelper.FOV = fieldOfView;
		GLHelper.zFar = zFar;
		GLHelper.zNear = zNear;
	}

	protected static void reinitializeOpenGL() {
		if (Nemgine.getSide() == Side.CLIENT) {
			GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
			GLHelper.aspect = (float) Display.getWidth() / (float) Display.getHeight();
			GLHelper.updatePerspectiveProjection();
		}
	}

	public static boolean closeRequested() {
		return Display.isCloseRequested();
	}

	public static boolean resize() {
		if (Nemgine.getSide() == Side.CLIENT && Display.wasResized()) {
			reinitializeOpenGL();
			GuiManager.resizeActiveGuis();
			return true;
		}
		return false;
	}

	public static void dispose() {
		if (Nemgine.getSide() == Side.CLIENT) {
			Display.destroy();
		}
	}

	public static void prepareRender() {
		if (Nemgine.getSide() == Side.CLIENT) {
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		}
	}

	public static void finishRender() {
		if (Nemgine.getSide() == Side.CLIENT) {
			Display.update();
		}
	}
	
	public static void changeTitle(String newTitle) {
		if (Nemgine.getSide() == Side.CLIENT) {
			Display.setTitle(newTitle);
		}
	}
	
	public static void fillDisplay(Color c) {
		if (Nemgine.getSide().isClient()) {
			GL11.glClearColor(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
		}
	}
}
