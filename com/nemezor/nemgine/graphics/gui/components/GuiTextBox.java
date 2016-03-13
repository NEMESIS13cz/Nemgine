package com.nemezor.nemgine.graphics.gui.components;

import java.awt.Point;
import java.awt.Rectangle;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import com.nemezor.nemgine.graphics.FontManager;
import com.nemezor.nemgine.graphics.ShaderManager;
import com.nemezor.nemgine.graphics.Tessellator;
import com.nemezor.nemgine.graphics.gui.Gui;
import com.nemezor.nemgine.graphics.gui.IGuiComponent;
import com.nemezor.nemgine.graphics.gui.IGuiKeyListener;
import com.nemezor.nemgine.graphics.util.Display;
import com.nemezor.nemgine.input.InputCharacter;
import com.nemezor.nemgine.input.Mouse;
import com.nemezor.nemgine.misc.Anchors;
import com.nemezor.nemgine.misc.Color;
import com.nemezor.nemgine.misc.IGuiListener;
import com.nemezor.nemgine.misc.MouseButton;
import com.nemezor.nemgine.misc.Registry;

public class GuiTextBox implements IGuiComponent, IGuiKeyListener {

	private int left, right, top, bottom;
	private int x, y, width, height;
	private int nonMultilineHeight;
	private int fontId = FontManager.getDefaultFontID20();
	private Color fontColor = Registry.GUI_DEFAULT_FONT_COLOR, hoverColor = Registry.GUI_DEFAULT_FONT_COLOR, pressedColor = Registry.GUI_DEFAULT_FONT_COLOR;
	private Color caretColor = Registry.GUI_DEFAULT_FONT_COLOR;
	private String text = "";
	private Anchors anch = Anchors.TOP_LEFT;
	private IGuiListener listener;
	private boolean pressedLeft = false, pressedRight = false;
	private boolean hover = false;
	private boolean multiline = false;
	private boolean ellipsize = false;
	private boolean editable = true;
	private boolean hasFocus = false;
	private int hoverCursor = Mouse.CURSOR_TEXT;
	private int defaultCursor = Mouse.CURSOR_NORMAL;
	private int caretPos = 0;
	private int caretPosXPx = 0;
	private int caretPosYPx = 0;
	private long lastSwitch = 0;
	private boolean caretVisible = true;
	private int lineOffset = 0;
	private int hoverPos = 0;
	private boolean dragging = false;
	private boolean enabled = true;
	
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
		caretPos = text.length();
		hoverPos = caretPos;
	}
	
	public void clear() {
		this.text = "";
		caretPos = text.length();
		hoverPos = caretPos;
	}
	
	public String getText() {
		return text;
	}
	
	public void appendText(String text) {
		this.text += text;
		caretPos = text.length();
		hoverPos = caretPos;
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
	
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	public void setFocus(boolean focus) {
		this.hasFocus = focus;
	}
	
	public void setDefaultCursor(int cursor) {
		this.defaultCursor = cursor;
	}
	
	public void setHoverCursor(int cursor) {
		this.hoverCursor = cursor;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	@Override
	public void setListener(IGuiListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void render(Display window) {
		int height = multiline ? this.height : nonMultilineHeight;
		
		ShaderManager.bindShader(ShaderManager.getColorShaderID());
		ShaderManager.loadMatrix4(ShaderManager.getColorShaderID(), "transformation", new Matrix4f());
		ShaderManager.loadMatrix4(ShaderManager.getColorShaderID(), "projection", window.get2DOrthographicProjectionMatrix());
		if (!enabled) {
			ShaderManager.loadVector4(ShaderManager.getColorShaderID(), "color", Gui.tertiaryColor.getColorAsVector());
		}else if (pressedLeft || pressedRight || hover) {
			ShaderManager.loadVector4(ShaderManager.getColorShaderID(), "color", Gui.primaryAccentColor.getColorAsVector());
		}else if (hasFocus) {
			ShaderManager.loadVector4(ShaderManager.getColorShaderID(), "color", Gui.secondaryColor.getColorAsVector());
		}else{
			ShaderManager.loadVector4(ShaderManager.getColorShaderID(), "color", Gui.tertiaryColor.getColorAsVector());
		}
		
		Tessellator.start(Tessellator.QUADS);
		
		Tessellator.addVertex(x, y);
		Tessellator.addVertex(x + width, y);
		Tessellator.addVertex(x + width, y + height);
		Tessellator.addVertex(x, y + height);
		
		Tessellator.finish();
		
		if (!enabled) {
			ShaderManager.loadVector4(ShaderManager.getColorShaderID(), "color", Gui.quaternaryColor.getColorAsVector());
		}else if (pressedLeft || pressedRight || hasFocus) {
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
		
		if (hasFocus) {
			if (caretVisible && y + caretPosYPx + Registry.GUI_CARET_PADDING < y + height) {
				ShaderManager.loadVector4(ShaderManager.getColorShaderID(), "color", caretColor.getColorAsVector());
				GL11.glLineWidth(Registry.GUI_CARET_WIDTH);
				Tessellator.start(Tessellator.LINES);
				
				Tessellator.addVertex(x + caretPosXPx, y + caretPosYPx + Registry.GUI_CARET_PADDING);
				
				int caretHighY = y + caretPosYPx + nonMultilineHeight - Registry.GUI_CARET_PADDING;
				if (caretHighY > y + height) {
					Tessellator.addVertex(x + caretPosXPx, y + height - Registry.GUI_CARET_PADDING);
				}else{
					Tessellator.addVertex(x + caretPosXPx, caretHighY);
				}
				
				Tessellator.finish();
				GL11.glLineWidth(1);
			}
			if (lastSwitch + Registry.ONE_SECOND_IN_MILLIS * Registry.GUI_CARET_BLINK_SPEED < System.currentTimeMillis()) {
				caretVisible = !caretVisible;
				lastSwitch = System.currentTimeMillis();
			}
		}
		
		ShaderManager.unbindShader();
		if (multiline) {
			if (!enabled) {
				FontManager.drawStringInBounds(fontId, x, y + nonMultilineHeight, text, new Rectangle(x, y, width, height), Gui.quaternaryColor, ellipsize, new Matrix4f(), window.get2DOrthographicProjectionMatrix(), caretPos > hoverPos ? hoverPos : caretPos, caretPos < hoverPos ? hoverPos : caretPos);
			}else if (pressedLeft || pressedRight) {
				FontManager.drawStringInBounds(fontId, x, y + nonMultilineHeight, text, new Rectangle(x, y, width, height), pressedColor, ellipsize, new Matrix4f(), window.get2DOrthographicProjectionMatrix(), caretPos > hoverPos ? hoverPos : caretPos, caretPos < hoverPos ? hoverPos : caretPos);
			}else if (hover) {
				FontManager.drawStringInBounds(fontId, x, y + nonMultilineHeight, text, new Rectangle(x, y, width, height), hoverColor, ellipsize, new Matrix4f(), window.get2DOrthographicProjectionMatrix(), caretPos > hoverPos ? hoverPos : caretPos, caretPos < hoverPos ? hoverPos : caretPos);
			}else{
				FontManager.drawStringInBounds(fontId, x, y + nonMultilineHeight, text, new Rectangle(x, y, width, height), fontColor, ellipsize, new Matrix4f(), window.get2DOrthographicProjectionMatrix(), caretPos > hoverPos ? hoverPos : caretPos, caretPos < hoverPos ? hoverPos : caretPos);
			}
		}else{
			int offset = hasFocus ? lineOffset : 0;
			if (!enabled) {
				FontManager.drawStringInBoundsSingleLine(fontId, x - offset, y + nonMultilineHeight, text, new Rectangle(x, y, width, height), Gui.quaternaryColor, ellipsize, new Matrix4f(), window.get2DOrthographicProjectionMatrix(), caretPos > hoverPos ? hoverPos : caretPos, caretPos < hoverPos ? hoverPos : caretPos);
			}else if (pressedLeft || pressedRight) {
				FontManager.drawStringInBoundsSingleLine(fontId, x - offset, y + nonMultilineHeight, text, new Rectangle(x, y, width, height), pressedColor, ellipsize, new Matrix4f(), window.get2DOrthographicProjectionMatrix(), caretPos > hoverPos ? hoverPos : caretPos, caretPos < hoverPos ? hoverPos : caretPos);
			}else if (hover) {
				FontManager.drawStringInBoundsSingleLine(fontId, x - offset, y + nonMultilineHeight, text, new Rectangle(x, y, width, height), hoverColor, ellipsize, new Matrix4f(), window.get2DOrthographicProjectionMatrix(), caretPos > hoverPos ? hoverPos : caretPos, caretPos < hoverPos ? hoverPos : caretPos);
			}else{
				FontManager.drawStringInBoundsSingleLine(fontId, x - offset, y + nonMultilineHeight, text, new Rectangle(x, y, width, height), fontColor, ellipsize, new Matrix4f(), window.get2DOrthographicProjectionMatrix(), caretPos > hoverPos ? hoverPos : caretPos, caretPos < hoverPos ? hoverPos : caretPos);
			}
		}
	}

	@Override
	public void update(Display window, int mouseX, int mouseY, boolean leftButton, boolean rightButton) {
		if (mouseX > x + width || mouseX < x || mouseY < y || mouseY > y + (multiline ? height : nonMultilineHeight)) {
			if (hover) {
				if (listener != null) {
					listener.onExit();
				}
				if (enabled) {
					Mouse.setCursor(window, defaultCursor);
				}
			}
			pressedRight = false;
			pressedLeft = false;
			hover = false;
			if (leftButton) {
				hasFocus = false;
				dragging = false;
				hoverPos = caretPos;
			}
		}else{
			if (!hover) {
				if (listener != null) {
					listener.onEnter();
				}
				if (enabled) {
					Mouse.setCursor(window, hoverCursor);
				}
			}
			hover = true;
			if (leftButton) {
				if (!pressedLeft && enabled) {
					if (listener != null) {
						listener.onPressed(MouseButton.LEFT);
					}
					if (multiline) {
						if (mouseY > y + height) {
							return;
						}
					}else{
						if (mouseY > y + nonMultilineHeight) {
							return;
						}
					}
					int tempCaretPos = FontManager.getCharIndexFromPosition(fontId, text, mouseX - x + (multiline ? 0 : lineOffset), mouseY - y, multiline ? width : Integer.MAX_VALUE);
					if (tempCaretPos == Registry.INVALID) {
						return;
					}
					caretPos = tempCaretPos;
					updateCaretPositionAndOffset();
					dragging = true;
					hasFocus = true;
					hoverPos = caretPos;
				}
				pressedLeft = true;
			}else{
				if (pressedLeft) {
					if (listener != null && enabled) {
						listener.onReleased(MouseButton.LEFT);
					}
					dragging = false;
				}
				pressedLeft = false;
			}
			if (rightButton) {
				if (!pressedRight && listener != null && enabled) {
					listener.onPressed(MouseButton.RIGHT);
				}
				pressedRight = true;
			}else{
				if (pressedRight && listener != null && enabled) {
					listener.onReleased(MouseButton.RIGHT);
				}
				pressedRight = false;
			}
			if (dragging && enabled) {
				int tempCaretPos = FontManager.getCharIndexFromPosition(fontId, text, mouseX - x + (multiline ? 0 : lineOffset), mouseY - y, multiline ? width : Integer.MAX_VALUE);
				if (tempCaretPos != Registry.INVALID) {
					hoverPos = tempCaretPos;
				}
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
		updateCaretPositionAndOffset();
	}
	
	@Override
	public void anchor(Anchors anchors) {
		this.anch = anchors;
	}

	@Override
	public void charEvent(InputCharacter c) {
		if (!editable || !hasFocus || !enabled) {
			return;
		}
		try{
			int lower = hoverPos > caretPos ? caretPos : hoverPos;
			int higher = hoverPos < caretPos ? caretPos : hoverPos;
			if (c.getCode() != Registry.INVALID) {
				if (c.getCode() == InputCharacter.BACKSPACE && text.length() > 0 && caretPos > 0) {
					text = text.substring(0, lower - (lower == higher ? 1 : 0)) + text.substring(higher > text.length() ? text.length() : higher);
					caretPos--;
					hoverPos = caretPos;
					updateCaretPositionAndOffset();
				}
				if (c.getCode() == InputCharacter.DELETE && caretPos < text.length()) {
					text = text.substring(0, lower) + text.substring(higher + (lower == higher ? 1 : 0));
					hoverPos = caretPos;
					updateCaretPositionAndOffset();
				}
				if (c.getCode() == InputCharacter.ENTER && multiline) {
					text = text.substring(0, lower) + "\n" + text.substring(higher + (lower != higher ? 1 : 0));
					caretPos++;
					hoverPos = caretPos;
					updateCaretPositionAndOffset();
				}
				if (c.getCode() == InputCharacter.TABULATOR) {
					if (lower == higher) {
						text = text.substring(0, lower) + "\t" + text.substring(lower);
						caretPos++;
					}else{
						text = text.substring(0, lower) + "\t" + text.substring(higher + 1 > text.length() ? higher : higher + 1);
						caretPos = lower + 1;
					}
					hoverPos = caretPos;
					updateCaretPositionAndOffset();
				}
				if (c.getCode() == InputCharacter.ARROW_LEFT) {
					if (caretPos > 0) {
						caretPos--;
						hoverPos = caretPos;
						updateCaretPositionAndOffset();
					}
				}
				if (c.getCode() == InputCharacter.ARROW_RIGHT) {
					if (caretPos < text.length()) {
						caretPos++;
						hoverPos = caretPos;
						updateCaretPositionAndOffset();
					}
				}
			}else{
				if (lower == higher) {
					text = text.substring(0, lower) + c.getChar() + text.substring(lower);
					caretPos++;
				}else{
					text = text.substring(0, lower) + c.getChar() + text.substring(higher + 1 > text.length() ? higher : higher + 1);
					caretPos = lower + 1;
				}
				hoverPos = caretPos;
				updateCaretPositionAndOffset();
			}
		}catch (Exception e) {
			e.printStackTrace();
			System.err.println("Fix me!");
		}
	}
	
	private void updateCaretPositionAndOffset() {
		Point pos = FontManager.getPositionInText(fontId, text, caretPos, multiline ? width : Integer.MAX_VALUE);
		if (multiline) {
			caretPosXPx = pos.x;
		}else{
			caretPosXPx = pos.x - lineOffset;
		}
		caretPosYPx = pos.y;
		int length = FontManager.getStringWidth(fontId, text);
		if (length > width) {
			
			lineOffset = length - width;
		}else{
			lineOffset = 0;
		}
		lastSwitch = System.currentTimeMillis();
		caretVisible = true;
	}
}
