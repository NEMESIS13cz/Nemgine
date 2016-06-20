package com.nemezor.nemgine.graphics;

import java.awt.Dimension;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

import com.nemezor.nemgine.graphics.util.Display;
import com.nemezor.nemgine.graphics.util.FrameBuffer;
import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.misc.Registry;
import com.nemezor.nemgine.misc.Side;

public class FrameBufferManager {
	
	private static HashMap<Integer, FrameBuffer> buffers = new HashMap<Integer, FrameBuffer>();
	private static ArrayList<Integer> currentBufferTex = new ArrayList<Integer>();
	private static int bufferCounter = 0;
	private static int currentBuffer = 0;
	private static FrameBuffer currentBufferData = null;
	
	protected static boolean inFrameBuffer = false;

	private FrameBufferManager() {}

	public static synchronized int generateFrameBuffers() {
		if (Nemgine.getSide() == Side.SERVER) {
			return Registry.INVALID;
		}
		bufferCounter++;
		buffers.put(bufferCounter, new FrameBuffer(Registry.INVALID));
		return bufferCounter;
	}
	
	public static void bindFrameBuffer(int id) {
		if (currentBuffer == id || currentBufferTex.contains(id)) {
			return;
		}
		FrameBuffer buffer = buffers.get(id);
		if (buffer == null) {
			return;
		}
		currentBufferData = buffer;
		currentBuffer = id;
		inFrameBuffer = true;
		TextureManager.unbindTexture();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, buffer.id);
		GL11.glViewport(0, 0, buffer.w, buffer.h);
		GL11.glClear((buffer.texture != Registry.INVALID ? GL11.GL_COLOR_BUFFER_BIT : 0) | (buffer.depth != Registry.INVALID ? GL11.GL_DEPTH_BUFFER_BIT : 0));
	}
	
	public static void unbindFrameBuffer(Display window) {
		if (currentBuffer == 0 || Nemgine.getSide() == Side.SERVER) {
			return;
		}
		currentBuffer = 0;
		currentBufferData = null;
		inFrameBuffer = false;
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
	}
	
	public static void bindFrameBufferTexture(int id, int texture) {
		if (currentBuffer == id || currentBufferTex.contains(id)) {
			return;
		}
		FrameBuffer buffer = buffers.get(id);
		if (buffer == null || buffer.id == Registry.INVALID) {
			return;
		}
		int texId;
		if (texture == FrameBuffer.DEPTH_BUFFER) {
			texId = buffer.depthTex;
		}else if (texture == FrameBuffer.TEXTURE_BUFFER) {
			texId = buffer.texture;
		}else{
			return;
		}
		currentBufferTex.add(id);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
	}
	
	public static void unbindFrameBufferTexture() {
		if (currentBufferTex.size() == 0 || Nemgine.getSide() == Side.SERVER) {
			return;
		}
		currentBufferTex.clear();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	public static void dispose(int id) {
		if (currentBuffer == id || Nemgine.getSide() == Side.SERVER) {
			return;
		}
		FrameBuffer buffer = buffers.get(id);
		if (buffer == null) {
			return;
		}
		if (buffer.id != Registry.INVALID) {
			buffer.dispose();
		}
		buffers.remove(id);
	}
	
	public static Dimension getFrameBufferResolution(int id) {
		if (Nemgine.getSide() == Side.SERVER) {
			return new Dimension(Registry.INVALID, Registry.INVALID);
		}
		if (currentBuffer == id) {
			return new Dimension(currentBufferData.w, currentBufferData.h);
		}
		FrameBuffer buffer = buffers.get(id);
		if (buffer == null || buffer.id == Registry.INVALID) {
			return null;
		}
		return new Dimension(buffer.w, buffer.h);
	}
	
	public static void disposeAll() {
		if (Nemgine.getSide() == Side.SERVER) {
			return;
		}
		Iterator<Integer> keys = buffers.keySet().iterator();
		
		while (keys.hasNext()) {
			FrameBuffer buffer = buffers.get(keys.next());
			if (buffer.id != Registry.INVALID) {
				buffer.dispose();
			}
		}
		buffers.clear();
	}
	
	public static boolean initializeFrameBuffer(Display window, int id, int width, int height, boolean textureBuffer, boolean depthTextureBuffer, boolean depthBuffer) {
		if (currentBuffer == id) {
			return false;
		}
		FrameBuffer buffer = buffers.get(id);
		if (buffer == null || buffer.id != Registry.INVALID) {
			return false;
		}
		int glId = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, glId);
		int texture = Registry.INVALID;
		int textureDepth = Registry.INVALID;
		int depth = Registry.INVALID;
		if (textureBuffer) {
			texture = GL11.glGenTextures();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
			GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, texture, 0);
		}
		if (depthTextureBuffer) {
			textureDepth = GL11.glGenTextures();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureDepth);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, width, height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer)null);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_FUNC, GL11.GL_LEQUAL);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_MODE, GL14.GL_COMPARE_R_TO_TEXTURE);
			GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, textureDepth, 0);
		}
		if (depthBuffer) {
			depth = GL30.glGenRenderbuffers();
			GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depth);
			GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_DEPTH_COMPONENT, width, height);
			GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, depth);
		}
		if (textureBuffer) {
			GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
		}else{
			GL11.glDrawBuffer(GL11.GL_NONE);
			GL11.glReadBuffer(GL11.GL_NONE);
		}
		if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
			GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
			return false;
		}
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
		buffer.id = glId;
		buffer.texture = texture;
		buffer.depth = depth;
		buffer.depthTex = textureDepth;
		buffer.w = width;
		buffer.h = height;
		return true;
	}
}
