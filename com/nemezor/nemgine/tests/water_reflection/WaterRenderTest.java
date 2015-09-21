package com.nemezor.nemgine.tests.water_reflection;

import org.lwjgl.LWJGLException;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.nemezor.nemgine.graphics.DisplayManager;
import com.nemezor.nemgine.graphics.GLHelper;
import com.nemezor.nemgine.graphics.ModelManager;
import com.nemezor.nemgine.graphics.ShaderManager;
import com.nemezor.nemgine.main.Application;
import com.nemezor.nemgine.main.IMainRenderLoop;
import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.main.NemgineLoader;

public class WaterRenderTest implements IMainRenderLoop {

	private int shader;
	private int model;
	private int angle = 0;
	
	@Application(name="Water Renderer", width=800, height=450, path="tests/water", contained=true)
	public void entry() {
		NemgineLoader.updateState("Initializing");
		int thread = Nemgine.generateThreads("Render", true);
		Nemgine.bindRenderLoop(thread, this);
		Nemgine.startThread(thread);
	}
	
	@Override
	public void render() {
		if (DisplayManager.closeRequested()) {
			Nemgine.shutDown();
		}
		DisplayManager.resize();
		DisplayManager.prepareRender();
		
		Matrix4f transform = GLHelper.initTransformationMatrix(new Vector3f(0, -5, -25), new Vector3f(0, (float)Math.toRadians(angle), 0), new Vector3f(1, 1, 1));
		ModelManager.renderModel(model, 0, shader, transform, GLHelper.getCurrentPerspectiveProjectionMatrix(), "transformation", "projection");
		ModelManager.finishRendering();
		
		DisplayManager.finishRender();
		angle++;
	}
	
	@Override
	public void setUpRender() {
		try {
			DisplayManager.initialize(70.0f, 0.002f, 500.0f);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void cleanUpRender() {
		Nemgine.exit(0);
	}

	@Override
	public void loadResources() {
		NemgineLoader.updateState("Loading Shaders");
		
		shader = ShaderManager.generateShaders();
		ShaderManager.initializeShader(shader, "com/nemezor/nemgine/tests/water_reflection/vertex.shader", 
											   "com/nemezor/nemgine/tests/water_reflection/fragment.shader", 
											   new String[] {"projection", "transformation", "light"}, 
											   new String[] {"position", "texCoords", "normal"}, new int[] {0, 1, 2});
		ShaderManager.bindShader(shader);
		ShaderManager.loadMatrix4(shader, "projection", GLHelper.getCurrentPerspectiveProjectionMatrix());
		ShaderManager.loadMatrix4(shader, "transformation", new Matrix4f());
		ShaderManager.loadVector3(shader, "light", new Vector3f(-50, 30, 10));
		ShaderManager.unbindShader();
		
		NemgineLoader.updateState("Loading Models");
		
		model = ModelManager.generateModels();
		ModelManager.initializeModel(model, "com/nemezor/nemgine/tests/water_reflection/dragon.obj");
	}

	@Override
	public void updateRenderSecond(int frames, long averageInterval) {
		DisplayManager.changeTitle("Water Renderer | FPS: " + frames + " | AVG: " + averageInterval + "ms");
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
		Nemgine.start(args, WaterRenderTest.class);
	}
}
