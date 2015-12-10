package com.nemezor.nemgine.graphics;

import java.util.HashMap;
import java.util.Iterator;

import com.nemezor.nemgine.graphics.gui.Gui;
import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.misc.Registry;
import com.nemezor.nemgine.misc.Side;

public class GuiManager {

	private static int guiCount = 0;
	private static int currentGuiId = 0;
	private static Gui currentGui = null;
	private static HashMap<Integer, Gui> guis = new HashMap<Integer, Gui>();
	
	public static synchronized int generateGuis() {
		if (Nemgine.getSide() == Side.SERVER) {
			return Registry.INVALID;
		}
		guiCount++;
		guis.put(guiCount, new Gui(Registry.INVALID));
		return guiCount;
	}
	
	public static boolean initializeGui(int id) {
		if (currentGuiId == id || Nemgine.getSide() == Side.SERVER) {
			return false;
		}
		Gui gui = guis.get(id);
		if (gui == null || gui.getState() != Registry.INVALID) {
			return false;
		}
		gui.initialize();
		return true;
	}
	
	public static void bindGui(int id) {
		if (currentGuiId == id || Nemgine.getSide() == Side.SERVER) {
			return;
		}
		Gui gui = guis.get(id);
		if (gui == null || gui.getState() == Registry.INVALID) {
			return;
		}
		currentGuiId = id;
		currentGui = gui;
	}
	
	public static void unbind() {
		if (currentGuiId == 0 || Nemgine.getSide() == Side.SERVER) {
			return;
		}
		currentGuiId = 0;
		currentGui = null;
	}
	
	public static void dispose(int id) {
		if (Nemgine.getSide() == Side.SERVER) {
			return;
		}
		if (currentGuiId == id) {
			currentGuiId = 0;
			currentGui = null;
		}
		Gui gui = guis.get(id);
		if (gui == null) {
			return;
		}
		gui.dispose();
		guis.remove(id);
	}
	
	public static void disposeAll() {
		if (Nemgine.getSide() == Side.SERVER) {
			return;
		}
		if (currentGuiId != 0) {
			currentGuiId = 0;
			currentGui = null;
		}
		Iterator<Integer> iter = guis.keySet().iterator();
		
		while (iter.hasNext()) {
			Gui gui = guis.get(iter.next());
			if (gui == null) {
				continue;
			}
			gui.dispose();
		}
		guis.clear();
	}
	
	public static void render(int id) {
		if (currentGuiId != id || Nemgine.getSide() == Side.SERVER) {
			return;
		}
		currentGui.render();
	}
	
	public static void update(int id) {
		if (currentGuiId != id || Nemgine.getSide() == Side.SERVER) {
			return;
		}
		currentGui.update();
	}
}
