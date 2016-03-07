package com.nemezor.nemgine.input;

public interface IKeyInput {

	void keyEvent(int key, int scancode, int action, int mods);
	void charEvent(char character);
	void charModsEvent(char character, int mods);
	
}
