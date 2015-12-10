package com.nemezor.nemgine.tests.gui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.nemezor.nemgine.graphics.DisplayManager;
import com.nemezor.nemgine.graphics.FrameBufferManager;
import com.nemezor.nemgine.graphics.GLHelper;
import com.nemezor.nemgine.graphics.ModelManager;
import com.nemezor.nemgine.graphics.ShaderManager;
import com.nemezor.nemgine.graphics.TextureManager;
import com.nemezor.nemgine.graphics.util.Camera;
import com.nemezor.nemgine.graphics.util.FrameBuffer;
import com.nemezor.nemgine.main.Application;
import com.nemezor.nemgine.main.IMainRenderLoop;
import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.misc.Color;

public class GuiTest implements IMainRenderLoop {

	private int shader;
	private int logoShader;
	private int model;
	private int logo;
	private int angle = 0;
	private int water;
	private int frame;
	private int waterLevel = -20;
	private int testTexture;
	
	private Camera cam;
	
	@Application(name="Gui Test", width=1280, height=720, path="tests/water", contained=true)
	public void entry() {
		int thread = Nemgine.generateThreads("Render", true);
		Nemgine.bindRenderLoop(thread, this);
		Nemgine.startThread(thread);
		cam = new Camera(new Vector3f(), new Vector3f());
	}
	
	@Override
	public void render() {
		if (DisplayManager.closeRequested()) {
			Nemgine.shutDown();
		}
		DisplayManager.resize();
		DisplayManager.prepareRender();
		
		Matrix4f transform = GLHelper.initTransformationMatrix(cam, new Vector3f(0, -5, -25), new Vector3f(0, (float)Math.toRadians(angle), 0), new Vector3f(1, 1, 1));
		Matrix4f logoTransform = GLHelper.initTransformationMatrix(cam, new Vector3f(-15, 10, -30), new Vector3f((float)Math.toRadians(15 + angle), (float)Math.toRadians(25 + angle), (float)Math.toRadians(15)), new Vector3f(1, 1, 1));
		Matrix4f waterTransform = GLHelper.initTransformationMatrix(cam, new Vector3f(0, waterLevel, -25), new Vector3f(), new Vector3f(100, 1, 100));
		Vector3f camP = cam.getPosition();
		Vector3f camR = cam.getRotation();
		Camera invertCam = new Camera(new Vector3f(camP.x, waterLevel - camP.y, camP.z), new Vector3f(-camR.x, camR.y, camR.z));
		Matrix4f logoTransform2 = GLHelper.initTransformationMatrix(invertCam, new Vector3f(-15, 10, -30), new Vector3f((float)Math.toRadians(15 + angle), (float)Math.toRadians(25 + angle), (float)Math.toRadians(15)), new Vector3f(1, 1, 1));
		Matrix4f transform2 = GLHelper.initTransformationMatrix(invertCam, new Vector3f(0, -5, -25), new Vector3f(0, (float)Math.toRadians(angle), 0), new Vector3f(1, 1, 1));
		Matrix4f testTransform = GLHelper.initTransformationMatrix(new Vector3f(0.25f, 0.25f, 0), new Vector3f((float)Math.toRadians(90), 0, 0), new Vector3f(0.25f, 1, 0.25f));
		
		FrameBufferManager.bindFrameBuffer(frame);
		
		ModelManager.renderModel(model, 0, shader, transform2, GLHelper.getCurrentPerspectiveProjectionMatrix(), "transformation", "projection");
		ModelManager.renderModel(logo, 0, logoShader, logoTransform2, GLHelper.getCurrentPerspectiveProjectionMatrix(), "transformation", "projection");
		ModelManager.finishRendering();
		
		FrameBufferManager.unbindFrameBuffer();
		
		ModelManager.renderModel(model, 0, shader, transform, GLHelper.getCurrentPerspectiveProjectionMatrix(), "transformation", "projection");
		ModelManager.renderModel(logo, 0, logoShader, logoTransform, GLHelper.getCurrentPerspectiveProjectionMatrix(), "transformation", "projection");
		ModelManager.renderModelWithFrameBufferTexture(ModelManager.getSquareModelID(), frame, FrameBuffer.TEXTURE_BUFFER, water, waterTransform, GLHelper.getCurrentPerspectiveProjectionMatrix(), "transformation", "projection");
		
		ModelManager.renderModelWithColor(ModelManager.getSquareModelID(), testTexture, ShaderManager.getTextureShaderID(), testTransform, GLHelper.initBasicOrthographicProjectionMatrix(), new Color(1, 1, 1, 0.5f), "transformation", "projection", "color");
		
		ModelManager.finishRendering();
		DisplayManager.finishRender();
		angle++;
		
		handleInput();
	}
	
