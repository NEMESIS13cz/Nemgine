package com.nemezor.nemgine.tests.physics;

import java.awt.Point;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.nemezor.nemgine.debug.DebugColorizer;
import com.nemezor.nemgine.graphics.DisplayManager;
import com.nemezor.nemgine.graphics.FontManager;
import com.nemezor.nemgine.graphics.FrameBufferManager;
import com.nemezor.nemgine.graphics.GLHelper;
import com.nemezor.nemgine.graphics.ModelManager;
import com.nemezor.nemgine.graphics.ShaderManager;
import com.nemezor.nemgine.graphics.Tessellator;
import com.nemezor.nemgine.graphics.TextureManager;
import com.nemezor.nemgine.graphics.util.Camera;
import com.nemezor.nemgine.graphics.util.Display;
import com.nemezor.nemgine.graphics.util.FrameBuffer;
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
import com.nemezor.nemgine.misc.Debugger;
import com.nemezor.nemgine.misc.Platform;
import com.nemezor.nemgine.misc.Registry;
import com.nemezor.nemgine.physics.Body;
import com.nemezor.nemgine.physics.Collider;

public class PhysicsTest implements IMainRenderLoop, IMainTickLoop {

	private static final int SHADOWMAP_SIZE = 4096;
	
	private int windowID;
	private Display window;
	private Camera cam;
	private LightSource light;
	private int fps;
	private long time;
	private int shader;
	private int depthShader;
	private int framebuffer;
	private int model;
	private int v, n, t;
	private int sunObject;
	private int sunBuffer;
	private int finalBuffer;
	private int rainbowzBuffer;
	private int sunShader;
	private int lensFlare1;
	private int lensFlare2;
	private int lensFlare3;
	private int lensFlare4;
	private int rainbowzShader;
	private DebugColorizer colorizer = new DebugColorizer(0);
	private Color currentRainbowz = new Color(0xFFFFFFFF);
	private float angle = 0;
	private boolean derpMode = false;
	
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
			model = ModelManager.generateModels();
			sunObject = ModelManager.generateModels();
			sunShader = ShaderManager.generateShaders();
			lensFlare1 = TextureManager.generateTextures();
			lensFlare2 = TextureManager.generateTextures();
			lensFlare3 = TextureManager.generateTextures();
			lensFlare4 = TextureManager.generateTextures();
			rainbowzShader = ShaderManager.generateShaders();
		}else if (e == GLResourceEvent.LOAD_SHADERS) {
			ShaderManager.initializeShader(shader, "com/nemezor/nemgine/tests/shadows.vert", "com/nemezor/nemgine/tests/shadows.frag", new String[] {"projection", "transformation", "color", "lightProjection", "lightTransformation", "lightDir", "shadowMapSize"}, new String[] {"position", "normal"}, new int[] {0, 2});
			ShaderManager.initializeShader(depthShader, "com/nemezor/nemgine/tests/depth.vert", "com/nemezor/nemgine/tests/depth.frag", new String[] {"projection", "transformation"}, new String[] {"position"}, new int[] {0});
			ShaderManager.initializeShader(sunShader, "com/nemezor/nemgine/tests/sun.vert", "com/nemezor/nemgine/tests/sun.frag", new String[] {"projection", "transformation", "sunColor", "sun_map", "shadow_map", "sunPosIn"}, new String[] {"position"}, new int[] {0});
			ShaderManager.initializeShader(rainbowzShader, "com/nemezor/nemgine/tests/rainbowz.vert", "com/nemezor/nemgine/tests/rainbowz.frag", new String[] {"projection", "transformation", "color"}, new String[] {"position"}, new int[] {0});
		}else if (e == GLResourceEvent.LOAD_MODELS) {
			ModelManager.initializeModel(model, "text.obj");
			ModelManager.initializeModel(sunObject, "com/nemezor/nemgine/resources/circle_64.obj");
		}else if (e == GLResourceEvent.LOAD_TEXTURES) {
			TextureManager.initializeTextureFile(lensFlare1, "com/nemezor/nemgine/resources/lens_flare_1.png");
			TextureManager.initializeTextureFile(lensFlare2, "com/nemezor/nemgine/resources/lens_flare_2.png");
			TextureManager.initializeTextureFile(lensFlare3, "com/nemezor/nemgine/resources/lens_flare_3.png");
			if (derpMode) {
				TextureManager.initializeTextureFile(lensFlare4, "doge.png");
			}else{
				TextureManager.initializeTextureFile(lensFlare4, "com/nemezor/nemgine/resources/lens_flare_4.png");
			}
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
		
		int scale = 70;
		Matrix4f transform = GLHelper.initTransformationMatrix(cam, new Vector3f(150, 20, 0), new Vector3f((float)Math.toRadians(angle), (float)Math.toRadians(angle), 0), new Vector3f(scale, scale, scale));
		Matrix4f transformLight = GLHelper.initTransformationMatrix(new Vector3f(150, 20, 0), new Vector3f((float)Math.toRadians(angle), (float)Math.toRadians(angle), 0), new Vector3f(scale, scale, scale));
		angle += 10;
		
		FrameBufferManager.bindFrameBuffer(framebuffer);
		
		window.fill(new Color(0x000000FF));
		ShaderManager.bindShader(depthShader);
		ShaderManager.loadMatrix4(depthShader, "projection", light.getProjectionMatrix());
		ShaderManager.loadMatrix4(depthShader, "transformation", light.getTransformationMatrix());
		
		Collider.render(0, null, null);
		ModelManager.renderModel(model, 0, depthShader, Matrix4f.mul(light.getTransformationMatrix(), transformLight, null), light.getProjectionMatrix(), "transformation", "projection");
		
		ModelManager.finishRendering();
		FrameBufferManager.bindFrameBuffer(sunBuffer);
		window.fill(new Color(0x00000000));
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		ModelManager.renderModelWithColor(sunObject, 0, ShaderManager.getColorShaderID(), GLHelper.initTransformationMatrix(cam, Vector3f.add(cam.getPosition(), light.getPosition(), null), light.getRotation(), new Vector3f(10, 10, 10)), window.getPerspectiveProjectionMatrix(), new Color(0xFFFFFFFF), "transformation", "projection", "color");
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		ShaderManager.loadMatrix4(ShaderManager.getColorShaderID(), "projection", window.getPerspectiveProjectionMatrix());
		ShaderManager.loadMatrix4(ShaderManager.getColorShaderID(), "transformation", GLHelper.initTransformationMatrix(cam, new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1)));
		
		Collider.render(ShaderManager.getColorShaderID(), new Color(0x00000000), "color");
		ModelManager.renderModel(model, 0, ShaderManager.getColorShaderID(), transform, window.getPerspectiveProjectionMatrix(), "transformation", "projection");
		
		FrameBufferManager.bindFrameBuffer(finalBuffer);
		window.fill(new Color(0x96C1E1FF));
		
		ShaderManager.bindShader(shader);
		ShaderManager.loadVector4(shader, "color", new Color(0xFFFFFFFF).getColorAsVector());
		ShaderManager.loadMatrix4(shader, "projection", window.getPerspectiveProjectionMatrix());
		ShaderManager.loadMatrix4(shader, "transformation", GLHelper.initTransformationMatrix(cam, new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1)));
		ShaderManager.loadMatrix4(shader, "lightProjection", light.getProjectionMatrix());
		ShaderManager.loadMatrix4(shader, "lightTransformation", light.getTransformationMatrix());
		ShaderManager.loadVector3(shader, "lightDir", new Vector3f(0, 100, 150));
		FrameBufferManager.bindFrameBufferTexture(framebuffer, FrameBuffer.TEXTURE_BUFFER);
		Collider.render(0, null, null);
		ShaderManager.loadVector4(shader, "color", new Color(0xd4a190FF).getColorAsVector());
		ModelManager.renderModel(model, 0, shader, transform, window.getPerspectiveProjectionMatrix(), "transformation", "projection");

		FrameBufferManager.unbindFrameBuffer(window);
		FrameBufferManager.bindFrameBuffer(rainbowzBuffer);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		FrameBufferManager.bindFrameBufferTexture(finalBuffer, FrameBuffer.TEXTURE_BUFFER);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		FrameBufferManager.bindFrameBufferTexture(sunBuffer, FrameBuffer.TEXTURE_BUFFER);
		
		ShaderManager.bindShader(sunShader);
		ShaderManager.loadInteger(sunShader, "shadow_map", 0);
		ShaderManager.loadInteger(sunShader, "sun_map", 1);
		ShaderManager.loadVector4(sunShader, "sunColor", new Color(0xFFFFFFFF).getColorAsVector());
		ShaderManager.loadMatrix4(sunShader, "projection", GLHelper.initBasicOrthographicProjectionMatrix());
		ShaderManager.loadMatrix4(sunShader, "transformation", GLHelper.initTransformationMatrix(new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1)));
		Vector4f lightPosTemp = Matrix4f.transform(window.getPerspectiveProjectionMatrix(), Matrix4f.transform(GLHelper.initTransformationMatrix(cam, Vector3f.add(cam.getPosition(), light.getPosition(), null), light.getRotation(), new Vector3f(10, 10, 10)), new Vector4f(0, 0, 0, 1), null), null);
		ShaderManager.loadVector2(sunShader, "sunPosIn", new Vector2f((lightPosTemp.x / lightPosTemp.w) / 2 + 0.5f, 1.0f - ((lightPosTemp.y / lightPosTemp.w) / 2 + 0.5f)));
		
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
		if ((lightPosTemp.z / lightPosTemp.w) / 2 + 0.5f < 1.0f) {
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			
			float lx = 0.5f;
			float ly = 0.5f;
			float sunX = (lightPosTemp.x / lightPosTemp.w) / 2 + 0.5f;
			float sunY = 1.0f - ((lightPosTemp.y / lightPosTemp.w) / 2 + 0.5f);
			Vector2f sunToCenter = new Vector2f(sunX - lx, sunY - ly);
			float incrementX = (lx - sunX) / sunToCenter.length();
			float incrementY = (ly - sunY) / sunToCenter.length();
			
			renderFlare(new Color(0xFFFFFFFF), lensFlare4, sunX, sunY, 400);
			renderFlare(new Color(0xFFFFFF20), lensFlare3, sunX + incrementX * 0.05f, sunY + incrementY * 0.05f, 650);
			renderFlare(new Color(0xFFFFFF40), lensFlare2, sunX + incrementX * 0.15f, sunY + incrementY * 0.15f, 400);
			renderFlare(new Color(0xFFFFFF80), lensFlare3, sunX + incrementX * 0.4f, sunY + incrementY * 0.4f, 80);
			renderFlare(new Color(0xFFFFFF80), lensFlare1, sunX + incrementX * 0.5f, sunY + incrementY * 0.5f, 40);
	
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL11.GL_BLEND);
		}
		FrameBufferManager.unbindFrameBuffer(window);
		FrameBufferManager.bindFrameBufferTexture(rainbowzBuffer, FrameBuffer.TEXTURE_BUFFER);
		ShaderManager.bindShader(ShaderManager.getTextureShaderID());
		ShaderManager.loadMatrix4(ShaderManager.getTextureShaderID(), "projection", GLHelper.initBasicOrthographicProjectionMatrix());
		ShaderManager.loadMatrix4(ShaderManager.getTextureShaderID(), "transformation", GLHelper.initTransformationMatrix(new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1)));
		if (derpMode) {
			ShaderManager.loadVector4(ShaderManager.getTextureShaderID(), "color", (currentRainbowz = colorizer.getNext(currentRainbowz)).getColorAsVector());
		}else{
			ShaderManager.loadVector4(ShaderManager.getTextureShaderID(), "color", new Color(0xFFFFFFFF).getColorAsVector());
		}
		
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
		
		FontManager.drawString(FontManager.getDefaultFontID20(), 20, 40, fps + " FPS - " + time + "ms", new Color(0xFF00FFFF), new Matrix4f(), GLHelper.initOrthographicProjectionMatrix(0, window.getWidth(), 0, window.getHeight(), 0, 1), Registry.INVALID, Registry.INVALID);
		FontManager.drawString(FontManager.getDefaultFontID20(), 20, 70, "Mem: " + (Platform.getUsedMemory() / 1048576) + "/" + (Platform.getAllocatedMemory() / 1048576) + "MB", new Color(0xFF00FFFF), new Matrix4f(), GLHelper.initOrthographicProjectionMatrix(0, window.getWidth(), 0, window.getHeight(), 0, 1), Registry.INVALID, Registry.INVALID);
		FontManager.drawString(FontManager.getDefaultFontID20(), 20, 100, "CPU: " + new DecimalFormat("#00.00").format(Platform.getCPUUsage() * 100.0d) + "%", new Color(0xFF00FFFF), new Matrix4f(), GLHelper.initOrthographicProjectionMatrix(0, window.getWidth(), 0, window.getHeight(), 0, 1), Registry.INVALID, Registry.INVALID);
		FontManager.drawString(FontManager.getDefaultFontID20(), 20, 130, "Vertices: " + v, new Color(0xFF00FFFF), new Matrix4f(), GLHelper.initOrthographicProjectionMatrix(0, window.getWidth(), 0, window.getHeight(), 0, 1), Registry.INVALID, Registry.INVALID);
		FontManager.drawString(FontManager.getDefaultFontID20(), 20, 160, "Normals: " + n, new Color(0xFF00FFFF), new Matrix4f(), GLHelper.initOrthographicProjectionMatrix(0, window.getWidth(), 0, window.getHeight(), 0, 1), Registry.INVALID, Registry.INVALID);
		FontManager.drawString(FontManager.getDefaultFontID20(), 20, 190, "TexCoords: " + t, new Color(0xFF00FFFF), new Matrix4f(), GLHelper.initOrthographicProjectionMatrix(0, window.getWidth(), 0, window.getHeight(), 0, 1), Registry.INVALID, Registry.INVALID);
		
		ModelManager.finishRendering();
		window.finishRender();

		handleMouseInput();
		handleKeyboardInput();
		light.updateMatrices();
		
		v = Debugger.vertices;
		n = Debugger.normals;
		t = Debugger.texCoords;
		Debugger.clear();

		if (derpMode) {
			currentRainbowz = colorizer.getNext(currentRainbowz);
			currentRainbowz = colorizer.getNext(currentRainbowz);
			currentRainbowz = colorizer.getNext(currentRainbowz);
			currentRainbowz = colorizer.getNext(currentRainbowz);
			currentRainbowz = colorizer.getNext(currentRainbowz);
			currentRainbowz = colorizer.getNext(currentRainbowz);
			currentRainbowz = colorizer.getNext(currentRainbowz);
			currentRainbowz = colorizer.getNext(currentRainbowz);
			currentRainbowz = colorizer.getNext(currentRainbowz);
			currentRainbowz = colorizer.getNext(currentRainbowz);
			currentRainbowz = colorizer.getNext(currentRainbowz);
			currentRainbowz = colorizer.getNext(currentRainbowz);
			currentRainbowz = colorizer.getNext(currentRainbowz);
			currentRainbowz = colorizer.getNext(currentRainbowz);
			currentRainbowz = colorizer.getNext(currentRainbowz);
			currentRainbowz = colorizer.getNext(currentRainbowz);
			currentRainbowz = colorizer.getNext(currentRainbowz);
		}
	}
	
	private void renderFlare(Color color, int texture, float x, float y, int size) {
		float texelW = 1.0f / window.getWidth();
		float texelH = 1.0f / window.getHeight();
		
		ShaderManager.bindShader(ShaderManager.getTextureShaderID());
		ShaderManager.loadMatrix4(ShaderManager.getTextureShaderID(), "projection", GLHelper.initBasicOrthographicProjectionMatrix());
		ShaderManager.loadMatrix4(ShaderManager.getTextureShaderID(), "transformation", GLHelper.initTransformationMatrix(new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1)));
		ShaderManager.loadVector4(ShaderManager.getTextureShaderID(), "color", color.getColorAsVector());
		TextureManager.bindTexture(texture);
		
		Tessellator.start(Tessellator.QUADS);
		Tessellator.addVertex(x - size * texelW, y - size * texelH);
		Tessellator.addTexCoord(0, 1);
		Tessellator.addVertex(x + size * texelW, y - size * texelH);
		Tessellator.addTexCoord(1, 1);
		Tessellator.addVertex(x + size * texelW, y + size * texelH);
		Tessellator.addTexCoord(1, 0);
		Tessellator.addVertex(x - size * texelW, y + size * texelH);
		Tessellator.addTexCoord(0, 0);
		Tessellator.finish();
	}

	@Override
	public void setUpRender() {
		windowID = DisplayManager.generateDisplays();
		window = DisplayManager.initializeDisplay(windowID, 70.0f, 1280, 720, 0.01f, 1000.0f, true);
		framebuffer = FrameBufferManager.generateFrameBuffers();
		FrameBufferManager.initializeFrameBuffer(window, framebuffer, SHADOWMAP_SIZE, SHADOWMAP_SIZE, true, false, true);
		light = new LightSource(new Vector3f(0, 75, 150), cam, window);
		sunBuffer = FrameBufferManager.generateFrameBuffers();
		FrameBufferManager.initializeFrameBuffer(window, sunBuffer, 1280, 720, true, false, true);
		finalBuffer = FrameBufferManager.generateFrameBuffers();
		FrameBufferManager.initializeFrameBuffer(window, finalBuffer, 1280, 720, true, false, true);
		rainbowzBuffer = FrameBufferManager.generateFrameBuffers();
		FrameBufferManager.initializeFrameBuffer(window, rainbowzBuffer, 1280, 720, true, false, true);
		ShaderManager.bindShader(shader);
		ShaderManager.loadInteger(shader, "shadowMapSize", SHADOWMAP_SIZE);
		ShaderManager.unbindShader();
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
		return 33;
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
	
	volatile boolean Q = false;
	
	public void handleKeyboardInput() {
		boolean W = Keyboard.isKeyDown(window, GLFW.GLFW_KEY_S);
		boolean S = Keyboard.isKeyDown(window, GLFW.GLFW_KEY_W);
		boolean A = Keyboard.isKeyDown(window, GLFW.GLFW_KEY_D);
		boolean D = Keyboard.isKeyDown(window, GLFW.GLFW_KEY_A);
		boolean SHIFT = Keyboard.isKeyDown(window, GLFW.GLFW_KEY_LEFT_SHIFT);
		boolean SPACE = Keyboard.isKeyDown(window, GLFW.GLFW_KEY_SPACE);
		Q = Keyboard.isKeyDown(window, GLFW.GLFW_KEY_Q);
		if (Keyboard.isKeyDown(window, GLFW.GLFW_KEY_E)) {
			Collider.clearAll();
			setUpTick();
		}
		
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
		if (Q) {
			Collider.tick();
		}
	}

	@Override
	public void setUpTick() {
		ArrayList<Vector3f> vecs = new ArrayList<Vector3f>();
		
		vecs.add(new Vector3f(-10, -2, -10));
		vecs.add(new Vector3f(10, -2, -10));
		vecs.add(new Vector3f(0, -2, 10));
		
		Collider.addBody(vecs, shader, new Color(0xFF0000FF), new Vector3f(0, 1, 0));
		
		vecs.clear();
		vecs.add(new Vector3f(-1, 10, 0));
		vecs.add(new Vector3f(1, 10, 0));
		vecs.add(new Vector3f(0, 11, 0));

		Collider.addBody(vecs, shader, new Color(0x00FF00FF), new Vector3f(0, 1, 0));
		
		vecs.clear();
		vecs.add(new Vector3f(-50, -50, -30));
		vecs.add(new Vector3f(0, 50, -30));
		vecs.add(new Vector3f(50, -50, -30));

		Collider.addBody(vecs, shader, new Color(0x0000FFFF), new Vector3f(0, 0, 1));
		
		vecs.clear();
		vecs.add(new Vector3f(-5000, -50, -5000));
		vecs.add(new Vector3f(5000, -50, -5000));
		vecs.add(new Vector3f(0, -50, 5000));

		Body plane = Collider.addBody(vecs, shader, new Color(0x639b6cFF), new Vector3f(0, 1, 0));
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
