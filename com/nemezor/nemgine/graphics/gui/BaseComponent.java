package com.nemezor.nemgine.graphics.gui;

import java.awt.Rectangle;

import org.lwjgl.util.vector.Matrix4f;

import com.nemezor.nemgine.input.Mouse;
import com.nemezor.nemgine.misc.Color;
import com.nemezor.nemgine.misc.RenderAttributes;

public class BaseComponent implements IGuiComponent {

	private int x, y, fx, fy, w, h;  // initial x, y, farX, farY, width, height
	private int al, ar, at, ab; // absolute left, right, top, bottom
	private boolean l, r, t, b; // anchors left, right, top, bottom
	private boolean pressedRight, pressedLeft, hovering;
	private Matrix4f transformation = null;
	
	final Gui parentGui;
	
	public BaseComponent(Gui parent, Rectangle square, boolean left, boolean right, boolean top, boolean bottom) {
		x = square.x;
		y = square.y;
		fx = parent.canvasWidth - (square.x + square.width);
		fy = parent.canvasHeight - (square.y + square.height);
		w = square.width;
		h = square.height;
		
		l = left;
		r = right;
		t = top;
		b = bottom;
		
		parentGui = parent;
	}
	
	@Override
	public void update(float x, float y) {
		if (x > ar || x < al || y > ab || y < at) {
			if (hovering) {
				hovering = false;
				mouseLeft(x, y);
				if (pressedLeft && Mouse.isButtonDown(Mouse.LEFT_MOUSE_BUTTON)) {
					pressedLeft = false;
					mouseLeftButtonReleased(x, y);
				}
				if (pressedRight && Mouse.isButtonDown(Mouse.RIGHT_MOUSE_BUTTON)) {
					pressedRight = false;
					mouseRightButtonReleased(x, y);
				}
			}
		}else{
			if (!hovering) {
				hovering = true;
				mouseEntered(x, y);
			}
			if (!pressedLeft && Mouse.isButtonDown(Mouse.LEFT_MOUSE_BUTTON)) {
				pressedLeft = true;
				mouseLeftButtonPressed(x, y);
			}
			if (!pressedRight && Mouse.isButtonDown(Mouse.RIGHT_MOUSE_BUTTON)) {
				pressedRight = true;
				mouseRightButtonPressed(x, y);
			}
		}
	}

	@Override
	public void render(float x, float y) {
		
	}

	@Override
	public void initialize(int cW, int cH) {
		
	}
	
	@Override
	public void resize(int width, int height) {
		if (l && r) {
			al = x;
			ar = width - fx;
		}else if (r) {
			al = width - (fx + w);
			ar = width - fx;
		}else{
			al = x;
			ar = x + w;
		}
		if (t && b) {
			at = y;
			ab = height - fy;
		}else if (b) {
			at = width - (fy + h);
			ab = width - fy;
		}else{
			at = y;
			ab = y + h;
		}
	}
	
	@Override
	public RenderAttributes getRenderAttributes() {
		return new RenderAttributes(new Color(0xFFFFFFFF), false);
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
	
	void mouseLeftButtonPressed(float mouseX, float mouseY) {
		
	}
	
	void mouseLeftButtonReleased(float mouseX, float mouseY) {
		
	}
}
