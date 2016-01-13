package com.nemezor.nemgine.graphics;

import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.JFrame;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.misc.ErrorScreen;
import com.nemezor.nemgine.misc.Logger;
import com.nemezor.nemgine.misc.Platform;
import com.nemezor.nemgine.misc.Registry;
import com.nemezor.nemgine.misc.Side;

public class Loader {

	private static int appW;
	private static int appH;
	private static String appTitle;
	private static boolean initialized = false;
	private static boolean postinitialized = false;
	private static boolean isDone = false;
	private static boolean loaded = false;
	private static int logoShader = 0;
	private static int barShader = 0;
	private static int texture = 0;
	private static Matrix4f transformation;
	private static Matrix4f perspective;
	private static JFrame frame;
	private static Matrix4f textureTransformation;
	private static Matrix4f shaderTransformation;
	private static Matrix4f modelTransformation;
	private static long frameskip = Registry.ONE_SECOND_IN_MILLIS / Registry.LOADING_SCREEN_REFRESHRATE;
	private static long lastFrame = 0;
	
	protected static int shaderCounter = 0;
	protected static int modelCounter = 0;
	protected static int textureCounter = 0;
	protected static int shaderProgress = 0;
	protected static int modelProgress = 0;
	protected static int textureProgress = 0;
	
	public static void initialize(int appWidth, int appHeight, String title) {
		if (initialized) {
			return;
		}
		appW = appWidth;
		appH = appHeight;
		appTitle = title;
		
		if (Nemgine.getSide() == Side.SERVER) {
			initialized = true;
			return;
		}
		
		try {
			Display.setTitle(appTitle);
			Display.setResizable(false);
			frame = new JFrame();
			Canvas canvas = new Canvas();
			frame.getContentPane().add(canvas);
			frame.setUndecorated(true);
			frame.setTitle(appTitle);
			frame.pack();
			frame.setSize(Registry.LOADING_SCREEN_WIDTH, Registry.LOADING_SCREEN_HEIGHT);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
			Display.setParent(canvas);
			if (Nemgine.isInCompatibilityMode() && Platform.getOpenGLVersion() < Registry.OPENGL_OFFICIAL_SUPPORTED_VERSION) {
				Display.create(new PixelFormat());
			}else{
				ContextAttribs attributes = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);
				Display.create(new PixelFormat(), attributes);
			}
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		initialized = true;
	}
	
	public static void postInitialize() {
		if (postinitialized) {
			return;
		}
		if (Nemgine.getSide() == Side.SERVER) {
			postinitialized = true;
			update();
			return;
		}
		
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		GLHelper.aspect = (float) Display.getWidth() / (float) Display.getHeight();
		GLHelper.updatePerspectiveProjection();
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
		postinitialized = true;
		update();
		ShaderManager.generateGuiShaderIDs();
	}
	
	public static void finish() {
		if (isDone) {
			return;
		}
		if (Nemgine.getSide() == Side.SERVER) {
			isDone = true;
			return;
		}
		ShaderManager.dispose(logoShader);
		ShaderManager.dispose(barShader);
		TextureManager.dispose(texture);
		try {
			Display.setParent(null);
			Display.setDisplayMode(new DisplayMode(appW, appH));
		} catch (LWJGLException e) {}
		Display.setResizable(true);
		DisplayManager.reinitializeOpenGL();
		frame.dispose();
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
		DisplayManager.prepareRender();
		ModelManager.renderModel(ModelManager.getSquareModelID(), texture, logoShader, transformation, perspective, "transformation", "projection");
		ShaderManager.bindShader(barShader);
		ShaderManager.loadFloat(barShader, "progress", (float)textureProgress / (float)textureCounter);
		ModelManager.renderModel(ModelManager.getSquareModelID(), 0, barShader, textureTransformation, perspective, "transformation", "projection");
		ShaderManager.loadFloat(barShader, "progress", (float)shaderProgress / (float)shaderCounter);
		ModelManager.renderModel(ModelManager.getSquareModelID(), 0, barShader, shaderTransformation, perspective, "transformation", "projection");
		ShaderManager.loadFloat(barShader, "progress", (float)modelProgress / (float)modelCounter);
		ModelManager.renderModel(ModelManager.getSquareModelID(), 0, barShader, modelTransformation, perspective, "transformation", "projection");
		
		ModelManager.finishRendering();
		DisplayManager.finishRender();
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
	
	public static Dimension getSize() {
		return new Dimension(Display.getWidth(), Display.getHeight());
	}
	
	public static String getTitle() {
		return Display.getTitle();
	}
	
	public static boolean loading() {
		return postinitialized ? !isDone : false;
	}
}
