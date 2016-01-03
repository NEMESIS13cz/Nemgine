package com.nemezor.nemgine.graphics.gui;

import com.nemezor.nemgine.misc.RenderAttributes;

public interface IGuiComponent {

	public void update(float mouseX, float mouseY);
	
	public void render(float mouseX, float mouseY);
	
	public void initialize(int canvasWidth, int canvasHeight);
	
	public void resize(int width, int height);
	
	public RenderAttributes getRenderAttributes();
	
	public void dispose();
}
