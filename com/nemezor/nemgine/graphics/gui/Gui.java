package com.nemezor.nemgine.graphics.gui;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.nemezor.nemgine.graphics.util.Display;
import com.nemezor.nemgine.input.InputCharacter;
import com.nemezor.nemgine.input.Mouse;
import com.nemezor.nemgine.misc.Color;
import com.nemezor.nemgine.misc.Registry;

public abstract class Gui {
	
	public static Color primaryColor = Registry.GUI_DEFAULT_PRIMARY_COLOR;
	public static Color secondaryColor = Registry.GUI_DEFAULT_SECONDARY_COLOR;
	public static Color tertiaryColor = Registry.GUI_DEFAULT_TERTIARY_COLOR;
	public static Color quaternaryColor = Registry.GUI_DEFAULT_QUATERNARY_COLOR;
	public static Color primaryAccentColor = Registry.GUI_DEFAULT_PRIMARY_ACCENT_COLOR;
	public static Color secondaryAccentColor = Registry.GUI_DEFAULT_SECONDARY_ACCENT_COLOR;
	
	private HashMap<String, IGuiComponent> components = new HashMap<String, IGuiComponent>();
	private ArrayList<IGuiKeyListener> keyListeners = new ArrayList<IGuiKeyListener>();
	private boolean firstRender = true;
	
	public abstract void populate(int rasterWidth, int rasterHeight);
	
	public final void render(Display window) {
		if (firstRender) {
			resize(window);
			firstRender = false;
		}else if (window.displayResized()) {
			resize(window);
		}
		update(window);

		GL11.glDisable(GL11.GL_DEPTH_TEST);
		for (IGuiComponent c : components.values()) {
			c.render(window);
		}
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
	public final boolean remove(String name, IGuiComponent component) {
		if (components.containsKey(name)) {
			IGuiComponent c = components.remove(name);
			if (c instanceof IGuiKeyListener) {
				keyListeners.remove((IGuiKeyListener)c);
			}
			return true;
		}
		return false;
	}
	
	public final boolean add(String name, IGuiComponent component) {
		if (components.containsKey(name)) {
			return false;
		}
		components.put(name, component);
		if (component instanceof IGuiKeyListener) {
			keyListeners.add((IGuiKeyListener)component);
		}
		return true;
	}
	
	private final void resize(Display window) {
		for (IGuiComponent c : components.values()) {
			c.resize(window);
		}
	}
	
	private final void update(Display window) {
		Point mouse = Mouse.getMousePosition(window);
		boolean left = Mouse.isButtonDown(window, 0);
		boolean right = Mouse.isButtonDown(window, 1);
		for (IGuiComponent c : components.values()) {
			c.update(window, (int)mouse.getX(), (int)mouse.getY(), left, right);
		}
	}
	
	public final void onKeyEvent(int key, int action) {
		if (action == GLFW.GLFW_RELEASE) {
			return;
		}
		InputCharacter c = new InputCharacter(key);
		for (IGuiKeyListener l : keyListeners) {
			l.charEvent(c);
		}
	}
	
	public final void onCharEvent(char character) {
		InputCharacter c = new InputCharacter(character);
		for (IGuiKeyListener l : keyListeners) {
			l.charEvent(c);
		}
	}
	
	public static final void setColorTheme(Color primary, Color secondary, Color tertiary, Color quaternary, Color primaryAccent, Color secondaryAccent) {
		primaryColor = primary.clone();
		secondaryColor = secondary.clone();
		tertiaryColor = tertiary.clone();
		quaternaryColor = quaternary.clone();
		primaryAccentColor = primaryAccent.clone();
		secondaryAccentColor = secondaryAccent.clone();
	}
}
