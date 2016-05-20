package com.nemezor.nemgine.tests.physics;

import java.awt.Point;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.nemezor.nemgine.graphics.DisplayManager;
import com.nemezor.nemgine.graphics.FontManager;
import com.nemezor.nemgine.graphics.FrameBufferManager;
import com.nemezor.nemgine.graphics.GLHelper;
import com.nemezor.nemgine.graphics.ModelManager;
import com.nemezor.nemgine.graphics.ShaderManager;
import com.nemezor.nemgine.graphics.util.Camera;
import com.nemezor.nemgine.graphics.util.Display;
import com.nemezor.nemgine.graphics.util.GLResourceEvent;
import com.nemezor.nemgine.graphics.util.LightSource;
import com.nemezor.nemgine.graphics.util.OpenGLResources;
import com.nemezor.nemgine.input.Keyboard;
import com.nemezor.nemgine.input.Mouse;
import com.nemezor.nemgine.main.Application;
import com.nemezor.nemgine.main.IMainRenderLoop;
import com.nemezor.nemgine.main.IMainTickLoop;
import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.misc.Color;
import com.nemezor.nemgine.misc.Platform;
import com.nemezor.nemgine.misc.Registry;
import com.nemezor.nemgine.physics.Body;
import com.nemezor.nemgine.physics.Collider;

public class PhysicsTest implements IMainRenderLoop, IMainTickLoop {

	private int windowID;
	private Display window;
	private Camera cam;
	private LightSource light;
	private int fps;
	private long time;
	private int angle;
	private int shader;
	private int depthShader;
	private int framebuffer;
	
	@Application(name="Physics Test", path="tests/physics", contained=true)
	public void entry() {
		int thread = Nemgine.generateThreads("Render", true);
		int tick = Nemgine.generateThreads("Tick", true);
		Nemgine.bindRenderLoop(thread, this);
		Nemgine.bindTickLoop(tick, this);
		Nemgine.startThread(thread);
		Nemgine.startThread(tick);
		cam = new Camera(new Vector3f(-10, 15, 20), new Vector3f((float)Math.PI / 6, (float)Math.PI / 8, 0));
		Nemgine.printThreadKeepUpWarnings(false);
	}
	
	@OpenGLResources
	public void load(GLResourceEvent e) {
		if (e == GLResourceEvent.GENERATE_IDS) {
			shader = ShaderManager.generateShaders();
			depthShader = ShaderManager.generateShaders();
		}else if (e == GLResourceEvent.LOAD_SHADERS) {
			ShaderManager.initializeShader(shader, "com/nemezor/nemgine/tests/shadows.vert", "com/nemezor/nemgine/tests/shadows.frag", new String[] {"projection", "transformation", "color", "lightProjection", "lightTransformation"}, new String[] {"position"}, new int[] {0});
			ShaderManager.initializeShader(depthShader, "com/nemezor/nemgine/tests/depth.vert", "com/nemezor/nemgine/tests/depth.frag", new String[] {"projection", "transformation"}, new String[] {"position"}, new int[] {0});
		}
	}
	
	public static void main(String[] args) {
		Nemgine.start(args, PhysicsTest.class);
	}

	@Override
	public void render() {
		DisplayManager.switchDisplay(windowID);
		if (window.closeRequested()) {
			Nemgine.shutDown();
		}
		window.prepareRender();
		
		window.fill(new Color(0x000000FF));
		/*
		FrameBufferManager.bindFrameBuffer(framebuffer);

		window.fill(new Color(0x0000FFFF));
		ShaderManager.bindShader(depthShader);
		ShaderManager.loadMatrix4(depthShader, "projection", light.getProjectionMatrix());
		ShaderManager.loadMatrix4(depthShader, "transformation", light.getTransformationMatrix());
		
		Collider.render();

		ModelManager.finishRendering();
		FrameBufferManager.unbindFrameBuffer(window);*/
		/*
		ShaderManager.bindShader(shader);
		ShaderManager.loadVector4(shader, "color", new Color(0xFFFFFFFF).getColorAsVector());
		ShaderManager.loadMatrix4(shader, "projection", window.getPerspectiveProjectionMatrix());
		ShaderManager.loadMatrix4(shader, "transformation", GLHelper.initTransformationMatrix(cam, new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1)));
		ShaderManager.loadMatrix4(shader, "lightProjection", light.getProjectionMatrix());
		ShaderManager.loadMatrix4(shader, "lightTransformation", light.getTransformationMatrix());
		FrameBufferManager.bindFrameBufferTexture(framebuffer, FrameBuffer.TEXTURE_BUFFER);*/
		ShaderManager.bindShader(ShaderManager.getColorShaderID());
		ShaderManager.loadVector4(ShaderManager.getColorShaderID(), "color", new Color(0xFFFFFFFF).getColorAsVector());
		ShaderManager.loadMatrix4(ShaderManager.getColorShaderID(), "projection", window.getPerspectiveProjectionMatrix());
		ShaderManager.loadMatrix4(ShaderManager.getColorShaderID(), "transformation", GLHelper.initTransformationMatrix(cam, new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1)));
		
		Collider.render();
		
