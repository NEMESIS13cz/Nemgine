package com.nemezor.nemgine.graphics.util;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import com.nemezor.nemgine.misc.Registry;

public class FrameBuffer {

	public static final int TEXTURE_BUFFER = 1;
	public static final int DEPTH_BUFFER = 2;
	
	public int id;
	public int w;
	public int h;
	public int texture;
	public int depth;
	public int depthTex;
	
	public FrameBuffer(int id) {
		this.id = id;
	}
	
	public void dispose() {
		GL30.glDeleteFramebuffers(id);
		if (texture != Registry.INVALID) {
			GL11.glDeleteTextures(texture);
		}
		if (depthTex != Registry.INVALID) {
			GL11.glDeleteTextures(depthTex);
		}
		if (depth != Registry.INVALID) {
			GL30.glDeleteRenderbuffers(depth);
		}
	}
}
