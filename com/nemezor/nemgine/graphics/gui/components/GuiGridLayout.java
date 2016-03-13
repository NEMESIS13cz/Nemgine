package com.nemezor.nemgine.graphics.gui.components;

import java.util.ArrayList;

import com.nemezor.nemgine.graphics.ShaderManager;
import com.nemezor.nemgine.graphics.Tessellator;
import com.nemezor.nemgine.graphics.gui.GridCell;
import com.nemezor.nemgine.graphics.gui.Gui;
import com.nemezor.nemgine.graphics.gui.IGuiComponent;
import com.nemezor.nemgine.graphics.gui.IGuiKeyListener;
import com.nemezor.nemgine.graphics.util.Display;
import com.nemezor.nemgine.input.InputCharacter;
import com.nemezor.nemgine.misc.Anchors;
import com.nemezor.nemgine.misc.IGuiListener;

public class GuiGridLayout implements IGuiComponent, IGuiKeyListener {

	private ArrayList<IGuiKeyListener> keyListeners = new ArrayList<IGuiKeyListener>();
	private GridCell[][] cells;
	private int left, right, top, bottom;
	private int x, y, width, height;
	private Anchors anch = Anchors.TOP_LEFT;
	private boolean invisible = true;
	
	public GuiGridLayout(int x, int y, int width, int height, int rasterWidth, int rasterHeight, int rows, int columns) {
		left = x;
		top = y;
		right = rasterWidth - (x + width);
		bottom = rasterHeight - (y + height);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.cells = new GridCell[rows][columns];
		for (int row = 0; row < cells.length; row++) {
			for (int col = 0; col < cells[row].length; col++) {
				cells[row][col] = new GridCell(row, col, (float)width / (float)columns, (float)height / (float)rows);
			}
		}
	}
	
	public void setVisible(boolean visible) {
		invisible = !visible;
	}
	
	public boolean remove(String name, IGuiComponent component, int row, int column) {
		GridCell cell = cells[row][column];
		if (cell != null && cell.components.containsKey(name)) {
			IGuiComponent c = cell.components.remove(name);
			if (c instanceof IGuiKeyListener) {
				keyListeners.remove((IGuiKeyListener)c);
			}
			return true;
		}
		return false;
	}

	public boolean add(String name, IGuiComponent component, int row, int column) {
		GridCell cell = cells[row][column];
		if (cell == null || cell.components.containsKey(name)) {
			return false;
		}
		cell.components.put(name, component);
		if (component instanceof IGuiKeyListener) {
			keyListeners.add((IGuiKeyListener)component);
		}
		return true;
	}

	public void setRowProperties(int row, float height, boolean percentageBased) {
		if (row >= cells.length) {
			return;
		}
		for (GridCell cell : cells[row]) {
			cell.height = height;
			cell.percRow = percentageBased;
		}
	}

	public void setColumnProperties(int column, float width, boolean percentageBased) {
		if (column >= cells[0].length) {
			return;
		}
		for (GridCell[] row : cells) {
			GridCell cell = row[column];
			cell.width = width;
			cell.percCol = percentageBased;
		}
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
		for (int row = 0; row < cells.length; row++) {
			for (int col = 0; col < cells[row].length; col++) {
				cells[row][col].display.recalcTransform(window);
				for (IGuiComponent c : cells[row][col].components.values()) {
					c.render(cells[row][col].display);
				}
			}
		}
	}

	@Override
	public void update(Display window, int mouseX, int mouseY, boolean leftButton, boolean rightButton) {
		for (int row = 0; row < cells.length; row++) {
			for (int col = 0; col < cells[row].length; col++) {
				GridCell cell = cells[row][col];
				int mX = mouseX - (int)cell.display.getX();
				int mY = mouseY - (int)cell.display.getY();
				for (IGuiComponent c : cell.components.values()) {
					c.update(cell.display, mX, mY, leftButton, rightButton);
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
		float setWidth = 0;
		float setHeight = 0;
		for (int row = 0; row < cells.length; row++) {
			if (!cells[row][0].percRow) {
				setHeight += cells[row][0].height;
			}
		}
		for (int col = 0; col < cells[0].length; col++) {
			if (!cells[0][col].percCol) {
				setWidth += cells[0][col].width;
			}
		}
		float onePercentW = (width - setWidth) / 100;
		float onePercentH = (height - setHeight) / 100;
		float cellX = x;
		float cellY = y;
		float lastHeight = 0;
		for (int row = 0; row < cells.length; row++) {
			for (int col = 0; col < cells[row].length; col++) {
				GridCell cell = cells[row][col];
				float cellW = cell.percCol ? cell.width * onePercentW : cell.width;
				float cellH = cell.percRow ? cell.height * onePercentH : cell.height;
				cell.display.setTranslation(cellX, cellY);
				cell.display.setSize(cellW, cellH);
				cellX += cellW;
				lastHeight = cellH;
			}
			cellX = x;
			cellY += lastHeight;
		}
		for (int row = 0; row < cells.length; row++) {
			for (int col = 0; col < cells[row].length; col++) {
				for (IGuiComponent c : cells[row][col].components.values()) {
					c.resize(cells[row][col].display);
				}
			}
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
