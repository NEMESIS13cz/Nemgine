package com.nemezor.nemgine.graphics.gui;

import java.awt.Point;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;

import com.nemezor.nemgine.graphics.util.Display;
import com.nemezor.nemgine.input.Mouse;
import com.nemezor.nemgine.misc.Color;
import com.nemezor.nemgine.misc.Registry;

public abstract class Gui {
	
	public static Color primaryColor = Registry.GUI_DEFAULT_PRIMARY_COLOR;
	public static Color secondaryColor = Registry.GUI_DEFAULT_SECONDARY_COLOR;
	public static Color accentColor = Registry.GUI_DEFAULT_ACCENT_COLOR;
	
	private HashMap<String, IGuiComponent> components = new HashMap<String, IGuiComponent>();
	private boolean firstRender = true;
	
	public abstract void populate(int rasterWidth, int rasterHeight);
	
	public void render(Display window) {
		if (firstRender) {
			resize(window);
			firstRender = false;
		}
		if (window.displayResized()) {
			resize(window);
		}
		update(window);

		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glLineWidth(2);
		for (IGuiComponent c : components.values()) {
			c.render(window);
		}
		GL11.glLineWidth(1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
	public boolean remove(String name, IGuiComponent component) {
		if (components.containsKey(name)) {
			components.remove(name);
			return true;
		}
		return false;
	}
	
	public boolean add(String name, IGuiComponent component) {
		if (components.containsKey(name)) {
			return false;
		}
		components.put(name, component);
		return true;
	}
	
	private void resize(Display window) {
		for (IGuiComponent c : components.values()) {
			c.resize(window);
		}
	}
	
	private void update(Display window) {
		Point mouse = Mouse.getMousePosition(window);
		boolean left = Mouse.isButtonDown(window, 0);
		boolean right = Mouse.isButtonDown(window, 1);
		for (IGuiComponent c : components.values()) {
			c.update((int)mouse.getX(), (int)mouse.getY(), left, right);
		}
	}
	
	public static void setColorTheme(Color primary, Color secondary, Color accent) {
		primaryColor = primary.clone();
		secondaryColor = secondary.clone();
		accentColor = accent.clone();
	}
}
