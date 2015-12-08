package com.nemezor.nemgine.misc;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.Pbuffer;
import org.lwjgl.opengl.PixelFormat;

public class Platform {

	private static boolean initialized = false;
	
	public static void initialize(boolean headless) {
		if (initialized) {
			return;
		}
		try {
			Pbuffer gl = new Pbuffer(1, 1, new PixelFormat(), null);
			gl.makeCurrent();
			
			System.out.println(GL11.glGetString(GL11.GL_VERSION));
			
			gl.destroy();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
}