		//FrameBufferManager.unbindFrameBufferTexture();
		/*
		ModelManager.finishRendering();
		ShaderManager.bindShader(ShaderManager.getTextureShaderID());
		ShaderManager.loadVector4(ShaderManager.getTextureShaderID(), "color", new Color(0xFF0000FF).getColorAsVector());
		ShaderManager.loadMatrix4(ShaderManager.getTextureShaderID(), "projection", GLHelper.initBasicOrthographicProjectionMatrix());
		ShaderManager.loadMatrix4(ShaderManager.getTextureShaderID(), "transformation", GLHelper.initTransformationMatrix(new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1)));
		FrameBufferManager.bindFrameBufferTexture(framebuffer, FrameBuffer.TEXTURE_BUFFER);
		
		Tessellator.start(Tessellator.QUADS);
		Tessellator.addVertex(0, 0);
		Tessellator.addTexCoord(0, 1);
		Tessellator.addVertex(1, 0);
		Tessellator.addTexCoord(1, 1);
		Tessellator.addVertex(1, 1);
		Tessellator.addTexCoord(1, 0);
		Tessellator.addVertex(0, 1);
		Tessellator.addTexCoord(0, 0);
		Tessellator.finish();
		
		FrameBufferManager.unbindFrameBufferTexture();
		ModelManager.finishRendering();*/
		
		FontManager.drawString(FontManager.getDefaultFontID20(), 20, 40, fps + " FPS - " + time + "ms", new Color(0xFF00FFFF), new Matrix4f(), GLHelper.initOrthographicProjectionMatrix(0, window.getWidth(), 0, window.getHeight(), 0, 1), Registry.INVALID, Registry.INVALID);
		FontManager.drawString(FontManager.getDefaultFontID20(), 20, 70, "Mem: " + (Platform.getUsedMemory() / 1048576) + "/" + (Platform.getAllocatedMemory() / 1048576) + "MB", new Color(0xFF00FFFF), new Matrix4f(), GLHelper.initOrthographicProjectionMatrix(0, window.getWidth(), 0, window.getHeight(), 0, 1), Registry.INVALID, Registry.INVALID);
		FontManager.drawString(FontManager.getDefaultFontID20(), 20, 100, "CPU: " + new DecimalFormat("#00.00").format(Platform.getCPUUsage() * 100.0d) + "%", new Color(0xFF00FFFF), new Matrix4f(), GLHelper.initOrthographicProjectionMatrix(0, window.getWidth(), 0, window.getHeight(), 0, 1), Registry.INVALID, Registry.INVALID);
		
		ModelManager.finishRendering();
		window.finishRender();

		handleMouseInput();
		handleKeyboardInput();
		light.changeLocation(new Vector3f((float)Math.sin(Math.toRadians(angle)) * 150.0f, 100, (float)Math.cos(Math.toRadians(angle)) * 150.0f));
		light.updateMatrices();
		angle++;
		
		if (angle == 360) {
			angle = 0;
		}
	}

	@Override
	public void setUpRender() {
		windowID = DisplayManager.generateDisplays();
		window = DisplayManager.initializeDisplay(windowID, 70.0f, 1280, 720, 0.01f, 1000.0f, true);
		framebuffer = FrameBufferManager.generateFrameBuffers();
		FrameBufferManager.initializeFrameBuffer(window, framebuffer, 2048, 2048, true, false, true);
		light = new LightSource(new Vector3f(0, 100, 150), cam, window);
	}

	@Override
	public void cleanUpRender() {
		Nemgine.exit(0);
	}

	@Override
	public void updateRenderSecond(int frames, long averageInterval) {
		fps = frames;
		time = averageInterval;
	}

	@Override
	public long getRenderSleepInterval() {
		return 16;
	}

	@Override
	public int getRenderFrameskipTreshold() {
		return 20;
	}

	public static final float maxLookDown = -85;
	public static final float maxLookUp = 85;
	public float mouseSensitivity = 2;
	public int walkingSpeed = 200;
	private Point lastPos;
	
	public void handleMouseInput() {
		if (Mouse.isButtonDown(window, 0) && Mouse.isInsideWindow(window)) {
			Point pos = Mouse.getMousePosition(window);
			if (lastPos == null) {
				lastPos = pos;
			}
			float mouseDX = (float)(pos.getX() - lastPos.getX()) * mouseSensitivity * 0.16F;
			float mouseDY = (float)(lastPos.getY() - pos.getY()) * mouseSensitivity * 0.16F;
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
	}
	
	public void handleKeyboardInput() {
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

	@Override
	public void tick() {
		Collider.tick();
	}

	@Override
	public void setUpTick() {
		ArrayList<Vector3f> vecs = new ArrayList<Vector3f>();
		
		vecs.add(new Vector3f(-10, -2, -10));
		vecs.add(new Vector3f(10, -2, -10));
		vecs.add(new Vector3f(0, -2, 10));
		
		Collider.addBody(vecs, ShaderManager.getColorShaderID(), new Color(0xFF0000FF));
		
		vecs.clear();
		vecs.add(new Vector3f(-1, 10, 0));
		vecs.add(new Vector3f(1, 10, 0));
		vecs.add(new Vector3f(0, 11, 0));

		Collider.addBody(vecs, ShaderManager.getColorShaderID(), new Color(0xFF00FF));
		
		vecs.clear();
		vecs.add(new Vector3f(-50, -50, -30));
		vecs.add(new Vector3f(0, 50, -30));
		vecs.add(new Vector3f(50, -50, -30));

		Collider.addBody(vecs, ShaderManager.getColorShaderID(), new Color(0xFFFF));
		
		vecs.clear();
		vecs.add(new Vector3f(-5000, -50, -5000));
		vecs.add(new Vector3f(5000, -50, -5000));
		vecs.add(new Vector3f(0, -50, 5000));

		Body plane = Collider.addBody(vecs, ShaderManager.getColorShaderID(), new Color(0xFFFFFFFF));
		plane.stationary = true;
	}

	@Override
	public void cleanUpTick() {
		
	}

	@Override
	public void updateTickSecond(int ticks, long averageInterval) {
		
	}

	@Override
	public long getTickSleepInterval() {
		return 50;
	}

	@Override
	public int getTickTickskipTreshold() {
		return 0;
	}
}
