package com.nemezor.nemgine.graphics;

import java.lang.reflect.Method;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.vector.Matrix4f;

import com.nemezor.nemgine.exceptions.WindowException;
import com.nemezor.nemgine.graphics.util.GLResourceEvent;
import com.nemezor.nemgine.graphics.util.LoaderSegment;
import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.misc.Color;
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
	private static long frameskip = Registry.ONE_SECOND_IN_MILLIS / Registry.LOADING_SCREEN_REFRESHRATE;
	private static long lastFrame = 0;
	private static long window;
	private static Matrix4f projection = GLHelper.initBasicOrthographicProjectionMatrix();
	private static Matrix4f textProjection = GLHelper.initOrthographicProjectionMatrix(0, Registry.LOADING_SCREEN_WIDTH, 0, Registry.LOADING_SCREEN_HEIGHT, 0, 1);
	private static LoaderSegment segment;
	
	protected static int shaderCounter = 0;
	protected static int modelCounter = 0;
	protected static int textureCounter = 0;
	protected static int fontCounter = 0;
	
	protected static boolean silent = false;
	
	private Loader() {}
	
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
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		ModelManager.initializeSquareModel();
		FontManager.initializeDefaultFont();
		texture = TextureManager.loadMissingTextureAndLogo();
		logoShader = ShaderManager.loadLogoShaders();
		barShader = ShaderManager.loadProgressBarShaders();
		ShaderManager.bindShader(logoShader);
		ShaderManager.loadMatrix4(logoShader, "projection", projection);
		ShaderManager.loadMatrix4(logoShader, "transformation", new Matrix4f());
		ShaderManager.loadVector4(logoShader, "color", new Color(0xFFFFFFFF).getColorAsVector());
		ShaderManager.bindShader(barShader);
		ShaderManager.loadMatrix4(barShader, "projection", projection);
		ShaderManager.loadMatrix4(barShader, "transformation", new Matrix4f());
		ShaderManager.loadFloat(barShader, "progress", 0);
		ShaderManager.unbindShader();
		
		initialized = true;
		update();
		
		ShaderManager.generateDefaultShaderIDs();
		
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
	
	public static void beginLoadSequence(Method resources, Object instance) {
		if (loaded) {
			return;
		}
		loaded = true;
		LoaderSegment textures = new LoaderSegment(Registry.LOADING_PROGRESS_GFX_TEXTURES, textureCounter * 2);
		LoaderSegment models = new LoaderSegment(Registry.LOADING_PROGRESS_GFX_MODELS, modelCounter * 5);
		LoaderSegment shaders = new LoaderSegment(Registry.LOADING_PROGRESS_GFX_SHADERS, shaderCounter * 4);
		LoaderSegment fonts = new LoaderSegment(Registry.LOADING_PROGRESS_GFX_FONTS, fontCounter * 6);
		
		segment = new LoaderSegment(Registry.LOADING_PROGRESS_GFX_RESOURCES, 0);
		segment.addSubsegment(textures);
		segment.addSubsegment(models);
		segment.addSubsegment(shaders);
		segment.addSubsegment(fonts);
		try {
			resources.invoke(instance, GLResourceEvent.LOAD_TEXTURES);
			segment.nextSubsegment();
			resources.invoke(instance, GLResourceEvent.LOAD_MODELS);
			segment.nextSubsegment();
			ShaderManager.loadDefaultShaders();
			resources.invoke(instance, GLResourceEvent.LOAD_SHADERS);
			segment.nextSubsegment();
			resources.invoke(instance, GLResourceEvent.LOAD_FONTS);
			segment = null;
		} catch (Exception e) {
			Logger.log(Registry.NEMGINE_NAME, Registry.LOADING_RESOURCES_LOAD_FAILED, false);
			e.printStackTrace();
            GLFW.glfwTerminate();
			System.exit(Registry.INVALID);
		}
	}
	
	private static void update() {
		if (lastFrame + frameskip > System.currentTimeMillis()) {
			return;
		}
		
		if (Nemgine.getSide() == Side.SERVER) {
			lastFrame = System.currentTimeMillis();
			return;
		}
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		ShaderManager.bindShader(logoShader);
		TextureManager.bindTexture(texture);
		Tessellator.start(Tessellator.QUADS);
		
		Tessellator.addVertex(0, 0, 0);
		Tessellator.addTexCoord(0, 0);
		Tessellator.addVertex(1, 0, 0);
		Tessellator.addTexCoord(1, 0);
		Tessellator.addVertex(1, 1, 0);
		Tessellator.addTexCoord(1, 1);
		Tessellator.addVertex(0, 1, 0);
		Tessellator.addTexCoord(0, 1);

		Tessellator.finish();
		
		if (segment != null) {
			renderSegment(segment, 0);
		}
		
		ModelManager.finishRendering();
		
		GLFW.glfwSwapBuffers(window);
		GLFW.glfwPollEvents();
		lastFrame = System.currentTimeMillis();
	}
	
	private static void renderSegment(LoaderSegment segment, float offset) {
		TextureManager.unbindTexture();
		ShaderManager.bindShader(barShader);
		float progress = segment.getProgress();
		ShaderManager.loadFloat(barShader, "progress", progress);
		progress *= 0.8f;
		progress += 0.1f;
		Tessellator.start(Tessellator.LINES);

		Tessellator.addVertex(0.1f, 0.6f + offset, 0);
		Tessellator.addVertex(0.9f, 0.6f + offset, 0);
		Tessellator.addVertex(0.1f, 0.68f + offset, 0);
		Tessellator.addVertex(0.9f, 0.68f + offset, 0);
		Tessellator.addVertex(0.9f, 0.6f + offset, 0);
		Tessellator.addVertex(0.9f, 0.68f + offset, 0);
		Tessellator.addVertex(0.1f, 0.6f + offset, 0);
		Tessellator.addVertex(0.1f, 0.68f + offset, 0);
		
		Tessellator.finish();
		Tessellator.start(Tessellator.QUADS);
		
		Tessellator.addVertex(0.1f, 0.6f + offset, 0);
		Tessellator.addVertex(progress, 0.6f + offset, 0);
		Tessellator.addVertex(progress, 0.68f + offset, 0);
		Tessellator.addVertex(0.1f, 0.68f + offset, 0);
		
		Tessellator.finish();
		
		FontManager.drawString(FontManager.getDefaultFontID(), 0.098f * Registry.LOADING_SCREEN_WIDTH, (0.495f + offset) * Registry.LOADING_SCREEN_HEIGHT, segment.getLabel(), Registry.LOADING_SCREEN_FONT_COLOR, new Matrix4f(), textProjection);
		
		if (segment.getSubsegment() != null) {
			renderSegment(segment.getSubsegment(), offset + 0.14f);
		}
	}
	
	protected static void loadingTexture(String name) {
		Logger.log(Registry.LOADING_SCREEN_NAME, "Loading Texture: " + name);
		segment.setLabel(Registry.LOADING_PROGRESS_GFX_TEXTURES + " (" + name + ")");
		segment.stepProgress();
		update();
	}
	
	protected static void textureLoaded() {
		segment.stepProgress();
		update();
	}
	
	protected static void loadingShader(String name) {
		Logger.log(Registry.LOADING_SCREEN_NAME, "Loading Shader: " + name);
		segment.setLabel(Registry.LOADING_PROGRESS_GFX_SHADERS + " (" + name + ")");
		segment.stepProgress();
		update();
	}
	
	protected static void shaderLoaded() {
		segment.stepProgress();
		update();
	}
	
	protected static void loadingModel(String name) {
		Logger.log(Registry.LOADING_SCREEN_NAME, "Loading Model: " + name);
		segment.setLabel(Registry.LOADING_PROGRESS_GFX_MODELS + " (" + name + ")");
		segment.stepProgress();
		update();
	}
	
	protected static void modelLoaded() {
		segment.stepProgress();
		update();
	}
	
	protected static void loadingFont(String name) {
		Logger.log(Registry.LOADING_SCREEN_NAME, "Loading Font: " + name);
		segment.setLabel(Registry.LOADING_PROGRESS_GFX_FONTS + " (" + name + ")");
		segment.stepProgress();
		update();
	}
	
	protected static void fontLoaded() {
		segment.stepProgress();
		update();
	}
	
	protected static void stepLoader() {
		segment.stepProgress();
		update();
	}
	
	protected static void failedToLoadResource(String message) {
		ErrorScreen.show(message, true);
	}
	
	public static boolean loading() {
		return initialized ? isDone ? false : !silent : false;
	}
}
