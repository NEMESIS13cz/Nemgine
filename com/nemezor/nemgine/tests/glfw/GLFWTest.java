package com.nemezor.nemgine.tests.glfw;

import java.awt.Font;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.nemezor.nemgine.debug.DebugColorizer;
import com.nemezor.nemgine.graphics.DisplayManager;
import com.nemezor.nemgine.graphics.FontManager;
import com.nemezor.nemgine.graphics.GLHelper;
import com.nemezor.nemgine.graphics.ModelManager;
import com.nemezor.nemgine.graphics.ShaderManager;
import com.nemezor.nemgine.graphics.Tessellator;
import com.nemezor.nemgine.graphics.TextureManager;
import com.nemezor.nemgine.graphics.util.Camera;
import com.nemezor.nemgine.graphics.util.Display;
import com.nemezor.nemgine.graphics.util.GLResourceEvent;
import com.nemezor.nemgine.graphics.util.OpenGLResources;
import com.nemezor.nemgine.input.Keyboard;
import com.nemezor.nemgine.input.Mouse;
import com.nemezor.nemgine.main.Application;
import com.nemezor.nemgine.main.IMainRenderLoop;
import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.misc.Color;
import com.nemezor.nemgine.misc.Platform;

public class GLFWTest implements IMainRenderLoop {

	int windowID;
	Display window;
	int windowID2;
	Display window2;
	private int shader;
	private int logoShader;
	private int model;
	private int logo;
	private int angle = 330 * 15;
	private int testTexture;
	private int font;
	
	private DebugColorizer colorizer = new DebugColorizer(0);
	private Color currColor = new Color(0xFFFFFFFF);
	private Camera cam;
	
	@Application(name="GLFW Test", path="tests/glfw", contained=true)
	public void entry() {
		int thread = Nemgine.generateThreads("Render", true);
		Nemgine.bindRenderLoop(thread, this);
		Nemgine.startThread(thread);
		cam = new Camera(new Vector3f(0, 40, 0), new Vector3f());
		Nemgine.printThreadKeepUpWarnings(false);
	}
	
	@OpenGLResources
	public void load(GLResourceEvent e) {
		if (e == GLResourceEvent.GENERATE_IDS) {
			
			shader = ShaderManager.generateShaders();
			logoShader = ShaderManager.generateShaders();
			model = ModelManager.generateModels();
			logo = ModelManager.generateModels();
			testTexture = TextureManager.generateTextures();
			font = FontManager.generateFonts();
			
		}else if (e == GLResourceEvent.LOAD_FONTS) {
			
			FontManager.initializeFont(font, "Monospaced", Font.PLAIN, 10);
			
		}else if (e == GLResourceEvent.LOAD_MODELS) {
			
			ModelManager.initializeModel(model, "test2.obj");
			ModelManager.initializeModel(logo, "nemgine.obj");
			
		}else if (e == GLResourceEvent.LOAD_SHADERS) {
			
			ShaderManager.initializeShader(shader, "com/nemezor/nemgine/tests/dragon.vert", 
												   "com/nemezor/nemgine/tests/dragon.frag", 
												   new String[] {"projection", "transformation", "lightVectorIn", "lightColorIn"}, 
												   new String[] {"position", "normal"}, new int[] {0, 2});
			ShaderManager.initializeShader(logoShader, "com/nemezor/nemgine/tests/logo.vert", 
												   "com/nemezor/nemgine/tests/logo.frag", 
												   new String[] {"projection", "transformation"}, 
												   new String[] {"position"}, new int[] {0});
			
		}else if (e == GLResourceEvent.LOAD_TEXTURES) {
			
			TextureManager.initializeTextureFile(testTexture, "com/nemezor/nemgine/tests/test_texture.png");
			
		}
	}
	
	public static void main(String[] args) {
		Nemgine.start(args, GLFWTest.class);
	}

