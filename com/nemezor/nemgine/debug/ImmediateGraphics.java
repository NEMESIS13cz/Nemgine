package com.nemezor.nemgine.debug;

import org.lwjgl.opengl.GL11;

public class ImmediateGraphics {
	
	private ImmediateGraphics() {}
	
	public static void vertex3f(float x, float y, float z) {
		if (ImmediateRender.enabled) {
			GL11.glVertex3f(x, y, z);
		}
	}
}
