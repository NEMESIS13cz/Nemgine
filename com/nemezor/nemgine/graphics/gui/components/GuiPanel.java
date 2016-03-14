package com.nemezor.nemgine.graphics.gui.components;

import java.awt.Dimension;

import com.nemezor.nemgine.graphics.DisplayManager;
import com.nemezor.nemgine.graphics.FrameBufferManager;
import com.nemezor.nemgine.graphics.ModelManager;
import com.nemezor.nemgine.graphics.ShaderManager;
import com.nemezor.nemgine.graphics.Tessellator;
import com.nemezor.nemgine.graphics.gui.Gui;
import com.nemezor.nemgine.graphics.gui.IGuiComponent;
import com.nemezor.nemgine.graphics.gui.IGuiRenderCallback;
import com.nemezor.nemgine.graphics.util.Display;
import com.nemezor.nemgine.graphics.util.FrameBuffer;
import com.nemezor.nemgine.misc.Anchors;
import com.nemezor.nemgine.misc.Color;
import com.nemezor.nemgine.misc.IGuiListener;
import com.nemezor.nemgine.misc.MouseButton;
import com.nemezor.nemgine.misc.Registry;

public class GuiPanel implements IGuiComponent {

	private int left, right, top, bottom;
	private int x, y, width, height;
	private Anchors anch = Anchors.TOP_LEFT;
	private IGuiListener listener;
	private IGuiRenderCallback callback;
	private boolean pressedLeft = false, pressedRight = false;
	private boolean hover = false;
	private boolean enabled = true;
	private int framebuffer;
	private long resizeTime = Registry.INVALID;
	
	public GuiPanel(int x, int y, int width, int height, int rasterWidth, int rasterHeight) {
		left = x;
		top = y;
		right = rasterWidth - (x + width);
		bottom = rasterHeight - (y + height);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		framebuffer = FrameBufferManager.generateFrameBuffers();
		FrameBufferManager.initializeFrameBuffer(DisplayManager.getDisplay(DisplayManager.getCurrentDisplayID()), framebuffer, Math.abs(this.width - 1), Math.abs(this.height - 1), true, false, true);
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public void setRenderCallback(IGuiRenderCallback callback) {
		this.callback = callback;
	}
	
	@Override
	public void setListener(IGuiListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void render(Display window) {
		if (resizeTime != Registry.INVALID && resizeTime + Registry.GUI_PANEL_FBO_RESIZE_TRESHOLD < System.currentTimeMillis()) {
			resizeTime = Registry.INVALID;
			
			FrameBufferManager.dispose(framebuffer);
			framebuffer = FrameBufferManager.generateFrameBuffers();
			FrameBufferManager.initializeFrameBuffer(window, framebuffer, Math.abs(this.width - 1), Math.abs(this.height - 1), true, false, true);
		}
		if (callback != null) {
			FrameBufferManager.bindFrameBuffer(framebuffer);
			Dimension dim = FrameBufferManager.getFrameBufferResolution(framebuffer);
			
			callback.render(dim.width, dim.height);
			
			ModelManager.finishRendering();
			FrameBufferManager.unbindFrameBuffer(window);
		}
		
		ShaderManager.bindShader(ShaderManager.getTextureShaderID());
		ShaderManager.loadMatrix4(ShaderManager.getTextureShaderID(), "transformation", window.getTransformationMatrix());
		ShaderManager.loadMatrix4(ShaderManager.getTextureShaderID(), "projection", window.get2DOrthographicProjectionMatrix());
		ShaderManager.loadVector4(ShaderManager.getTextureShaderID(), "color", new Color(0xFFFFFFFF).getColorAsVector());
		
		FrameBufferManager.bindFrameBufferTexture(framebuffer, FrameBuffer.TEXTURE_BUFFER);
		Tessellator.start(Tessellator.QUADS);
		
		Tessellator.addVertex(x, y);
		Tessellator.addTexCoord(0, 1);
		Tessellator.addVertex(x + width - 1, y);
		Tessellator.addTexCoord(1, 1);
		Tessellator.addVertex(x + width - 1, y + height - 1);
		Tessellator.addTexCoord(1, 0);
		Tessellator.addVertex(x, y + height - 1);
		Tessellator.addTexCoord(0, 0);
		
		Tessellator.finish();
		FrameBufferManager.unbindFrameBufferTexture();
		
		ShaderManager.bindShader(ShaderManager.getColorShaderID());
		ShaderManager.loadMatrix4(ShaderManager.getColorShaderID(), "transformation", window.getTransformationMatrix());
		ShaderManager.loadMatrix4(ShaderManager.getColorShaderID(), "projection", window.get2DOrthographicProjectionMatrix());
		
		if (!enabled) {
			ShaderManager.loadVector4(ShaderManager.getColorShaderID(), "color", Gui.quaternaryColor.getColorAsVector());
		}else if (pressedLeft || pressedRight) {
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
	}

	@Override
	public void update(Display window, int mouseX, int mouseY, boolean leftButton, boolean rightButton) {
		if (mouseX > x + width || mouseX < x || mouseY < y || mouseY > y + height) {
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
				if (!pressedLeft && listener != null && enabled) {
					listener.onPressed(MouseButton.LEFT);
				}
				pressedLeft = true;
			}else{
				if (pressedLeft && listener != null && enabled) {
					listener.onReleased(MouseButton.LEFT);
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
		}
	}

	@Override
	public void resize(Display window) {
		boolean resized = false;
		switch (anch) {
		case BOTTOM_LEFT_RIGHT:
			width = window.getWidth() - right - left;
		case BOTTOM:
		case BOTTOM_LEFT:
			y = window.getHeight() - bottom - height;
			resized = true;
			break;
		case BOTTOM_RIGHT:
			y = window.getHeight() - bottom - height;
			x = window.getWidth() - right - width;
			resized = true;
			break;
		case LEFT_RIGHT:
			width = window.getWidth() - right - left;
			resized = true;
			break;
		case LEFT_RIGHT_TOP_BOTTOM:
			width = window.getWidth() - right - left;
			height = window.getHeight() - bottom - top;
			resized = true;
			break;
		case RIGHT_TOP_BOTTOM:
			x = window.getWidth() - right - width;
		case LEFT_TOP_BOTTOM:
		case TOP_BOTTOM:
			height = window.getHeight() - bottom - top;
			resized = true;
			break;
		case RIGHT:
		case TOP_RIGHT:
			x = window.getHeight() - right - width;
			resized = true;
			break;
		case TOP_LEFT_RIGHT:
			width = window.getWidth() - right - left;
			resized = true;
			break;
		default:
			break;
		}
		if (resized) {
			resizeTime = System.currentTimeMillis();
		}
	}
	
	@Override
	public void anchor(Anchors anchors) {
		this.anch = anchors;
	}
}
