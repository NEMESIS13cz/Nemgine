package com.nemezor.nemgine.tests.gpu_render;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.nemezor.nemgine.graphics.DisplayManager;
import com.nemezor.nemgine.graphics.GLHelper;
import com.nemezor.nemgine.graphics.ModelManager;
import com.nemezor.nemgine.graphics.ShaderManager;
import com.nemezor.nemgine.main.Application;
import com.nemezor.nemgine.main.IMainRenderLoop;
import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.misc.Color;
import com.nemezor.nemgine.misc.Side;

public class GPU_Render implements IMainRenderLoop {

	private static Matrix4f mat = GLHelper.initTransformationMatrix(new Vector3f(0, 0, -25), new Vector3f((float)Math.toRadians(90.0f), 0, 0), new Vector3f(1, 1, 1));
	private static Color color = new Color(0xFFFFFFFF);
	
	@Application(path="tests/gpu_render", width=1280, height=720, name="GPU Rendering Test", side=Side.CLIENT, contained=true)
	public void entry() {
		int thread = Nemgine.generateThreads("Render", false);
		Nemgine.bindRenderLoop(thread, this);
		Nemgine.startThread(thread);
	}
	
	public static void main(String[] args) {
		Nemgine.start(args, GPU_Render.class);
	}

	@Override
	public void render() {
		if (DisplayManager.closeRequested()) {
			Nemgine.shutDown();
		}
		DisplayManager.resize();
		DisplayManager.prepareRender();
		
		for (int i = 0; i < 100000; i++) {
			ModelManager.renderModelWithColor(ModelManager.getSquareModelID(), 0, ShaderManager.getColorShaderID(), mat, GLHelper.getCurrentPerspectiveProjectionMatrix(), color, "transformation", "projection", "color");
		}
		
		ModelManager.finishRendering();
		DisplayManager.finishRender();
	}

	@Override
	public void setUpRender() {
		DisplayManager.setOpenGLConfiguration(70.0f, 0.002f, 1000.0f);
	}

	@Override
	public void cleanUpRender() {
		Nemgine.exit(0);
	}

	@Override
	public void generateResources() {
		
	}

	@Override
	public void loadResources() {
		
	}

	@Override
	public void updateRenderSecond(int frames, long averageInterval) {
		DisplayManager.changeTitle("GPU Rendering Test | FPS: " + frames);
	}

	@Override
	public long getRenderSleepInterval() {
		return 0;
	}

	@Override
	public int getRenderFrameskipTreshold() {
		return -1;
	}
}
