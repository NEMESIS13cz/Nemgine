package com.nemezor.nemgine.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import com.nemezor.nemgine.misc.Registry;

public class FrameBuffer {

	public static final int TEXTURE_BUFFER = 1;
	public static final int DEPTH_BUFFER = 2;
	
	protected int id;
	protected int w;
	protected int h;
	protected int texture;
	protected int depth;
	protected int depthTex;
	
	public FrameBuffer(int id) {
		this.id = id;
	}
	
	protected void dispose() {
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
