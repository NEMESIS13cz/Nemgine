package com.nemezor.nemgine.graphics.util;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.vector.Matrix4f;

import com.nemezor.nemgine.exceptions.WindowException;
import com.nemezor.nemgine.graphics.GLHelper;
import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.misc.Color;
import com.nemezor.nemgine.misc.Platform;
import com.nemezor.nemgine.misc.Registry;

public class Display {
	
	private int status;
	private float FOV;
	private float width, height;
	private float zNear, zFar;
	private String title;
	private Matrix4f persp;
	private boolean init = false;
	private long window;
	private boolean invalid = false;
	private volatile boolean resized = false;
	private GLFWWindowSizeCallback sizeCallback;
	private boolean wireframe = false;
	
	public Display(int status) {
		this.status = status;
		persp = new Matrix4f();
		title = Nemgine.getApplicationName();
	}
	
	public void initialize(float fieldOfView, int w, int h, float zn, float zf, boolean resizable, long share) {
		if (init) {
			return;
		}
		FOV = fieldOfView;
		zNear = zn;
		zFar = zf;
		width = w;
		height = h;
		
		Platform.setDefaultGLFWWindowConfigurations();
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, resizable ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		
		window = GLFW.glfwCreateWindow(w, h, title, MemoryUtil.NULL, share == Registry.INVALID ? Nemgine.getOpenGLContext() : share);
		if (window == MemoryUtil.NULL) {
			new WindowException(Registry.WINDOW_EXCEPTION_FAILED_TO_CREATE).printStackTrace();
			return;
		}
		GLFWVidMode mode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		GLFW.glfwSetWindowPos(window, (mode.width() - w) / 2, (mode.height() - h) / 2);
		
		sizeCallback = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long w, int wi, int he) {
				width = wi;
				height = he;
				resized = true;
			}
		};
		
		GLFW.glfwSetWindowSizeCallback(window, sizeCallback);
		GLFW.glfwMakeContextCurrent(window);
		GL.createCapabilities(Registry.OPENGL_FORWARD_COMPATIBLE);
		GLFW.glfwShowWindow(window);
		IntBuffer b1 = BufferUtils.createIntBuffer(1);
		IntBuffer b2 = BufferUtils.createIntBuffer(1);
		GLFW.glfwGetFramebufferSize(window, b1, b2);
		GL11.glViewport(0, 0, b1.get(), b2.get());
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		resize();
		init = true;
	}
	
	public boolean closeRequested() {
		return GLFW.glfwWindowShouldClose(window) == GLFW.GLFW_TRUE;
	}
	
	public void setSize(int w, int h) {
		width = w;
		height = h;
		if (init) {
			GLFW.glfwSetWindowSize(window, (int)width, (int)height);
			resize();
		}
	}
	
	public void setStatus(int s) {
		if (status != Registry.INVALID) {
			return;
		}
		status = s;
	}
	
	public int getStatus() {
		return status;
	}
	
	public Matrix4f getPerspectiveProjectionMatrix() {
		return persp;
	}
	
	public void prepareRender() {
		if (resized) {
			resize();
			resized = false;
		}
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}
	
	public void finishRender() {
		GLFW.glfwSwapBuffers(window);
		GLFW.glfwPollEvents();
	}
	
	public void dispose() {
		invalid = true;
		Callbacks.glfwReleaseCallbacks(window);
		GLFW.glfwDestroyWindow(window);
	}
	
	public void changeTitle(String newTitle) {
		title = newTitle;
		GLFW.glfwSetWindowTitle(window, newTitle);
	}
	
	public void fill(Color c) {
		GL11.glClearColor(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
	}
	
	public int getWidth() {
		return (int)width;
	}
	
	public int getHeight() {
		return (int)height;
	}
	
	public long getGLFWId() {
		return window;
	}
	
	public boolean isInvalid() {
		return invalid;
	}
	
	public void setWireframeRender(boolean on) {
		if (wireframe != on) {
			if (on) {
				GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			}else{
				GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
			}
			wireframe = on;
		}
	}
	
	private void resize() {
		IntBuffer b1 = BufferUtils.createIntBuffer(1);
		IntBuffer b2 = BufferUtils.createIntBuffer(1);
		GLFW.glfwGetFramebufferSize(window, b1, b2);
		GL11.glViewport(0, 0, b1.get(), b2.get());
		persp = GLHelper.initPerspectiveProjectionMatrix(FOV, width, height, zNear, zFar);
	}
}
