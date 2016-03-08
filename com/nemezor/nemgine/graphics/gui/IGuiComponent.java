package com.nemezor.nemgine.graphics.gui;

import com.nemezor.nemgine.graphics.util.Display;
import com.nemezor.nemgine.misc.Anchors;
import com.nemezor.nemgine.misc.IGuiListener;

public interface IGuiComponent {
	
	void render(Display window);
	void update(Display window, int mouseX, int mouseY, boolean leftButton, boolean rightButton);
	void resize(Display window);
	void anchor(Anchors anchor);
	void setListener(IGuiListener listener);
}
