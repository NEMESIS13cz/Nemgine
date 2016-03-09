package com.nemezor.nemgine.tests.gui;

import java.util.Random;

import com.nemezor.nemgine.graphics.gui.Gui;
import com.nemezor.nemgine.graphics.gui.components.GuiButton;
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
		
		input.anchor(Anchors.TOP_LEFT_RIGHT);
		output.anchor(Anchors.TOP_LEFT_RIGHT);
		button.anchor(Anchors.TOP_LEFT);

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
		
		add("inputTextbox", input);
		add("outputTextbox", output);
		add("generateButton", button);
	}
}
