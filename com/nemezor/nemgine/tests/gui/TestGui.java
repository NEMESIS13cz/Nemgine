package com.nemezor.nemgine.tests.gui;

import java.util.Random;

import com.nemezor.nemgine.graphics.gui.Gui;
import com.nemezor.nemgine.graphics.gui.components.GuiButton;
import com.nemezor.nemgine.graphics.gui.components.GuiGridLayout;
import com.nemezor.nemgine.graphics.gui.components.GuiResourceMonitor;
import com.nemezor.nemgine.graphics.gui.components.GuiTextBox;
import com.nemezor.nemgine.misc.Anchors;
import com.nemezor.nemgine.misc.IGuiListener;
import com.nemezor.nemgine.misc.MouseButton;
import com.nemezor.nemgine.misc.Registry;

public class TestGui extends Gui {

	private Random rand = new Random();
	
	@Override
	public void populate(int rasterWidth, int rasterHeight) {
		GuiTextBox input = new GuiTextBox(10, 10, rasterWidth - 20, 50, rasterWidth, rasterHeight);
		GuiTextBox output = new GuiTextBox(10, 40, rasterWidth - 20, 50, rasterWidth, rasterHeight);
		GuiButton button = new GuiButton(10, 70, 150, 40, rasterWidth, rasterHeight);
		GuiResourceMonitor res = new GuiResourceMonitor(rasterWidth - 410, rasterHeight - 110, 400, 100, rasterWidth, rasterHeight);
		GuiGridLayout grid = new GuiGridLayout(10, 150, 300, 200, rasterWidth, rasterHeight, 1, 2);
		GuiGridLayout grid2 = new GuiGridLayout(0, 0, 150, 200, 150, 200, 2, 1);
		GuiButton gridButton = new GuiButton(10, 10, 130, 80, 150, 100);
		
		input.anchor(Anchors.TOP_LEFT_RIGHT);
		output.anchor(Anchors.TOP_LEFT_RIGHT);
		button.anchor(Anchors.TOP_LEFT);
		res.anchor(Anchors.BOTTOM_RIGHT);
		grid.anchor(Anchors.LEFT_RIGHT_TOP_BOTTOM);
		grid2.anchor(Anchors.LEFT_RIGHT_TOP_BOTTOM);
		gridButton.anchor(Anchors.LEFT_RIGHT_TOP_BOTTOM);
		
		output.setEditable(false);
		
		grid.setVisible(true);
		grid.setRowProperties(0, 100, true);
		grid.setColumnProperties(0, 50, true);
		grid.setColumnProperties(1, 50, true);
		
		grid2.setVisible(true);
		grid2.setRowProperties(0, 50, true);
		grid2.setRowProperties(1, 50, true);
		grid2.setColumnProperties(0, 100, true);
		
		gridButton.setText("test");
		
		res.setEnabled(false);
		
		button.setText("Generate");
		button.setListener(new IGuiListener() {
			public void onPressed(MouseButton button) {
				
			}

			public void onReleased(MouseButton button) {
				int number = Registry.INVALID;
				output.clear();
				try{
					number = Integer.parseInt(input.getText());
				} catch (NumberFormatException e) {
					output.setText("Invalid input!");
					return;
				}
				for (int i = 0; i < number; i++) {
					output.appendText(rand.nextInt(20) + " ");
				}
			}

			public void onEnter() {
				
			}

			public void onExit() {
				
			}
		});
		
		grid2.add("button", gridButton, 1, 0);
		
		grid.add("nestedGrid", grid2, 0, 1);
		
		add("inputTextbox", input);
		add("outputTextbox", output);
		add("generateButton", button);
		add("resourceMonitor", res);
		add("grid", grid);
	}
}
