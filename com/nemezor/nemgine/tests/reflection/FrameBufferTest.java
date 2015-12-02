package com.nemezor.nemgine.tests.reflection;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.nemezor.nemgine.graphics.Camera;
import com.nemezor.nemgine.graphics.DisplayManager;
import com.nemezor.nemgine.graphics.FrameBuffer;
import com.nemezor.nemgine.graphics.FrameBufferManager;
import com.nemezor.nemgine.graphics.GLHelper;
import com.nemezor.nemgine.graphics.ModelManager;
import com.nemezor.nemgine.graphics.ShaderManager;
import com.nemezor.nemgine.main.Application;
import com.nemezor.nemgine.main.IMainRenderLoop;
import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.main.NemgineLoader;

public class FrameBufferTest implements IMainRenderLoop {

	private int shader;
	private int logoShader;
	private int model;
	private int logo;
	private int angle = 0;
	private int square;
	private int water;
	private int frame;
	private int waterLevel = -20;
	
	private Camera cam;
	
	@Application(name="FrameBuffer Renderer", width=800, height=450, path="tests/water", contained=true)
	public void entry() {
		NemgineLoader.updateState("Initializing");
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
		
		FrameBufferManager.bindFrameBuffer(frame);
		
		ModelManager.renderModel(model, 0, shader, transform2, GLHelper.getCurrentPerspectiveProjectionMatrix(), "transformation", "projection");
		ModelManager.renderModel(logo, 0, logoShader, logoTransform2, GLHelper.getCurrentPerspectiveProjectionMatrix(), "transformation", "projection");
		ModelManager.finishRendering();
		
		FrameBufferManager.unbindFrameBuffer();

		ModelManager.renderModel(model, 0, shader, transform, GLHelper.getCurrentPerspectiveProjectionMatrix(), "transformation", "projection");
		ModelManager.renderModel(logo, 0, logoShader, logoTransform, GLHelper.getCurrentPerspectiveProjectionMatrix(), "transformation", "projection");
		ModelManager.renderModelWithFrameBufferTexture(square, frame, FrameBuffer.TEXTURE_BUFFER, water, waterTransform, GLHelper.getCurrentPerspectiveProjectionMatrix(), "transformation", "projection");
		
		
		ModelManager.finishRendering();
		
		DisplayManager.finishRender();
		angle++;
		
		handleInput();
	}
	
	@Override
	public void setUpRender() {
		try {
			DisplayManager.initialize(70.0f, 0.002f, 500.0f);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		frame = FrameBufferManager.generateFrameBuffers();
		FrameBufferManager.initializeFrameBuffer(frame, 800, 450, true, false, true);
	}

	@Override
	public void cleanUpRender() {
		Nemgine.exit(0);
	}
	
	@Override
	public void loadResources() {
		NemgineLoader.updateState("Loading Shaders");
		
		shader = ShaderManager.generateShaders();
		ShaderManager.initializeShader(shader, "com/nemezor/nemgine/tests/reflection/vertex.shader", 
											   "com/nemezor/nemgine/tests/reflection/fragment.shader", 
											   new String[] {"projection", "transformation", "light"}, 
											   new String[] {"position", "normal"}, new int[] {0, 2});
		logoShader = ShaderManager.generateShaders();
		ShaderManager.initializeShader(logoShader, "com/nemezor/nemgine/tests/reflection/logo.vertex", 
												   "com/nemezor/nemgine/tests/reflection/logo.fragment", 
												   new String[] {"projection", "transformation"}, 
												   new String[] {"position"}, new int[] {0});
		water = ShaderManager.generateShaders();
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
		
		NemgineLoader.updateState("Loading Models");
		
		model = ModelManager.generateModels();
		ModelManager.initializeModel(model, "com/nemezor/nemgine/tests/reflection/dragon.obj");
		logo = ModelManager.generateModels();
		ModelManager.initializeModel(logo, "com/nemezor/nemgine/tests/reflection/nemgine.obj");
		square = ModelManager.generateModels();
		ModelManager.initializeModel(square, "com/nemezor/nemgine/tests/reflection/square.obj");
	}

	@Override
	public void updateRenderSecond(int frames, long averageInterval) {
		DisplayManager.changeTitle("FrameBuffer Renderer | FPS: " + frames + " | AVG: " + averageInterval + "ms");
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
		Nemgine.start(args, FrameBufferTest.class);
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
