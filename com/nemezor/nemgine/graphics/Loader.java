package com.nemezor.nemgine.graphics;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.misc.Logger;
import com.nemezor.nemgine.misc.Registry;

public class Loader {

	private static int appW;
	private static int appH;
	private static String appTitle;
	private static boolean initialized = false;
	private static boolean postinitialized = false;
	private static boolean isDone = false;
	private static boolean exit = false;
	private static int logoShader = 0;
	private static int barShader = 0;
	private static int texture = 0;
	private static Matrix4f transformation;
	private static Matrix4f perspective;
	private static JFrame frame;
	private static Matrix4f textureTransformation;
	private static Matrix4f shaderTransformation;
	private static Matrix4f modelTransformation;
	
	protected static int shaderCounter = 0;
	protected static int modelCounter = 0;
	protected static int textureCounter = 0;
	protected static int shaderProgress = 0;
	protected static int modelProgress = 0;
	protected static int textureProgress = 0;
	
	public static void initialize(int appWidth, int appHeight, String title, ContextAttribs attributes) {
		if (initialized) {
			return;
		}
		appW = appWidth;
		appH = appHeight;
		appTitle = title;
		
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
			Display.create(new PixelFormat(), attributes);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		initialized = true;
	}
	
	public static void postInitialize() {
		if (postinitialized) {
			return;
		}
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		GLHelper.aspect = (float) Display.getWidth() / (float) Display.getHeight();
		GLHelper.updatePerspectiveProjection();
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		ModelManager.initializeSquareModel();
		texture = TextureManager.loadMissingTextureAndLogo();
		logoShader = ShaderManager.loadLogoShaders();
		barShader = ShaderManager.loadProgressBarShaders();
		transformation = GLHelper.initTransformationMatrix(new Vector3f(0, 0.1f, 0), new Vector3f(90, 0, 0), new Vector3f(1, 0, 1));
		textureTransformation = GLHelper.initTransformationMatrix(new Vector3f(0, -0.3f, 0), new Vector3f(90, 0, 0), new Vector3f(0.8f, 0, 0.1f));
		shaderTransformation = GLHelper.initTransformationMatrix(new Vector3f(0, -0.5f, 0), new Vector3f(90, 0, 0), new Vector3f(0.8f, 0, 0.1f));
		modelTransformation = GLHelper.initTransformationMatrix(new Vector3f(0, -0.7f, 0), new Vector3f(90, 0, 0), new Vector3f(0.8f, 0, 0.1f));
		perspective = GLHelper.initOrthographicProjectionMatrix(-1, 1, 1, -1, -1, 1);
		postinitialized = true;
		update();
	}
	
	public static void finish() {
		if (isDone) {
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
		frame.dispose();
		isDone = true;
	}
	
	private static void update() {
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
	}
	
	protected static void loadingTexture(String name) {
		Logger.log("Loading Texture: " + name);
		update();
	}
	
	protected static void textureLoaded() {
		textureProgress++;
		update();
	}
	
	protected static void loadingShader(String name) {
		Logger.log("Loading Shader: " + name);
		update();
	}
	
	protected static void shaderLoaded() {
		shaderProgress++;
		update();
	}
	
	protected static void loadingModel(String name) {
		Logger.log("Loading Model: " + name);
		update();
	}
	
	protected static void modelLoaded() {
		modelProgress++;
		update();
	}
	
	protected static void failedToLoadResource(String message) {
		JFrame frame = new JFrame();
		
		WindowListener action = new WindowListener() {
			
			public void windowOpened(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {
				frame.dispose();
				exit = true;
			}
			public void windowClosed(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
		};
		
		JTextArea text = new JTextArea(Registry.NEMGINE_EXCEPTION_SHUTDOWN + "\n\n>>> " + message + "\n\n\n\n" + Registry.NEMGINE_SHUTDOWN_EXIT + Registry.INVALID + "\n\n\n" + (Logger.isContained() ? "" : Registry.NEMGINE_EXCEPTION_SHUTDOWN_MORE));
		text.setEditable(false);
		
		frame.setResizable(false);
		frame.getContentPane().add(text);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(action);
		frame.pack();
		frame.setSize(new Dimension(Registry.ERROR_SCREEN_WIDTH, Registry.ERROR_SCREEN_HEIGHT));
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		while (!exit) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		Nemgine.shutDown();
		Nemgine.exit(Registry.INVALID);
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
