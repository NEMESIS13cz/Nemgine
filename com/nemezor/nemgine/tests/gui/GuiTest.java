package com.nemezor.nemgine.tests.gui;

import com.nemezor.nemgine.graphics.DisplayManager;
import com.nemezor.nemgine.graphics.util.Display;
import com.nemezor.nemgine.graphics.util.GLResourceEvent;
import com.nemezor.nemgine.graphics.util.OpenGLResources;
import com.nemezor.nemgine.main.Application;
import com.nemezor.nemgine.main.IMainRenderLoop;
import com.nemezor.nemgine.main.Nemgine;

public class GuiTest implements IMainRenderLoop {

	int windowID;
	Display window;
	
	@Application(name="GUI Test", path="tests/gui", contained=true)
	public void entry() {
		int thread = Nemgine.generateThreads("Main", false);
		Nemgine.printThreadKeepUpWarnings(false);
		Nemgine.bindRenderLoop(thread, this);
		Nemgine.startThread(thread);
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
		
		
		
		window.finishRender();
	}

	@Override
	public void setUpRender() {
		windowID = DisplayManager.generateDisplays();
		window = DisplayManager.initializeDisplay(windowID, 70.0f, 1280, 720, 0.1f, 1500000.0f, true);
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
