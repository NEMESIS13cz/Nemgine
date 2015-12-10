package com.nemezor.nemgine.graphics.gui;

import java.util.ArrayList;

import com.nemezor.nemgine.graphics.DisplayManager;
import com.nemezor.nemgine.misc.RenderAttributes;

public class Gui {

	private int state;
	private ArrayList<IGuiComponent> components = new ArrayList<IGuiComponent>();
	
	public Gui(int state) {
		this.state = state;
	}
	
	public int getState() {
		return state;
	}
	
	public void initialize() {
		state = 0;
	}
	
	public void dispose() {
		for (IGuiComponent c : components) {
			c.dispose();
		}
	}
	
	public void render() {
		ArrayList<IGuiComponent> transparent = new ArrayList<IGuiComponent>();
		ArrayList<IGuiComponent> texturedTransparent = new ArrayList<IGuiComponent>();
		ArrayList<IGuiComponent> regular = new ArrayList<IGuiComponent>();
		ArrayList<IGuiComponent> textured = new ArrayList<IGuiComponent>();
		
		for (IGuiComponent c : components) {
			RenderAttributes a = c.getRenderAttributes();
			if (a.isTransparent()) {
				if (a.isTextured()) {
					texturedTransparent.add(c);
				}else{
					transparent.add(c);
				}
			}else{
				if (a.isTextured()) {
					textured.add(c);
				}else{
					regular.add(c);
				}
			}
		}
		
		for (IGuiComponent c : regular) {
			c.render();
		}
		for (IGuiComponent c : textured) {
			c.render();
		}
		DisplayManager.enableBlending();
		for (IGuiComponent c : transparent) {
			c.render();
		}
		for (IGuiComponent c : texturedTransparent) {
			c.render();
		}
		DisplayManager.disableBlending();
	}
	
	public void update() {
		for (IGuiComponent c : components) {
			c.update();
		}
	}
}
