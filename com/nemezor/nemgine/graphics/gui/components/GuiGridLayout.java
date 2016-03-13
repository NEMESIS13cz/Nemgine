package com.nemezor.nemgine.graphics.gui.components;

import java.util.ArrayList;
import java.util.HashMap;

import com.nemezor.nemgine.graphics.ShaderManager;
import com.nemezor.nemgine.graphics.Tessellator;
import com.nemezor.nemgine.graphics.gui.Gui;
import com.nemezor.nemgine.graphics.gui.IGuiComponent;
import com.nemezor.nemgine.graphics.gui.IGuiKeyListener;
import com.nemezor.nemgine.graphics.util.Display;
import com.nemezor.nemgine.graphics.util.FakeDisplay;
import com.nemezor.nemgine.input.InputCharacter;
import com.nemezor.nemgine.misc.Anchors;
import com.nemezor.nemgine.misc.IGuiListener;

public class GuiGridLayout implements IGuiComponent, IGuiKeyListener { // TODO add columns/rows to this so that its actually useful...

	private HashMap<String, IGuiComponent> components = new HashMap<String, IGuiComponent>();
	private ArrayList<IGuiKeyListener> keyListeners = new ArrayList<IGuiKeyListener>();
	private int left, right, top, bottom;
	private int x, y, width, height;
	private Anchors anch = Anchors.TOP_LEFT;
	private boolean invisible = true;
	private FakeDisplay display;
	
	public GuiGridLayout(int x, int y, int width, int height, int rasterWidth, int rasterHeight) {
		left = x;
		top = y;
		right = rasterWidth - (x + width);
		bottom = rasterHeight - (y + height);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		display = new FakeDisplay(width, height, 0, 0, 0);
		display.setTranslation(x, y);
	}
	
	public void setVisible(boolean visible) {
		invisible = !visible;
	}
	
	public boolean remove(String name, IGuiComponent component) {
		if (components.containsKey(name)) {
			IGuiComponent c = components.remove(name);
			if (c instanceof IGuiKeyListener) {
				keyListeners.remove((IGuiKeyListener)c);
			}
			return true;
		}
		return false;
	}

	public boolean add(String name, IGuiComponent component) {
		if (components.containsKey(name)) {
			return false;
		}
		components.put(name, component);
		if (component instanceof IGuiKeyListener) {
			keyListeners.add((IGuiKeyListener)component);
		}
		return true;
	}

	@Override
	public void setListener(IGuiListener listener) {
		
	}
	
	@Override
	public void render(Display window) {
		if (!invisible) {
			ShaderManager.bindShader(ShaderManager.getColorShaderID());
			ShaderManager.loadMatrix4(ShaderManager.getColorShaderID(), "transformation", window.getTransformationMatrix());
			ShaderManager.loadMatrix4(ShaderManager.getColorShaderID(), "projection", window.get2DOrthographicProjectionMatrix());
			ShaderManager.loadVector4(ShaderManager.getColorShaderID(), "color", Gui.secondaryColor.getColorAsVector());
			
			Tessellator.start(Tessellator.QUADS);
			
			Tessellator.addVertex(x, y);
			Tessellator.addVertex(x + width, y);
			Tessellator.addVertex(x + width, y + height);
			Tessellator.addVertex(x, y + height);
			
			Tessellator.finish();
			ShaderManager.loadVector4(ShaderManager.getColorShaderID(), "color", Gui.tertiaryColor.getColorAsVector());
			
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
		display.recalcTransform(window);
		
		for (IGuiComponent c : components.values()) {
			c.render(display);
		}
	}

	@Override
	public void update(Display window, int mouseX, int mouseY, boolean leftButton, boolean rightButton) {
		mouseX = mouseX - x;
		mouseY = mouseY - y;
		for (IGuiComponent c : components.values()) {
			c.update(display, mouseX, mouseY, leftButton, rightButton);
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
			x = window.getWidth() - right - width;
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
		display.setTranslation(x, y);
		display.setSize(width, height);
		for (IGuiComponent c : components.values()) {
			c.resize(display);
		}
	}
	
	@Override
	public void anchor(Anchors anchors) {
		this.anch = anchors;
	}

	@Override
	public void charEvent(InputCharacter character) {
		for (IGuiKeyListener l : keyListeners) {
			l.charEvent(character);
		}
	}
}
