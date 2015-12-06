package com.nemezor.nemgine.graphics;

import java.awt.Dimension;
import java.awt.Point;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.main.NemgineLoader;
import com.nemezor.nemgine.misc.Side;

public class DisplayManager {
	
	private DisplayManager() {}

	public static void initialize(float fieldOfView, float zNear, float zFar) throws LWJGLException {
		if (Nemgine.getSide() == Side.CLIENT) {
			initialize();
			initializeOpenGL(fieldOfView, zNear, zFar);
		}
	}

	public static void initialize() throws LWJGLException {
		if (Nemgine.getSide() == Side.CLIENT) {
			ContextAttribs attribs = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);
			Dimension size = NemgineLoader.getSize();
			Point location = NemgineLoader.getLocation();
			Display.setDisplayMode(new DisplayMode((int) size.getWidth(), (int) size.getHeight()));
			Display.setTitle(NemgineLoader.getTitle());
			Display.setResizable(true);
			Display.setLocation((int) location.getX(), (int) location.getY());
			NemgineLoader.initializeOpenGL(false, attribs);
		}
	}

	public static void initializeOpenGL(float fieldOfView, float zNear, float zFar) {
		if (Nemgine.getSide() == Side.CLIENT) {
			GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
			GLHelper.FOV = fieldOfView;
			GLHelper.aspect = (float) Display.getWidth() / (float) Display.getHeight();
			GLHelper.zFar = zFar;
			GLHelper.zNear = zNear;
			GLHelper.updatePerspectiveProjection();
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}
	}

	private static void reinitializeOpenGL() {
		if (Nemgine.getSide() == Side.CLIENT) {
			GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
			GLHelper.aspect = (float) Display.getWidth() / (float) Display.getHeight();
			GLHelper.updatePerspectiveProjection();
		}
	}

	public static boolean closeRequested() {
		return NemgineLoader.isCloseRequested();
	}

	public static boolean resize() {
		if (Nemgine.getSide() == Side.CLIENT && Display.wasResized()) {
			reinitializeOpenGL();
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
			NemgineLoader.setTitle(newTitle);
		}
	}
}
