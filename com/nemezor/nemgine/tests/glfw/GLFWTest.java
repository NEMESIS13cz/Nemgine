package com.nemezor.nemgine.tests.glfw;

import org.lwjgl.glfw.GLFW;

import com.nemezor.nemgine.graphics.DisplayManager;
import com.nemezor.nemgine.graphics.ModelManager;
import com.nemezor.nemgine.graphics.ShaderManager;
import com.nemezor.nemgine.graphics.TextureManager;
import com.nemezor.nemgine.graphics.util.Display;
import com.nemezor.nemgine.graphics.util.GLResourceEvent;
import com.nemezor.nemgine.graphics.util.OpenGLResources;
import com.nemezor.nemgine.main.Application;
import com.nemezor.nemgine.main.IMainRenderLoop;
import com.nemezor.nemgine.main.Nemgine;

public class GLFWTest implements IMainRenderLoop {

	int[] textureIDs = new int[2000];
	int[] modelIDs = new int[1000];
	int[] shaderIDs = new int[1000];
	int windowID;
	Display window;
	int windowID2;
	Display window2;
	
	@Application(name="GLFW Test", path="tests/glfw", contained=true)
	public void entry() {
		int thread = Nemgine.generateThreads("Render", true);
		Nemgine.bindRenderLoop(thread, this);
		Nemgine.startThread(thread);
	}
	
	@OpenGLResources
	public void load(GLResourceEvent e) {
		if (e == GLResourceEvent.GENERATE_IDS) {
			for (int i = 0; i < textureIDs.length; i++) {
				textureIDs[i] = TextureManager.generateTextures();
			}
			for (int i = 0; i < modelIDs.length; i++) {
				modelIDs[i] = ModelManager.generateModels();
			}
			for (int i = 0; i < shaderIDs.length; i++) {
				shaderIDs[i] = ShaderManager.generateShaders();
			}
		}else if (e == GLResourceEvent.LOAD_RESOURCES) {
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
	}
	
	public static void main(String[] args) {
		Nemgine.start(args, GLFWTest.class);
	}

	@Override
	public void render() {
		GLFW.glfwMakeContextCurrent(window.getGLFWId());
		if (window.closeRequested()) {
			Nemgine.shutDown();
		}
		window.prepareRender();
		
		// render stuff on first window
		
		window.finishRender();
		if (!window2.isInvalid()) {
			GLFW.glfwMakeContextCurrent(window2.getGLFWId());
			window2.prepareRender();
			
			// render stuff on second window
			
			window2.finishRender();
			if (window2.closeRequested()) {
				DisplayManager.dispose(windowID2);
			}
		}
	}

	@Override
	public void setUpRender() {
		windowID = DisplayManager.generateDisplays();
		window = DisplayManager.initializeDisplay(windowID, 70.0f, 600, 400, 0.002f, 500.0f, true);
		windowID2 = DisplayManager.generateDisplays();
		window2 = DisplayManager.initializeDisplay(windowID2, 70.0f, 600, 400, 0.002f, 500.0f, true);
		window2.changeTitle(Nemgine.getApplicationName() + " | Window: 2");
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
		return 16;
	}

	@Override
	public int getRenderFrameskipTreshold() {
		return 20;
	}
}