	@Override
	public void setUpRender() {
		DisplayManager.setOpenGLConfiguration(70.0f, 0.002f, 500.0f);
		frame = FrameBufferManager.generateFrameBuffers();
		FrameBufferManager.initializeFrameBuffer(frame, 800, 450, true, false, true);
	}

	@Override
	public void cleanUpRender() {
		Nemgine.exit(0);
	}

	@Override
	public void generateResources() {
		shader = ShaderManager.generateShaders();
		logoShader = ShaderManager.generateShaders();
		water = ShaderManager.generateShaders();
		model = ModelManager.generateModels();
		logo = ModelManager.generateModels();
		testTexture = TextureManager.generateTextures();
	}
	
	@Override
	public void loadResources() {
		TextureManager.initializeTexture(testTexture, "com/nemezor/nemgine/tests/gui/test_texture.png");
		
		ShaderManager.initializeShader(shader, "com/nemezor/nemgine/tests/reflection/vertex.shader", 
											   "com/nemezor/nemgine/tests/reflection/fragment.shader", 
											   new String[] {"projection", "transformation", "light"}, 
											   new String[] {"position", "normal"}, new int[] {0, 2});
		ShaderManager.initializeShader(logoShader, "com/nemezor/nemgine/tests/reflection/logo.vertex", 
												   "com/nemezor/nemgine/tests/reflection/logo.fragment", 
												   new String[] {"projection", "transformation"}, 
												   new String[] {"position"}, new int[] {0});
		ShaderManager.initializeShader(water, "com/nemezor/nemgine/tests/reflection/reflection.vertex", 
											   "com/nemezor/nemgine/tests/reflection/reflection.fragment", 
											   new String[] {"projection", "transformation", "light"}, 
											   new String[] {"position", "normal"}, new int[] {0, 2});
		
		ShaderManager.bindShader(shader);
		ShaderManager.loadMatrix4(shader, "projection", GLHelper.getCurrentPerspectiveProjectionMatrix());
		ShaderManager.loadMatrix4(shader, "transformation", new Matrix4f());
		ShaderManager.loadVector3(shader, "light", new Vector3f(-50, 30, 10));
		ShaderManager.bindShader(logoShader);
		ShaderManager.loadMatrix4(logoShader, "projection", GLHelper.getCurrentPerspectiveProjectionMatrix());
		ShaderManager.loadMatrix4(logoShader, "transformation", new Matrix4f());
		ShaderManager.bindShader(water);
		ShaderManager.loadMatrix4(water, "projection", GLHelper.getCurrentPerspectiveProjectionMatrix());
		ShaderManager.loadMatrix4(water, "transformation", new Matrix4f());
		ShaderManager.loadVector3(water, "light", new Vector3f(-50, 30, 10));
		ShaderManager.unbindShader();
		
		ModelManager.initializeModel(model, "com/nemezor/nemgine/tests/reflection/dragon.obj");
		ModelManager.initializeModel(logo, "com/nemezor/nemgine/tests/reflection/nemgine.obj");
	}

	@Override
	public void updateRenderSecond(int frames, long averageInterval) {
		DisplayManager.changeTitle("Gui Test | FPS: " + frames + " | AVG: " + averageInterval + "ms");
	}
	
	@Override
	public long getRenderSleepInterval() {
		return 16;
	}

	@Override
	public int getRenderFrameskipTreshold() {
		return 10;
	}
	
	public static void main(String[] args) {
		Nemgine.start(args, GuiTest.class);
	}

	////////////////////////////////////
	/**
	 * Dirty stuff I found in one of my old projects, too lazy to recode it for now
	 */
	
	public static final float maxLookDown = -85;
	public static final float maxLookUp = 85;
	public float mouseSensitivity = 2;
	public int walkingSpeed = 200;
	
	public void handleInput() {
		if (Mouse.isButtonDown(0) && Mouse.isInsideWindow()) {
			float mouseDX = (float)Mouse.getDX() * mouseSensitivity * 0.16F;
			float mouseDY = (float)Mouse.getDY() * mouseSensitivity * 0.16F;
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
		
		boolean W = Keyboard.isKeyDown(Keyboard.KEY_S);
		boolean S = Keyboard.isKeyDown(Keyboard.KEY_W);
		boolean A = Keyboard.isKeyDown(Keyboard.KEY_D);
		boolean D = Keyboard.isKeyDown(Keyboard.KEY_A);
		boolean SHIFT = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
		boolean SPACE = Keyboard.isKeyDown(Keyboard.KEY_SPACE);

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
