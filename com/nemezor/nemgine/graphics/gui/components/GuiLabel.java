package com.nemezor.nemgine.graphics.gui.components;

import java.awt.Rectangle;

import com.nemezor.nemgine.graphics.FontManager;
import com.nemezor.nemgine.graphics.gui.IGuiComponent;
import com.nemezor.nemgine.graphics.util.Display;
import com.nemezor.nemgine.misc.Anchors;
import com.nemezor.nemgine.misc.Color;
import com.nemezor.nemgine.misc.IGuiListener;
import com.nemezor.nemgine.misc.MouseButton;
import com.nemezor.nemgine.misc.Registry;

public class GuiLabel implements IGuiComponent {

	private int right, bottom;
	private int x, y;
	private int fontId = FontManager.getDefaultFontID20();
	private Color fontColor = Registry.GUI_DEFAULT_FONT_COLOR, hoverColor = Registry.GUI_DEFAULT_FONT_COLOR, pressedColor = Registry.GUI_DEFAULT_FONT_COLOR;
	private String text = "";
	private Anchors anch = Anchors.TOP_LEFT;
	private IGuiListener listener;
	private Rectangle bounds = new Rectangle(0, 0);
	private boolean pressedLeft = false, pressedRight = false;
	private boolean hover = false;
	
	public GuiLabel(int x, int y, int rasterWidth, int rasterHeight) {
		right = rasterWidth - x;
		bottom = rasterHeight - y;
		this.x = x;
		this.y = y;
	}
	
	public void setFont(int id) {
		this.fontId = id;
		this.bounds = FontManager.getStringBounds(fontId, text);
	}
	
	public void setText(String text) {
		this.text = text;
		this.bounds = FontManager.getStringBounds(fontId, text);
	}
	
	public void setColor(Color c) {
		this.fontColor = c.clone();
	}
	
	public void setHoverColor(Color c) {
		this.hoverColor = c.clone();
	}
	
	public void setPressedColor(Color c) {
		this.pressedColor = c.clone();
	}
	
	@Override
	public void setListener(IGuiListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void render(Display window) {
		if (pressedLeft || pressedRight) {
			FontManager.drawString(fontId, x, y + (int)bounds.getHeight(), text, pressedColor, window.getTransformationMatrix(), window.get2DOrthographicProjectionMatrix(), Registry.INVALID, Registry.INVALID);
		}else if (hover) {
			FontManager.drawString(fontId, x, y + (int)bounds.getHeight(), text, hoverColor, window.getTransformationMatrix(), window.get2DOrthographicProjectionMatrix(), Registry.INVALID, Registry.INVALID);
		}else{
			FontManager.drawString(fontId, x, y + (int)bounds.getHeight(), text, fontColor, window.getTransformationMatrix(), window.get2DOrthographicProjectionMatrix(), Registry.INVALID, Registry.INVALID);
		}
	}

	@Override
	public void update(Display window, int mouseX, int mouseY, boolean leftButton, boolean rightButton) {
		if (mouseX > x + bounds.getWidth() || mouseX < x || mouseY < y || mouseY > y + bounds.getHeight()) {
			if (hover && listener != null) {
				listener.onExit();
			}
			pressedRight = false;
			pressedLeft = false;
			hover = false;
		}else{
			if (!hover && listener != null) {
				listener.onEnter();
			}
			hover = true;
			if (leftButton) {
				if (!pressedLeft && listener != null) {
					listener.onPressed(MouseButton.LEFT);
				}
				pressedLeft = true;
			}else{
				if (pressedLeft && listener != null) {
					listener.onReleased(MouseButton.LEFT);
				}
				pressedLeft = false;
			}
			if (rightButton) {
				if (!pressedRight && listener != null) {
					listener.onPressed(MouseButton.RIGHT);
				}
				pressedRight = true;
			}else{
				if (pressedRight && listener != null) {
					listener.onReleased(MouseButton.RIGHT);
				}
				pressedRight = false;
			}
		}
	}

	@Override
	public void resize(Display window) {
		switch (anch) {
		case BOTTOM_LEFT_RIGHT:
		case BOTTOM:
		case BOTTOM_LEFT:
			y = window.getHeight() - bottom;
			break;
		case BOTTOM_RIGHT:
			y = window.getHeight() - bottom;
			x = window.getWidth() - right;
			break;
		case LEFT_RIGHT:
			break;
		case LEFT_RIGHT_TOP_BOTTOM:
			break;
		case RIGHT_TOP_BOTTOM:
			x = window.getWidth() - right;
		case LEFT_TOP_BOTTOM:
		case TOP_BOTTOM:
			break;
		case RIGHT:
		case TOP_RIGHT:
			x = window.getWidth() - right;
			break;
		case TOP_LEFT_RIGHT:
			break;
		default:
			break;
		}
	}
	
	@Override
	public void anchor(Anchors anchors) {
		this.anch = anchors;
	}
}
