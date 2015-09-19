package com.nemezor.nemgine.debug;

import java.awt.Dimension;
import java.awt.Point;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import com.nemezor.nemgine.main.NemgineLoader;

public class ImmediateRender {

	protected static volatile boolean enabled = false;
	private static boolean isImmediate = false;

	private ImmediateRender() {}

	public static void setup() {
		isImmediate = true;
		try {
			Dimension size = NemgineLoader.getSize();
			Point location = NemgineLoader.getLocation();
			Display.setDisplayMode(new DisplayMode((int) size.getWidth(), (int) size.getHeight()));
			Display.setTitle(NemgineLoader.getTitle());
			Display.setResizable(true);
			Display.setLocation((int) location.getX(), (int) location.getY());
			NemgineLoader.initializeOpenGL(true, null);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		initializeOpenGL();
		enabled = true;
	}

	private static void initializeOpenGL() {
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective(70.0f, (float) Display.getWidth() / (float) Display.getHeight(), 0.002f, 1000f);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glEnable(GL11.GL_COLOR);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
	}

	private static void reinitializeOpenGL() {
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective(70.0f, (float) Display.getWidth() / (float) Display.getHeight(), 0.002f, 1000f);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
	}

	public static boolean closeRequested() {
		return NemgineLoader.isCloseRequested();
	}

	public static void prepareRender() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	public static void finishRender() {
		Display.update();
	}

	public static void dispose() {
		enabled = false;
		Display.destroy();
	}

	public static void resize() {
		if (Display.wasResized()) {
			reinitializeOpenGL();
		}
	}

	public static synchronized boolean isRenderModeImmediate() {
		return isImmediate;
	}
}
