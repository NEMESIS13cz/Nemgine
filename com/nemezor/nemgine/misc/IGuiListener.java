package com.nemezor.nemgine.misc;

public interface IGuiListener {

	void onPressed(MouseButton button);
	void onReleased(MouseButton button);
	void onEnter();
	void onExit();
}
