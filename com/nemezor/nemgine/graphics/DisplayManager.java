package com.nemezor.nemgine.graphics;

import java.util.HashMap;
import java.util.Iterator;

import com.nemezor.nemgine.graphics.util.Display;
import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.misc.Registry;

public class DisplayManager {

	private DisplayManager() {}

	private static HashMap<Integer, Display> displays = new HashMap<Integer, Display>();
	private static int displayCounter = 0;
	
	public static synchronized int generateDisplays() {
		if (Nemgine.getSide().isServer()) {
			return Registry.INVALID;
		}
		displayCounter++;
		displays.put(displayCounter, new Display(Registry.INVALID));
		return displayCounter;
	}
	
	public static Display initializeDisplay(int id, float fieldOfView, int width, int height, float zNear, float zFar, boolean resizable) {
		Display display = displays.get(id);
		if (display == null || display.getStatus() != Registry.INVALID) {
			return null;
		}
		display.initialize(fieldOfView, width, height, zNear, zFar, resizable);
		display.setStatus(0);
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
		displays.clear();
	}
}