	@Override
	public void render() {
		DisplayManager.switchDisplay(windowID);
		if (window.closeRequested()) {
			Nemgine.shutDown();
		}
		window.prepareRender();
		
		window.fill(new Color(0x000000FF));
		
		Matrix4f transform = GLHelper.initTransformationMatrix(cam, new Vector3f(0, -5, -25), new Vector3f((float)Math.PI / 18, 0, (float)Math.PI / -18), new Vector3f(0.1f, 0.1f, 0.1f));
		Matrix4f logoTransform = GLHelper.initTransformationMatrix(cam, new Vector3f(-15, 10, -30), new Vector3f((float)Math.toRadians(15 + angle), (float)Math.toRadians(25 + angle), (float)Math.toRadians(15)), new Vector3f(1, 1, 1));
		/*
		ShaderManager.bindShader(shader);
		ShaderManager.loadVector4(shader, "lightColorIn", (currColor = colorizer.getNext(currColor)).getColorAsVector());
		ShaderManager.unbindShader();*/

		transform.translate(new Vector3f(50000, 0, 0));
		transform.rotate((float)Math.toRadians(angle / 15.0f), new Vector3f(0, 1, 0));
		transform.translate(new Vector3f(-50000, 0, 0));
		ModelManager.renderModel(model, 0, shader, transform, window.getPerspectiveProjectionMatrix(), "transformation", "projection");

		//ModelManager.renderModel(logo, 0, logoShader, logoTransform, window.getPerspectiveProjectionMatrix(), "transformation", "projection");
		
		FontManager.drawString(FontManager.getDefaultFontID(), 20, 5, (Platform.getUsedMemory() / 1048576) + "/" + (Platform.getAllocatedMemory() / 1048576) + "MB", new Color(0xFFFFFFFF), new Matrix4f(), GLHelper.initOrthographicProjectionMatrix(0, window.getWidth(), 0, window.getHeight(), 0, 1));
		
		ModelManager.finishRendering();
		angle++;
		
		handleInput();
		
		window.finishRender();/*
		if (!window2.isInvalid()) {
			DisplayManager.switchDisplay(windowID2);
			window2.prepareRender();
			
			window2.fill(new Color(0x0000FFFF));
			
			ShaderManager.bindShader(shader);
			ShaderManager.loadVector4(shader, "lightColorIn", currColor.invert().getColorAsVector());
			ShaderManager.unbindShader();
			
			ModelManager.renderModel(model, 0, shader, transform, window2.getPerspectiveProjectionMatrix(), "transformation", "projection");
			ModelManager.renderModel(logo, 0, logoShader, logoTransform, window2.getPerspectiveProjectionMatrix(), "transformation", "projection");
			
			ShaderManager.bindShader(ShaderManager.getTextureShaderID());
			ShaderManager.loadMatrix4(ShaderManager.getTextureShaderID(), "projection", GLHelper.initBasicOrthographicProjectionMatrix());
			ShaderManager.loadMatrix4(ShaderManager.getTextureShaderID(), "transformation", new Matrix4f());
			ShaderManager.loadVector4(ShaderManager.getTextureShaderID(), "color", new Color(0xFFFFFF80).getColorAsVector());
			TextureManager.bindTexture(testTexture);
			GLHelper.enableBlending();
			Tessellator.start(Tessellator.QUADS);
			
			Tessellator.addVertex(0, 0, 0);
			Tessellator.addTexCoord(0, 0);
			Tessellator.addVertex(0.5f, 0, 0);
			Tessellator.addTexCoord(1, 0);
			Tessellator.addVertex(0.5f, 0.5f, 0);
			Tessellator.addTexCoord(1, 1);
			Tessellator.addVertex(0, 0.5f, 0);
			Tessellator.addTexCoord(0, 1);

			Tessellator.finish();
			GLHelper.disableBlending();
			ModelManager.finishRendering();
			
			window2.finishRender();
			if (window2.closeRequested()) {
				DisplayManager.dispose(windowID2);
			}
		}*/
	}

	@Override
	public void setUpRender() {
		windowID = DisplayManager.generateDisplays();
		window = DisplayManager.initializeDisplay(windowID, 70.0f, 1280, 720, 0.1f, 1500000.0f, true);
		//windowID2 = DisplayManager.generateDisplays();
		//window2 = DisplayManager.initializeDisplay(windowID2, 70.0f, 600, 400, 0.002f, 500.0f, true);
		//window2.changeTitle(Nemgine.getApplicationName() + " | Window: 2");

		ShaderManager.bindShader(shader);
		ShaderManager.loadMatrix4(shader, "projection", window.getPerspectiveProjectionMatrix());
		ShaderManager.loadMatrix4(shader, "transformation", new Matrix4f());
		ShaderManager.loadVector3(shader, "lightVectorIn", new Vector3f(-50, 100, 100));
		ShaderManager.loadVector4(shader, "lightColorIn", new Color(0xE4EAFFFF).getColorAsVector());
		ShaderManager.bindShader(logoShader);
		ShaderManager.loadMatrix4(logoShader, "projection", window.getPerspectiveProjectionMatrix());
		ShaderManager.loadMatrix4(logoShader, "transformation", new Matrix4f());
		ShaderManager.unbindShader();
	}

