package com.nemezor.nemgine.graphics;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.nemezor.nemgine.exceptions.WindowException;
import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.misc.ErrorScreen;
import com.nemezor.nemgine.misc.Logger;
import com.nemezor.nemgine.misc.Platform;
import com.nemezor.nemgine.misc.Registry;
import com.nemezor.nemgine.misc.Side;

public class Loader {

	private static String appTitle;
	private static boolean initialized = false;
	private static boolean isDone = false;
	private static boolean loaded = false;
	private static int logoShader = 0;
	private static int barShader = 0;
	private static int texture = 0;
	private static Matrix4f transformation;
	private static Matrix4f perspective;
	private static Matrix4f textureTransformation;
	private static Matrix4f shaderTransformation;
	private static Matrix4f modelTransformation;
	private static long frameskip = Registry.ONE_SECOND_IN_MILLIS / Registry.LOADING_SCREEN_REFRESHRATE;
	private static long lastFrame = 0;
	private static long window;
	
	protected static int shaderCounter = 0;
	protected static int modelCounter = 0;
	protected static int textureCounter = 0;
	protected static int shaderProgress = 0;
	protected static int modelProgress = 0;
	protected static int textureProgress = 0;
	
	public static long initialize(String title) {
		if (initialized) {
			return Registry.INVALID;
		}
		appTitle = title;
		
		if (Nemgine.getSide() == Side.SERVER) {
			initialized = true;
			return Registry.INVALID;
		}
		
		Platform.setDefaultGLFWWindowConfigurations();
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);
		window = GLFW.glfwCreateWindow(Registry.LOADING_SCREEN_WIDTH, Registry.LOADING_SCREEN_HEIGHT, appTitle, MemoryUtil.NULL, MemoryUtil.NULL);
		if (window == MemoryUtil.NULL) {
			WindowException e = new WindowException(Registry.WINDOW_EXCEPTION_LOADING_SCREEN_INITIALIZATION_FAILED);
			e.printStackTrace();
			String stack = "";
			for (StackTraceElement el : e.getStackTrace()) {
				stack += el.toString() + "\n";
			}
			ErrorScreen.show(Registry.WINDOW_EXCEPTION_LOADING_SCREEN_INITIALIZATION_FAILED + "\n\n" + e.getLocalizedMessage() + "\n" + stack, true);
		}
		GLFWVidMode mode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		GLFW.glfwSetWindowPos(window, (mode.width() - Registry.LOADING_SCREEN_WIDTH) / 2, (mode.height() - Registry.LOADING_SCREEN_HEIGHT) / 2);
		GLFW.glfwMakeContextCurrent(window);
		GL.createCapabilities(Registry.OPENGL_FORWARD_COMPATIBLE);
		GLFW.glfwShowWindow(window);
		
		GL11.glViewport(0, 0, Registry.LOADING_SCREEN_WIDTH, Registry.LOADING_SCREEN_HEIGHT);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		ModelManager.initializeSquareModel();
		texture = TextureManager.loadMissingTextureAndLogo();
		logoShader = ShaderManager.loadLogoShaders();
		barShader = ShaderManager.loadProgressBarShaders();
		transformation = GLHelper.initTransformationMatrix(new Vector3f(0, 0.1f, 0), new Vector3f(90, 0, 0), new Vector3f(1, 0, 1));
		textureTransformation = GLHelper.initTransformationMatrix(new Vector3f(0, 0.3f, 0), new Vector3f(90, 0, 0), new Vector3f(0.8f, 0, 0.1f));
		shaderTransformation = GLHelper.initTransformationMatrix(new Vector3f(0, 0.5f, 0), new Vector3f(90, 0, 0), new Vector3f(0.8f, 0, 0.1f));
		modelTransformation = GLHelper.initTransformationMatrix(new Vector3f(0, 0.7f, 0), new Vector3f(90, 0, 0), new Vector3f(0.8f, 0, 0.1f));
		perspective = GLHelper.initOrthographicProjectionMatrix(-1, 1, 1, -1, -1, 1);

