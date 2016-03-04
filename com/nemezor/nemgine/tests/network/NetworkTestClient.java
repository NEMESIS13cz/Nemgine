package com.nemezor.nemgine.tests.network;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.nemezor.nemgine.debug.DebugColorizer;
import com.nemezor.nemgine.debug.DebugPacket;
import com.nemezor.nemgine.graphics.DisplayManager;
import com.nemezor.nemgine.graphics.GLHelper;
import com.nemezor.nemgine.graphics.ModelManager;
import com.nemezor.nemgine.graphics.ShaderManager;
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
import com.nemezor.nemgine.misc.Logger;
import com.nemezor.nemgine.network.Address;
import com.nemezor.nemgine.network.IPacket;
import com.nemezor.nemgine.network.Network;
import com.nemezor.nemgine.network.NetworkInfo;
import com.nemezor.nemgine.network.NetworkManager;
import com.nemezor.nemgine.network.NetworkObject;

public class NetworkTestClient implements IMainRenderLoop {

	public static void main(String[] args) {
		Nemgine.start(args, NetworkTestClient.class);
	}
	
	private DebugColorizer colorizer = new DebugColorizer(0);
	private Color currColor = new Color(0xFFFFFFFF);
	private Camera cam;
	private int shader;
	private int logoShader;
	private int model;
	private int logo;
	private int testTexture;
	int windowID;
	Display window;

	private volatile int angle = 0;
	private volatile int socketId = 0;
	
	@Application(contained=true, path="tests/network", name="Network Test | Client")
	public void entry() {
		int thread = Nemgine.generateThreads("Render", false);
		Nemgine.bindRenderLoop(thread, this);
		Nemgine.startThread(thread);
		cam = new Camera(new Vector3f(), new Vector3f());
	}
	
	@OpenGLResources
	public void loadResources(GLResourceEvent e) {
		if (e == GLResourceEvent.GENERATE_IDS) {
			
			shader = ShaderManager.generateShaders();
			logoShader = ShaderManager.generateShaders();
			model = ModelManager.generateModels();
			logo = ModelManager.generateModels();
			testTexture = TextureManager.generateTextures();
			
		}else if (e == GLResourceEvent.LOAD_MODELS) {
			
			ModelManager.initializeModel(model, "dragon.obj");
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

	@Network
	public void connectionEstablished(NetworkObject obj) {
		Logger.log("Connection to " + obj.getAddress().toString() + " established");
	}
	
	@Network
	public void networkInfo(NetworkObject obj, NetworkInfo info) {
		
	}

	@Network
	public void packetReceived(NetworkObject obj, IPacket packet) {
		if (packet.getClass() == DebugPacket.class) {
			angle++;
		}
	}
	
	private void disconnect() {
		NetworkManager.dispose(socketId);
	}
	
	private void connect() {
		socketId = NetworkManager.generateClientSockets(1000);
		NetworkManager.connect(socketId, new Address("localhost", 65535));
	}
	
	@Override
	public void render() {
		if (window.closeRequested()) {
			Nemgine.shutDown();
		}
		window.prepareRender();
		
		window.fill(new Color(0x0000FFFF));
		
		Matrix4f transform = GLHelper.initTransformationMatrix(cam, new Vector3f(0, -5, -25), new Vector3f(0, (float)Math.toRadians(angle), 0), new Vector3f(1, 1, 1));
		Matrix4f logoTransform = GLHelper.initTransformationMatrix(cam, new Vector3f(-15, 10, -30), new Vector3f((float)Math.toRadians(15 + angle), (float)Math.toRadians(25 + angle), (float)Math.toRadians(15)), new Vector3f(1, 1, 1));
		Matrix4f testTransform = GLHelper.initTransformationMatrix(new Vector3f(0.25f, 0.25f, 0), new Vector3f((float)Math.toRadians(90), 0, 0), new Vector3f(0.25f, 1, 0.25f));
		
		ShaderManager.bindShader(shader);
		ShaderManager.loadVector4(shader, "lightColorIn", (currColor = colorizer.getNext(currColor)).getColorAsVector());
		ShaderManager.unbindShader();
		
		ModelManager.renderModel(model, 0, shader, transform, window.getPerspectiveProjectionMatrix(), "transformation", "projection");
		ModelManager.renderModel(logo, 0, logoShader, logoTransform, window.getPerspectiveProjectionMatrix(), "transformation", "projection");
		
		ModelManager.renderModelWithColor(ModelManager.getSquareModelID(), testTexture, ShaderManager.getTextureShaderID(), testTransform, GLHelper.initBasicOrthographicProjectionMatrix(), new Color(1, 1, 1, 0.5f), "transformation", "projection", "color");
		
		ModelManager.finishRendering();
		
		handleInput();
		
		window.finishRender();
	}

	@Override
	public void setUpRender() {
		NetworkManager.registerListenerClass(this);
		windowID = DisplayManager.generateDisplays();
		window = DisplayManager.initializeDisplay(windowID, 70.0f, 1280, 720, 0.002f, 500.0f, true);
		
		ShaderManager.bindShader(shader);
		ShaderManager.loadMatrix4(shader, "projection", window.getPerspectiveProjectionMatrix());
		ShaderManager.loadMatrix4(shader, "transformation", new Matrix4f());
		ShaderManager.loadVector3(shader, "lightVectorIn", new Vector3f(-50, 30, 10));
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
		
	}

	@Override
	public long getRenderSleepInterval() {
		return 16;
	}

	@Override
	public int getRenderFrameskipTreshold() {
		return 50;
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
	private boolean Qw, Ew;
	
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
		boolean Q = Keyboard.isKeyDown(window, GLFW.GLFW_KEY_Q);
		boolean E = Keyboard.isKeyDown(window, GLFW.GLFW_KEY_E);
		
		if (Q && !E) {
			if (!Qw) {
				Qw = true;
				connect();
			}
		}else{
			Qw = false;
		}
		if (E && !Q) {
			if (!Ew) {
				Ew = true;
				disconnect();
			}
		}else{
			Ew = false;
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
}
