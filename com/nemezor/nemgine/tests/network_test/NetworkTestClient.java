package com.nemezor.nemgine.tests.network_test;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.nemezor.nemgine.console.Console;
import com.nemezor.nemgine.debug.DebugPacket;
import com.nemezor.nemgine.graphics.DisplayManager;
import com.nemezor.nemgine.graphics.FrameBufferManager;
import com.nemezor.nemgine.graphics.GLHelper;
import com.nemezor.nemgine.graphics.ModelManager;
import com.nemezor.nemgine.graphics.ShaderManager;
import com.nemezor.nemgine.graphics.util.Camera;
import com.nemezor.nemgine.graphics.util.FrameBuffer;
import com.nemezor.nemgine.main.Application;
import com.nemezor.nemgine.main.IMainRenderLoop;
import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.misc.Logger;
import com.nemezor.nemgine.network.Address;
import com.nemezor.nemgine.network.IPacket;
import com.nemezor.nemgine.network.Network;
import com.nemezor.nemgine.network.NetworkInfo;
import com.nemezor.nemgine.network.NetworkManager;
import com.nemezor.nemgine.network.NetworkObject;

public class NetworkTestClient implements IMainRenderLoop {

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
	
	@Application(name="Network Test - Client", width=800, height=450, path="tests/network", contained=true)
	public void entry() {
		int thread = Nemgine.generateThreads("Render", true);
		Nemgine.bindRenderLoop(thread, this);
		Nemgine.startThread(thread);
		cam = new Camera(new Vector3f(), new Vector3f());
	}
	
	@Network
	public void connectionEstablished(NetworkObject obj) {
		Logger.log("Connection established!");
	}
	
	@Network
	public void networkInfo(NetworkObject obj, NetworkInfo info) {
		Console.out.println(info.getType());
	}

	@Network
	public void packetReceived(NetworkObject obj, IPacket packet) {
		if (packet.getClass() == DebugPacket.class) {
			angle = Integer.valueOf(((DebugPacket)packet).getData());
		}
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
	}
	
	@Override
	public void setUpRender() {
		NetworkManager.registerListenerClass(this);
		int sockId = NetworkManager.generateClientSockets(3000);
		NetworkManager.connect(sockId, new Address("192.168.0.17", 1338));
		DisplayManager.setOpenGLConfiguration(70.0f, 0.002f, 500.0f);
		frame = FrameBufferManager.generateFrameBuffers();
		FrameBufferManager.initializeFrameBuffer(frame, 800, 450, true, false, true);
	}

	@Override
	public void cleanUpRender() {
		Nemgine.exit(0);
	}
	
	@Override
	public void loadResources() {
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
		
		model = ModelManager.generateModels();
		ModelManager.initializeModel(model, "com/nemezor/nemgine/tests/reflection/dragon.obj");
		logo = ModelManager.generateModels();
		ModelManager.initializeModel(logo, "com/nemezor/nemgine/tests/reflection/nemgine.obj");
		square = ModelManager.generateModels();
		ModelManager.initializeModel(square, "com/nemezor/nemgine/tests/reflection/square.obj");
	}

	@Override
	public void updateRenderSecond(int frames, long averageInterval) {
		DisplayManager.changeTitle("Network Test - Client | FPS: " + frames + " | AVG: " + averageInterval + "ms");
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
		Nemgine.start(args, NetworkTestClient.class);
	}
}
