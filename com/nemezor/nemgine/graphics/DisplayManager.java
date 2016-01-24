package com.nemezor.nemgine.graphics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

import com.nemezor.nemgine.graphics.util.Display;
import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.misc.Registry;

public class DisplayManager {

	private DisplayManager() {}

	private static HashMap<Integer, Display> displays = new HashMap<Integer, Display>();
	private static int displayCounter = 0;
	private static int currentDisplay = 0;
	
	public static synchronized int generateDisplays() {
		if (Nemgine.getSide().isServer()) {
			return Registry.INVALID;
		}
		displayCounter++;
		displays.put(displayCounter, new Display(Registry.INVALID));
		return displayCounter;
	}
	
	public static Display initializeDisplay(int id, float fieldOfView, int width, int height, float zNear, float zFar, boolean resizable) {
		return initializeDisplayWithCustomShare(id, fieldOfView, width, height, zNear, zFar, resizable, Registry.INVALID);
	}
	
	public static Display initializeDisplayWithCustomShare(int id, float fieldOfView, int width, int height, float zNear, float zFar, boolean resizable, long share) {
		Display display = displays.get(id);
		if (display == null || display.getStatus() != Registry.INVALID) {
			return null;
		}
		display.initialize(fieldOfView, width, height, zNear, zFar, resizable, share);
		display.setStatus(0);
		currentDisplay = id;
		ModelManager.reloadModels();
		return display;
	}

	public static void dispose(int id) {
		if (Nemgine.getSide().isServer()) {
			return;
		}
		Display display = displays.get(id);
		if (display == null || display.getStatus() == Registry.INVALID) {
			return;
		}
		display.dispose();
		if (currentDisplay == id) {
			currentDisplay = Registry.INVALID;
		}
		displays.remove(id);
	}
	
	public static void disposeAll() {
		if (Nemgine.getSide().isServer()) {
			return;
		}
		Iterator<Integer> keys = displays.keySet().iterator();
		
		while (keys.hasNext()) {
			Display display = displays.get(keys.next());
			if (display.getStatus() != Registry.INVALID) {
				display.dispose();
			}
		}
		currentDisplay = Registry.INVALID;
		displays.clear();
	}
	
	public static Display getDisplay(int id) {
		if (Nemgine.getSide().isServer()) {
			return null;
		}
		Display display = displays.get(id);
		if (display == null || display.getStatus() == Registry.INVALID) {
			return null;
		}
		return display;
	}
	
	public static void switchDisplay(int newDisplay) {
		if (Nemgine.getSide().isServer()) {
			return;
		}
		if (newDisplay == Registry.INVALID) {
			GLFW.glfwMakeContextCurrent(MemoryUtil.NULL);
			currentDisplay = Registry.INVALID;
		}else{
			GLFW.glfwMakeContextCurrent(getDisplay(newDisplay).getGLFWId());
			currentDisplay = newDisplay;
		}
	}
	
	public static int getCurrentDisplayID() {
		return currentDisplay;
	}
	
	protected static Set<Integer> getAllIds() {
		return displays.keySet();
	}
}
