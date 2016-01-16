package com.nemezor.nemgine.tests.loading;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.nemezor.nemgine.graphics.DisplayManager;
import com.nemezor.nemgine.graphics.GLHelper;
import com.nemezor.nemgine.graphics.ModelManager;
import com.nemezor.nemgine.graphics.ShaderManager;
import com.nemezor.nemgine.graphics.TextureManager;
import com.nemezor.nemgine.graphics.util.Camera;
import com.nemezor.nemgine.main.Application;
import com.nemezor.nemgine.main.IMainRenderLoop;
import com.nemezor.nemgine.main.Nemgine;

public class LoaderTest implements IMainRenderLoop {

	private int shader;
	private int logoShader;
	private int model;
	private int logo;
	private int angle = 0;
	private int square;
	
	private Camera cam;

	int[] textureIDs = new int[2000];
	int[] modelIDs = new int[1000];
	int[] shaderIDs = new int[1000];
	
	@Application(name="Loader Test", width=1280, height=720, path="tests/water", contained=true)
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

		ModelManager.renderModel(model, 0, shader, transform, GLHelper.getCurrentPerspectiveProjectionMatrix(), "transformation", "projection");
		ModelManager.renderModel(logo, 0, logoShader, logoTransform, GLHelper.getCurrentPerspectiveProjectionMatrix(), "transformation", "projection");

		ModelManager.finishRendering();
		DisplayManager.finishRender();
		angle++;
	}
	
	@Override
	public void setUpRender() {
		System.gc();
		DisplayManager.setOpenGLConfiguration(70.0f, 0.002f, 500.0f);
	}

	@Override
	public void cleanUpRender() {
		Nemgine.exit(0);
	}

	@Override
	public void generateResources() {
		shader = ShaderManager.generateShaders();
		logoShader = ShaderManager.generateShaders();
		model = ModelManager.generateModels();
		logo = ModelManager.generateModels();
		square = ModelManager.generateModels();
		for (int i = 0; i < textureIDs.length; i++) {
			textureIDs[i] = TextureManager.generateTextures();
		}
		for (int i = 0; i < modelIDs.length; i++) {
			modelIDs[i] = ModelManager.generateModels();
		}
		for (int i = 0; i < shaderIDs.length; i++) {
			shaderIDs[i] = ShaderManager.generateShaders();
		}
	}
	
	@Override
	public void loadResources() {
		ShaderManager.initializeShader(shader, "com/nemezor/nemgine/tests/reflection/vertex.shader", 
											   "com/nemezor/nemgine/tests/reflection/fragment.shader", 
											   new String[] {"projection", "transformation", "light"}, 
											   new String[] {"position", "normal"}, new int[] {0, 2});
		ShaderManager.initializeShader(logoShader, "com/nemezor/nemgine/tests/reflection/logo.vertex", 
												   "com/nemezor/nemgine/tests/reflection/logo.fragment", 
												   new String[] {"projection", "transformation"}, 
												   new String[] {"position"}, new int[] {0});
		
		ShaderManager.bindShader(shader);
		ShaderManager.loadMatrix4(shader, "projection", GLHelper.getCurrentPerspectiveProjectionMatrix());
		ShaderManager.loadMatrix4(shader, "transformation", new Matrix4f());
		ShaderManager.loadVector3(shader, "light", new Vector3f(-50, 30, 10));
		ShaderManager.bindShader(logoShader);
		ShaderManager.loadMatrix4(logoShader, "projection", GLHelper.getCurrentPerspectiveProjectionMatrix());
		ShaderManager.loadMatrix4(logoShader, "transformation", new Matrix4f());
		ShaderManager.unbindShader();
		
		ModelManager.initializeModel(model, "com/nemezor/nemgine/tests/reflection/dragon.obj");
		ModelManager.initializeModel(logo, "com/nemezor/nemgine/tests/reflection/nemgine.obj");
		ModelManager.initializeModel(square, "com/nemezor/nemgine/tests/reflection/square.obj");
		
		for (int i = 0; i < 1000; i++) {
			ModelManager.initializeModel(modelIDs[i], "com/nemezor/nemgine/tests/test_models/test_" + i + ".obj");
		}
		for (int i = 0; i < 1000; i++) {
			ShaderManager.initializeShader(shaderIDs[i], "com/nemezor/nemgine/tests/test_shaders/test_" + i + ".vertex", 
					"com/nemezor/nemgine/tests/test_shaders/test_" + i + ".fragment", new String[] {"projection", "transformation"}, new String[] {"position"}, new int[] {0});
		}
		for (int i = 0; i < 2000; i++) {
			TextureManager.initializeTexture(textureIDs[i], "com/nemezor/nemgine/tests/test_images/test_" + i + ".png");
		}
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
		Nemgine.start(args, LoaderTest.class);
	}
}
