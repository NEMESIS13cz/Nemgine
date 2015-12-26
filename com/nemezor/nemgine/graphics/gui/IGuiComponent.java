package com.nemezor.nemgine.graphics.gui;

import com.nemezor.nemgine.misc.RenderAttributes;

public interface IGuiComponent {

	public void update();
	
	public void render();
	
	public void initialize();
	
	public void resize();
	
	public RenderAttributes getRenderAttributes();
	
	public void dispose();
}
