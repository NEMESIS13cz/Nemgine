package com.nemezor.nemgine.graphics.gui;

import java.util.ArrayList;

import com.nemezor.nemgine.graphics.GLHelper;
import com.nemezor.nemgine.input.Mouse;
import com.nemezor.nemgine.misc.RenderAttributes;

public class Gui { //TODO FIX ALL THE THINGS

	private int state;
	private ArrayList<IGuiComponent> components = new ArrayList<IGuiComponent>();
	protected int width, height, canvasWidth, canvasHeight;
	
	public Gui(int state, int cW, int cH) {
		this.state = state;
		this.canvasWidth = cW;
		this.canvasHeight = cH;
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
	
	protected void addComponent(IGuiComponent c) {
		c.initialize(canvasWidth, canvasHeight);
		c.resize(width, height);
		components.add(c);
	}
	
	public void resize() {
		//width = Display.getWidth();
		//height = Display.getHeight();
		
		for (IGuiComponent c : components) {
			c.resize(width, height);
		}
	}
	
	public void render() {
		float mouseX = Mouse.getAbsoluteX();
		float mouseY = Mouse.getAbsoluteY();
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
			c.render(mouseX, mouseY);
		}
		for (IGuiComponent c : textured) {
			c.render(mouseX, mouseY);
		}
		GLHelper.enableBlending();
		for (IGuiComponent c : transparent) {
			c.render(mouseX, mouseY);
		}
		for (IGuiComponent c : texturedTransparent) {
			c.render(mouseX, mouseY);
		}
		GLHelper.disableBlending();
	}
	
	public void update() {
		float mouseX = Mouse.getAbsoluteX();
		float mouseY = Mouse.getAbsoluteY();
		
		for (IGuiComponent c : components) {
			c.update(mouseX, mouseY);
		}
	}
}
