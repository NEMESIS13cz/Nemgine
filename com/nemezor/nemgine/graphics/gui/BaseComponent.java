package com.nemezor.nemgine.graphics.gui;

import java.awt.Rectangle;

import com.nemezor.nemgine.input.Mouse;
import com.nemezor.nemgine.misc.RenderAttributes;

public class BaseComponent implements IGuiComponent {

	private float x, y, w, h;  // x, y, width, height
	private boolean l, r, t, b; // anchors - left, right, top, bottom
	private boolean pressedRight, pressedLeft, hovering;
	
	public BaseComponent(Rectangle square, boolean left, boolean right, boolean top, boolean bottom) {
		x = square.x;
		y = square.y;
		w = square.width;
		h = square.height;
		
		l = left;
		r = right;
		t = top;
		b = bottom;
	}
	
	@Override
	public void update() {
		float x = Mouse.getX();
		float y = Mouse.getY();
		
		if (x > this.x + this.w || x < this.x || y > this.y + this.h || y < this.y) { // Mouse outside
			if (hovering) {
				hovering = false;
				mouseLeft(x, y);
				if (pressedLeft && Mouse.isButtonDown(Mouse.LEFT_MOUSE_BUTTON)) {
					
				}
			}
		}else{ // Mouse inside
			if (!hovering) {
				hovering = true;
				mouseEntered(x, y);
			}
		}
	}

	@Override
	public void render() {
		
	}

	@Override
	public void initialize() {
		
	}

	@Override
	public void resize() {
		
	}
	
	@Override
	public RenderAttributes getRenderAttributes() {
		return null;
	}

	@Override
	public void dispose() {
		
	}
	
	// Events
	
	void mouseEntered(float mouseX, float mouseY) {
		
	}
	
	void mouseLeft(float mouseX, float mouseY) {
		
	}
	
	void mouseRightButtonPressed(float mouseX, float mouseY) {
		
	}
	
	void mouseRightButtonReleased(float mouseX, float mouseY) {
		
	}
	
	void mouseRightButtonReleasedOutside(float mouseX, float mouseY) {
		
	}
	
	void mouseLeftButtonPressed(float mouseX, float mouseY) {
		
	}
	
	void mouseLeftButtonReleased(float mouseX, float mouseY) {
		
	}
	
	void mouseLeftButtonReleasedOutside(float mouseX, float mouseY) {
		
	}
}