		initialized = true;
		update();
		
		ShaderManager.generateGuiShaderIDs();
		return GLFW.glfwGetCurrentContext();
	}
	
	public static void finish() {
		if (isDone) {
			return;
		}
		if (Nemgine.getSide() == Side.SERVER) {
			isDone = true;
			return;
		}
		
		GLFW.glfwHideWindow(window);
		ShaderManager.dispose(logoShader);
		ShaderManager.dispose(barShader);
		TextureManager.dispose(texture);
		isDone = true;
	}
	
	public static void loadDefaultResources() {
		if (loaded) {
			return;
		}
		if (Nemgine.getSide() == Side.SERVER) {
			loaded = true;
			return;
		}
		
		ShaderManager.loadGuiShaders();
		
		loaded = true;
	}
	
	private static void update() {
		if (lastFrame + frameskip > System.currentTimeMillis()) {
			return;
		}
		
		if (Nemgine.getSide() == Side.SERVER) {
			lastFrame = System.currentTimeMillis();
			return;
		}
		textureTransformation = GLHelper.initTransformationMatrix(new Vector3f(-0.8f + (0.8f * (float)textureProgress / (float)textureCounter), -0.3f, 0), new Vector3f(90, 0, 0), new Vector3f(0.8f * (float)textureProgress / (float)textureCounter, 0, 0.1f));
		shaderTransformation = GLHelper.initTransformationMatrix(new Vector3f(-0.8f + (0.8f * (float)shaderProgress / (float)shaderCounter), -0.5f, 0), new Vector3f(90, 0, 0), new Vector3f(0.8f * (float)shaderProgress / (float)shaderCounter, 0, 0.1f));
		modelTransformation = GLHelper.initTransformationMatrix(new Vector3f(-0.8f + (0.8f * (float)modelProgress / (float)modelCounter), -0.7f, 0), new Vector3f(90, 0, 0), new Vector3f(0.8f * (float)modelProgress / (float)modelCounter, 0, 0.1f));
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		ModelManager.renderModel(ModelManager.getSquareModelID(), texture, logoShader, transformation, perspective, "transformation", "projection");
		ShaderManager.bindShader(barShader);
		ShaderManager.loadFloat(barShader, "progress", (float)textureProgress / (float)textureCounter);
		ModelManager.renderModel(ModelManager.getSquareModelID(), 0, barShader, textureTransformation, perspective, "transformation", "projection");
		ShaderManager.loadFloat(barShader, "progress", (float)shaderProgress / (float)shaderCounter);
		ModelManager.renderModel(ModelManager.getSquareModelID(), 0, barShader, shaderTransformation, perspective, "transformation", "projection");
		ShaderManager.loadFloat(barShader, "progress", (float)modelProgress / (float)modelCounter);
		ModelManager.renderModel(ModelManager.getSquareModelID(), 0, barShader, modelTransformation, perspective, "transformation", "projection");

		ModelManager.finishRendering();
		GLFW.glfwSwapBuffers(window);
		GLFW.glfwPollEvents();
		lastFrame = System.currentTimeMillis();
	}
	
	protected static void loadingTexture(String name) {
		Logger.logSilently("Loading Texture: " + name);
		update();
	}
	
	protected static void textureLoaded() {
		textureProgress++;
		update();
	}
	
	protected static void loadingShader(String name) {
		Logger.logSilently("Loading Shader: " + name);
		update();
	}
	
	protected static void shaderLoaded() {
		shaderProgress++;
		update();
	}
	
	protected static void loadingModel(String name) {
		Logger.logSilently("Loading Model: " + name);
		update();
	}
	
	protected static void modelLoaded() {
		modelProgress++;
		update();
	}
	
	protected static void failedToLoadResource(String message) {
		ErrorScreen.show(message, true);
	}
	
	public static boolean loading() {
		return initialized ? !isDone : false;
	}
}
