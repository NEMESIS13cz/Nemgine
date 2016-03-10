package com.nemezor.nemgine.tests.gui;

import org.lwjgl.util.vector.Matrix4f;

import com.nemezor.nemgine.graphics.DisplayManager;
import com.nemezor.nemgine.graphics.FontManager;
import com.nemezor.nemgine.graphics.gui.Gui;
import com.nemezor.nemgine.graphics.util.Display;
import com.nemezor.nemgine.graphics.util.GLResourceEvent;
import com.nemezor.nemgine.graphics.util.OpenGLResources;
import com.nemezor.nemgine.input.IKeyInput;
import com.nemezor.nemgine.main.Application;
import com.nemezor.nemgine.main.IMainRenderLoop;
import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.misc.Color;
import com.nemezor.nemgine.misc.Platform;

public class GuiTest implements IMainRenderLoop {

	private int windowID;
	private Display window;
	private Gui gui;
	
	@Application(name="GUI Test", path="tests/gui", contained=true)
	public void entry() {
		int thread = Nemgine.generateThreads("Main", false);
		Nemgine.printThreadKeepUpWarnings(false);
		Nemgine.bindRenderLoop(thread, this);
		Nemgine.startThread(thread);
		Nemgine.debug(false);
	}
	
	@OpenGLResources
	public void load(GLResourceEvent e) {
		
	}
	
	public static void main(String[] args) {
		Nemgine.start(args, GuiTest.class);
	}

	@Override
	public void render() {
		DisplayManager.switchDisplay(windowID);
		if (window.closeRequested()) {
			Nemgine.shutDown();
		}
		window.prepareRender();
		window.fill(Gui.primaryColor);
		
		FontManager.drawString(FontManager.getDefaultFontID(), 10, 300, Platform.getTotalPhysicalMemory() / 1048576 + "MB", new Color(0x000000FF), new Matrix4f(), window.get2DOrthographicProjectionMatrix(), 0, 0);
		FontManager.drawString(FontManager.getDefaultFontID(), 10, 330, Platform.getUsedPhysicalMemory() / 1048576 + "MB", new Color(0x000000FF), new Matrix4f(), window.get2DOrthographicProjectionMatrix(), 0, 0);
		FontManager.drawString(FontManager.getDefaultFontID(), 10, 360, Platform.getTotalSwapMemory() / 1048576 + "MB", new Color(0x000000FF), new Matrix4f(), window.get2DOrthographicProjectionMatrix(), 0, 0);
		FontManager.drawString(FontManager.getDefaultFontID(), 10, 390, Platform.getUsedSwapMemory() / 1048576 + "MB", new Color(0x000000FF), new Matrix4f(), window.get2DOrthographicProjectionMatrix(), 0, 0);
		
		gui.render(window);
		
		window.finishRender();
	}

	@Override
	public void setUpRender() {
		windowID = DisplayManager.generateDisplays();
		window = DisplayManager.initializeDisplay(windowID, 70.0f, 800, 600, 0.1f, 150.0f, true);
		window.setKeyHandler(new IKeyInput() {
			@Override
			public void keyEvent(int key, int scancode, int action, int mods) {
				gui.onKeyEvent(key, action);
			}
			@Override
			public void charEvent(char character) {
				
			}
			@Override
			public void charModsEvent(char character, int mods) {
				gui.onCharEvent(character);
			}
		});
		
		gui = new TestGui();
		gui.populate(800, 600);
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
