package com.nemezor.nemgine.graphics.gui.components;

import java.awt.Rectangle;

import org.lwjgl.util.vector.Matrix4f;

import com.nemezor.nemgine.graphics.FontManager;
import com.nemezor.nemgine.graphics.ShaderManager;
import com.nemezor.nemgine.graphics.Tessellator;
import com.nemezor.nemgine.graphics.gui.Gui;
import com.nemezor.nemgine.graphics.gui.IGuiComponent;
import com.nemezor.nemgine.graphics.gui.IGuiKeyListener;
import com.nemezor.nemgine.graphics.util.Display;
import com.nemezor.nemgine.input.InputCharacter;
import com.nemezor.nemgine.misc.Anchors;
import com.nemezor.nemgine.misc.Color;
import com.nemezor.nemgine.misc.IGuiListener;
import com.nemezor.nemgine.misc.MouseButton;
import com.nemezor.nemgine.misc.Registry;

public class GuiTextBox implements IGuiComponent, IGuiKeyListener {

	private int left, right, top, bottom;
	private int x, y, width, height;
	private int nonMultilineHeight;
	private int fontId = FontManager.getDefaultFontID();
	private Color fontColor = Registry.GUI_DEFAULT_FONT_COLOR, hoverColor = Registry.GUI_DEFAULT_FONT_COLOR, pressedColor = Registry.GUI_DEFAULT_FONT_COLOR;
	private String text = "";
	private Anchors anch = Anchors.TOP_LEFT;
	private IGuiListener listener;
	private boolean pressedLeft = false, pressedRight = false;
	private boolean hover = false;
	private boolean multiline = false;
	private boolean ellipsize = false;
	
	public GuiTextBox(int x, int y, int width, int height, int rasterWidth, int rasterHeight) {
		left = x;
		top = y;
		right = rasterWidth - (x + width);
		bottom = rasterHeight - (y + height);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		nonMultilineHeight = FontManager.getFontHeight(fontId);
	}
	
	public void setFont(int id) {
		this.fontId = id;
		nonMultilineHeight = FontManager.getFontHeight(fontId);
	}
	
	public void setText(String text) {
		this.text = text;
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
	
	public void setMultiline(boolean multiline) {
		this.multiline = multiline;
	}
	
	public void setEllipsize(boolean ellipsize) {
		this.ellipsize = ellipsize;
	}
	
	@Override
	public void setListener(IGuiListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void render(Display window) {
		int height = multiline ? this.height : nonMultilineHeight + 1;
		
		ShaderManager.bindShader(ShaderManager.getColorShaderID());
		ShaderManager.loadMatrix4(ShaderManager.getColorShaderID(), "transformation", new Matrix4f());
		ShaderManager.loadMatrix4(ShaderManager.getColorShaderID(), "projection", window.get2DOrthographicProjectionMatrix());
		if (pressedLeft || pressedRight) {
			ShaderManager.loadVector4(ShaderManager.getColorShaderID(), "color", Gui.primaryAccentColor.getColorAsVector());
		}else if (hover) {
			ShaderManager.loadVector4(ShaderManager.getColorShaderID(), "color", Gui.primaryAccentColor.getColorAsVector());
		}else{
			ShaderManager.loadVector4(ShaderManager.getColorShaderID(), "color", Gui.tertiaryColor.getColorAsVector());
		}
		
		Tessellator.start(Tessellator.QUADS);
		
		Tessellator.addVertex(x, y);
		Tessellator.addVertex(x + width, y);
		Tessellator.addVertex(x + width, y + height);
		Tessellator.addVertex(x, y + height);
		
		Tessellator.finish();
		
		if (pressedLeft || pressedRight) {
			ShaderManager.loadVector4(ShaderManager.getColorShaderID(), "color", Gui.secondaryAccentColor.getColorAsVector());
		}else{
			ShaderManager.loadVector4(ShaderManager.getColorShaderID(), "color", Gui.quaternaryColor.getColorAsVector());
		}
		
		Tessellator.start(Tessellator.LINES);
		Tessellator.addVertex(x, y);
		Tessellator.addVertex(x + width, y);

		Tessellator.addVertex(x, y + height);
		Tessellator.addVertex(x + width, y + height);
		
		Tessellator.addVertex(x, y);
		Tessellator.addVertex(x, y + height);
		
		Tessellator.addVertex(x + width, y);
		Tessellator.addVertex(x + width, y + height);
		Tessellator.finish();
		
		ShaderManager.unbindShader();
		if (pressedLeft || pressedRight) {
			FontManager.drawStringInBounds(fontId, x, y + nonMultilineHeight, text, new Rectangle(width, height), pressedColor, ellipsize, new Matrix4f(), window.get2DOrthographicProjectionMatrix());
		}else if (hover) {
			FontManager.drawStringInBounds(fontId, x, y + nonMultilineHeight, text, new Rectangle(width, height), hoverColor, ellipsize, new Matrix4f(), window.get2DOrthographicProjectionMatrix());
		}else{
			FontManager.drawStringInBounds(fontId, x, y + nonMultilineHeight, text, new Rectangle(width, height), fontColor, ellipsize, new Matrix4f(), window.get2DOrthographicProjectionMatrix());
		}
	}

	@Override
	public void update(int mouseX, int mouseY, boolean leftButton, boolean rightButton) {
		if (mouseX > x + width || mouseX < x || mouseY < y || mouseY > y + (multiline ? height : nonMultilineHeight)) {
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
			width = window.getWidth() - right - left;
		case BOTTOM:
		case BOTTOM_LEFT:
			y = window.getHeight() - bottom - height;
			break;
		case BOTTOM_RIGHT:
			y = window.getHeight() - bottom - height;
			x = window.getWidth() - right - width;
			break;
		case LEFT_RIGHT:
			width = window.getWidth() - right - left;
			break;
		case LEFT_RIGHT_TOP_BOTTOM:
			width = window.getWidth() - right - left;
			height = window.getHeight() - bottom - top;
			break;
		case RIGHT_TOP_BOTTOM:
			x = window.getHeight() - right - width;
		case LEFT_TOP_BOTTOM:
		case TOP_BOTTOM:
			height = window.getHeight() - bottom - top;
			break;
		case RIGHT:
		case TOP_RIGHT:
			x = window.getHeight() - right - width;
			break;
		case TOP_LEFT_RIGHT:
			width = window.getWidth() - right - left;
			break;
		default:
			break;
		}
	}
	
	@Override
	public void anchor(Anchors anchors) {
		this.anch = anchors;
	}

	@Override
	public void charEvent(InputCharacter c) {
		if (c.getCode() != Registry.INVALID) {
			if (c.getCode() == InputCharacter.BACKSPACE && text.length() > 0) {
				text = text.substring(0, text.length() - 1);
			}
			if (c.getCode() == InputCharacter.ENTER && multiline) {
				text += '\n';
			}
			if (c.getCode() == InputCharacter.TABULATOR) {
				text += "\t";
			}
		}else{
			text += c.getChar();
		}
	}
}