	@Override
	public void cleanUpRender() {
		Nemgine.exit(0);
	}

	@Override
	public void updateRenderSecond(int frames, long averageInterval) {
		window.changeTitle(Nemgine.getApplicationName() + " | FPS: " + frames);
	}

	@Override
	public long getRenderSleepInterval() {
		return 50;
	}

	@Override
	public int getRenderFrameskipTreshold() {
		return 20;
	}

	////////////////////////////////////
	/**
	 * Dirty stuff I found in one of my old projects, too lazy to recode it for now
	 */
	
	public static final float maxLookDown = -85;
	public static final float maxLookUp = 85;
	public float mouseSensitivity = 2;
	public int walkingSpeed = 200;
	private double[] lastPos = new double[2];
	
	public void handleInput() {
		if (Mouse.isButtonDown(window, 0) && Mouse.isInsideWindow(window)) {
			double[] pos = Mouse.getMousePosition(window);
			if (lastPos == null) {
				lastPos = pos;
			}
			float mouseDX = (float)(pos[0] - lastPos[0]) * mouseSensitivity * 0.16F;
			float mouseDY = (float)(lastPos[1] - pos[1]) * mouseSensitivity * 0.16F;
			lastPos = pos;
			if (Math.toDegrees(cam.getRotation().y) + mouseDX >= 360) {
				cam.getRotation().y = (float)Math.toRadians(Math.toDegrees(cam.getRotation().y) + mouseDX - 360);
			}else if (Math.toDegrees(cam.getRotation().y) + mouseDX < 0) {
				cam.getRotation().y = (float)Math.toRadians(360 - Math.toDegrees(cam.getRotation().y) + mouseDX);
			}else{
				cam.getRotation().y += Math.toRadians(mouseDX);
			}
			if (Math.toDegrees(cam.getRotation().x) - mouseDY >= maxLookDown && Math.toDegrees(cam.getRotation().x) - mouseDY <= maxLookUp) {
				cam.getRotation().x += Math.toRadians(-mouseDY);
			}else if (Math.toDegrees(cam.getRotation().x) - mouseDY < maxLookDown) {
				cam.getRotation().x = (float)Math.toRadians(maxLookDown);
			}else if (Math.toDegrees(cam.getRotation().x) - mouseDY > maxLookUp) {
				cam.getRotation().x = (float)Math.toRadians(maxLookUp);
			}
		}
		if (!Mouse.isButtonDown(window, 0)) {
			lastPos = null;
		}
		
		boolean W = Keyboard.isKeyDown(window, GLFW.GLFW_KEY_S);
		boolean S = Keyboard.isKeyDown(window, GLFW.GLFW_KEY_W);
		boolean A = Keyboard.isKeyDown(window, GLFW.GLFW_KEY_D);
		boolean D = Keyboard.isKeyDown(window, GLFW.GLFW_KEY_A);
		boolean SHIFT = Keyboard.isKeyDown(window, GLFW.GLFW_KEY_LEFT_SHIFT);
		boolean SPACE = Keyboard.isKeyDown(window, GLFW.GLFW_KEY_SPACE);

		if (!SHIFT && SPACE) {
			cam.getPosition().setY(cam.getPosition().getY() + 0.3f);
		}
		if (SHIFT && !SPACE){
			cam.getPosition().setY(cam.getPosition().getY() - 0.3f);
		}
		if (W && !S && !A && !D) {
			float angle = (float)Math.toDegrees(cam.getRotation().y);
			Vector3f newPosition = new Vector3f(cam.getPosition().getX(), cam.getPosition().getY(), cam.getPosition().getZ());
			float walking = (walkingSpeed * 0.0002F) * 20;
			float deltaZ = walking * (float) Math.cos(Math.toRadians(angle));
			float deltaX = (float) (Math.sin(Math.toRadians(angle)) * walking);
			newPosition.z += deltaZ;
			newPosition.x -= deltaX;
			cam.getPosition().setZ(newPosition.z);
			cam.getPosition().setX(newPosition.x);
		}
		if (!W && S && !A && !D) {
			float angle = (float)Math.toDegrees(cam.getRotation().y);
			Vector3f newPosition = new Vector3f(cam.getPosition().getX(), cam.getPosition().getY(), cam.getPosition().getZ());
			float walking = -(walkingSpeed * 0.0002F) * 20;
			float deltaZ = walking * (float) Math.cos(Math.toRadians(angle));
			float deltaX = (float) (Math.sin(Math.toRadians(angle)) * walking);
			newPosition.z += deltaZ;
			newPosition.x -= deltaX;
			cam.getPosition().setZ(newPosition.z);
			cam.getPosition().setX(newPosition.x);
		}
		if (!W && !S && A && !D) {
			float angle = (float)Math.toDegrees(cam.getRotation().y) - 90;
			Vector3f newPosition = new Vector3f(cam.getPosition().getX(), cam.getPosition().getY(), cam.getPosition().getZ());
			float walking = (walkingSpeed * 0.0002F) * 20;
			float deltaZ = walking * (float) Math.cos(Math.toRadians(angle));
			float deltaX = (float) (Math.sin(Math.toRadians(angle)) * walking);
			newPosition.z += deltaZ;
			newPosition.x -= deltaX;
			cam.getPosition().setZ(newPosition.z);
			cam.getPosition().setX(newPosition.x);
		}
		if (!W && !S && !A && D) {
			float angle = (float)Math.toDegrees(cam.getRotation().y) + 90;
			Vector3f newPosition = new Vector3f(cam.getPosition().getX(), cam.getPosition().getY(), cam.getPosition().getZ());
			float walking = (walkingSpeed * 0.0002F) * 20;
			float deltaZ = walking * (float) Math.cos(Math.toRadians(angle));
			float deltaX = (float) (Math.sin(Math.toRadians(angle)) * walking);
			newPosition.z += deltaZ;
			newPosition.x -= deltaX;
			cam.getPosition().setZ(newPosition.z);
			cam.getPosition().setX(newPosition.x);
		}
		if (W && !S && !A && D) {
			float angle = (float)Math.toDegrees(cam.getRotation().y) + 45;
			Vector3f newPosition = new Vector3f(cam.getPosition().getX(), cam.getPosition().getY(), cam.getPosition().getZ());
			float walking = (walkingSpeed * 0.0002F) * 20;
			float deltaZ = walking * (float) Math.cos(Math.toRadians(angle));
			float deltaX = (float) (Math.sin(Math.toRadians(angle)) * walking);
			newPosition.z += deltaZ;
			newPosition.x -= deltaX;
			cam.getPosition().setZ(newPosition.z);
			cam.getPosition().setX(newPosition.x);
		}
		if (W && !S && A && !D) {
			float angle = (float)Math.toDegrees(cam.getRotation().y) - 45;
			Vector3f newPosition = new Vector3f(cam.getPosition().getX(), cam.getPosition().getY(), cam.getPosition().getZ());
			float walking = (walkingSpeed * 0.0002F) * 20;
			float deltaZ = walking * (float) Math.cos(Math.toRadians(angle));
			float deltaX = (float) (Math.sin(Math.toRadians(angle)) * walking);
			newPosition.z += deltaZ;
			newPosition.x -= deltaX;
			cam.getPosition().setZ(newPosition.z);
			cam.getPosition().setX(newPosition.x);
		}
		if (!W && S && !A && D) {
			float angle = (float)Math.toDegrees(cam.getRotation().y) + 135;
			Vector3f newPosition = new Vector3f(cam.getPosition().getX(), cam.getPosition().getY(), cam.getPosition().getZ());
			float walking = (walkingSpeed * 0.0002F) * 20;
			float deltaZ = walking * (float) Math.cos(Math.toRadians(angle));
			float deltaX = (float) (Math.sin(Math.toRadians(angle)) * walking);
			newPosition.z += deltaZ;
			newPosition.x -= deltaX;
			cam.getPosition().setZ(newPosition.z);
			cam.getPosition().setX(newPosition.x);
		}
		if (!W && S && A && !D) {
			float angle = (float)Math.toDegrees(cam.getRotation().y) - 135;
			Vector3f newPosition = new Vector3f(cam.getPosition().getX(), cam.getPosition().getY(), cam.getPosition().getZ());
			float walking = (walkingSpeed * 0.0002F) * 20;
			float deltaZ = walking * (float) Math.cos(Math.toRadians(angle));
			float deltaX = (float) (Math.sin(Math.toRadians(angle)) * walking);
			newPosition.z += deltaZ;
			newPosition.x -= deltaX;
			cam.getPosition().setZ(newPosition.z);
			cam.getPosition().setX(newPosition.x);
		}
	}
}
