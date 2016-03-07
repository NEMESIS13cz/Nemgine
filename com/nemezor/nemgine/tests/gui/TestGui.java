package com.nemezor.nemgine.tests.gui;

import com.nemezor.nemgine.console.Console;
import com.nemezor.nemgine.graphics.gui.Gui;
import com.nemezor.nemgine.graphics.gui.components.GuiTextBox;
import com.nemezor.nemgine.misc.Anchors;
import com.nemezor.nemgine.misc.IGuiListener;
import com.nemezor.nemgine.misc.MouseButton;

public class TestGui extends Gui {

	@Override
	public void populate(int rasterWidth, int rasterHeight) {
		GuiTextBox button = new GuiTextBox(300, 80, 200, 50, rasterWidth, rasterHeight);
		
		button.anchor(Anchors.LEFT_RIGHT_TOP_BOTTOM);
		
		button.setListener(new IGuiListener() {
			public void onPressed(MouseButton button) {
				Console.out.println(button + " PRESSED");
			}

			public void onReleased(MouseButton button) {
				Console.out.println(button + " RELEASED");
			}

			public void onEnter() {
				Console.out.println("ENTERED");
			}

			public void onExit() {
				Console.out.println("EXITED");
			}
		});
		
		add("button", button);
	}
}
