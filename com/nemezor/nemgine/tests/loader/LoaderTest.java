package com.nemezor.nemgine.tests.loader;

import java.awt.Font;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.nemezor.nemgine.debug.DebugColorizer;
import com.nemezor.nemgine.graphics.DisplayManager;
import com.nemezor.nemgine.graphics.FontManager;
import com.nemezor.nemgine.graphics.GLHelper;
import com.nemezor.nemgine.graphics.ModelManager;
import com.nemezor.nemgine.graphics.ShaderManager;
import com.nemezor.nemgine.graphics.TextureManager;
import com.nemezor.nemgine.graphics.util.Camera;
import com.nemezor.nemgine.graphics.util.Display;
import com.nemezor.nemgine.graphics.util.GLResourceEvent;
import com.nemezor.nemgine.graphics.util.OpenGLResources;
import com.nemezor.nemgine.main.Application;
import com.nemezor.nemgine.main.IMainRenderLoop;
import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.misc.Color;
import com.nemezor.nemgine.misc.Platform;
import com.nemezor.nemgine.misc.Registry;

public class LoaderTest implements IMainRenderLoop {

	int windowID;
	Display window;
	private int shader;
	private int logoShader;
	private int model;
	private int logo;
	private int angle = 0;
	private int testTexture;
	private int font;

	int[] textureIDs = new int[8000];
	int[] modelIDs = new int[10000];
	int[] shaderIDs = new int[2000];
	int[] fontIDs = new int[10];
	
	private DebugColorizer colorizer = new DebugColorizer(0);
	private Color currColor = new Color(0xFFFFFFFF);
	private Camera cam;
	
	@Application(name="Loader Test", path="tests/loader", contained=true)
	public void entry() {
		int thread = Nemgine.generateThreads("Render", true);
		Nemgine.bindRenderLoop(thread, this);
		Nemgine.startThread(thread);
		cam = new Camera(new Vector3f(), new Vector3f());
		Nemgine.printThreadKeepUpWarnings(false);
	}
	
	@OpenGLResources
	public void load(GLResourceEvent e) {
		if (e == GLResourceEvent.GENERATE_IDS) {
			
			shader = ShaderManager.generateShaders();
			logoShader = ShaderManager.generateShaders();
			model = ModelManager.generateModels();
			logo = ModelManager.generateModels();
			testTexture = TextureManager.generateTextures();
			font = FontManager.generateFonts();
			for (int i = 0; i < textureIDs.length; i++) {
				textureIDs[i] = TextureManager.generateTextures();
			}
			for (int i = 0; i < modelIDs.length; i++) {
				modelIDs[i] = ModelManager.generateModels();
			}
			for (int i = 0; i < shaderIDs.length; i++) {
				shaderIDs[i] = ShaderManager.generateShaders();
			}
			for (int i = 0; i < fontIDs.length; i++) {
				fontIDs[i] = FontManager.generateFonts();
			}
			
		}else if (e == GLResourceEvent.LOAD_FONTS) {
			
			FontManager.initializeFont(font, "Monospaced", Font.PLAIN, 5);
			for (int i = 0; i < fontIDs.length; i++) {
				FontManager.initializeFont(fontIDs[i], "Monospaced", Font.PLAIN, 16);
			}
			
		}else if (e == GLResourceEvent.LOAD_MODELS) {
			
			ModelManager.initializeModel(model, "dragon.obj");
			ModelManager.initializeModel(logo, "nemgine.obj");
			for (int i = 0; i < modelIDs.length; i++) {
				ModelManager.initializeModel(modelIDs[i], "com/nemezor/nemgine/tests/test_models/test_" + (i % 1000) + ".obj");
			}
			
		}else if (e == GLResourceEvent.LOAD_SHADERS) {
			
			ShaderManager.initializeShader(shader, "com/nemezor/nemgine/tests/dragon.vert", 
												   "com/nemezor/nemgine/tests/dragon.frag", 
												   new String[] {"projection", "transformation", "lightVectorIn", "lightColorIn"}, 
												   new String[] {"position", "normal"}, new int[] {0, 2});
			ShaderManager.initializeShader(logoShader, "com/nemezor/nemgine/tests/logo.vert", 
												   "com/nemezor/nemgine/tests/logo.frag", 
												   new String[] {"projection", "transformation"}, 
												   new String[] {"position"}, new int[] {0});
			for (int i = 0; i < shaderIDs.length; i++) {
				ShaderManager.initializeShader(shaderIDs[i], "com/nemezor/nemgine/tests/test_shaders/test_" + (i % 1000) + ".vertex", 
						"com/nemezor/nemgine/tests/test_shaders/test_" + (i % 1000) + ".fragment", new String[] {"projection", "transformation"}, new String[] {"position"}, new int[] {0});
			}
			
		}else if (e == GLResourceEvent.LOAD_TEXTURES) {
			
			TextureManager.initializeTextureFile(testTexture, "com/nemezor/nemgine/tests/test_texture.png");
			for (int i = 0; i < textureIDs.length; i++) {
				TextureManager.initializeTextureFile(textureIDs[i], "com/nemezor/nemgine/tests/test_images/test_" + (i % 1000) + ".png");
			}
		}
	}
	
	public static void main(String[] args) {
		Nemgine.start(args, LoaderTest.class);
	}

	@Override
	public void render() {
		DisplayManager.switchDisplay(windowID);
		if (window.closeRequested()) {
			Nemgine.shutDown();
		}
		window.prepareRender();
		
		window.fill(new Color(0x0000FFFF));
		
		Matrix4f transform = GLHelper.initTransformationMatrix(cam, new Vector3f(0, -5, -25), new Vector3f(0, (float)Math.toRadians(angle), 0), new Vector3f(1f, 1f, 1f));
		Matrix4f logoTransform = GLHelper.initTransformationMatrix(cam, new Vector3f(-15, 10, -30), new Vector3f((float)Math.toRadians(15 + angle), (float)Math.toRadians(25 + angle), (float)Math.toRadians(15)), new Vector3f(1, 1, 1));
		
		ShaderManager.bindShader(shader);
		ShaderManager.loadVector4(shader, "lightColorIn", (currColor = colorizer.getNext(currColor)).getColorAsVector());
		ShaderManager.unbindShader();
		
		ModelManager.renderModel(model, 0, shader, transform, window.getPerspectiveProjectionMatrix(), "transformation", "projection");

		ModelManager.renderModel(logo, 0, logoShader, logoTransform, window.getPerspectiveProjectionMatrix(), "transformation", "projection");
		
		FontManager.drawString(FontManager.getDefaultFontID20(), 20, 40, (Platform.getUsedMemory() / 1048576) + "/" + (Platform.getAllocatedMemory() / 1048576) + "MB", currColor.invert(), new Matrix4f(), GLHelper.initOrthographicProjectionMatrix(0, window.getWidth(), 0, window.getHeight(), 0, 1), Registry.INVALID, Registry.INVALID);
		
		ModelManager.finishRendering();
		angle++;
		
		window.finishRender();
	}

	@Override
	public void setUpRender() {
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
