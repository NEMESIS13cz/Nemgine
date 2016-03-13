package com.nemezor.nemgine.graphics.gui;

import java.util.HashMap;

import com.nemezor.nemgine.graphics.util.FakeDisplay;

public class GridCell {

	public int row, column;
	public float width, height;
	public FakeDisplay display;
	public boolean percRow = true, percCol = true;
	public HashMap<String, IGuiComponent> components = new HashMap<String, IGuiComponent>();
	
	public GridCell(int row, int column, float width, float height) {
		this.row = row;
		this.column = column;
		this.width = width;
		this.height = height;
		this.display = new FakeDisplay(width, height, 0, 0, 0);
	}
	
}
