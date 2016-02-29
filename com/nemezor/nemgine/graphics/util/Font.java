package com.nemezor.nemgine.graphics.util;

import java.util.HashMap;

import com.nemezor.nemgine.misc.Registry;

public class Font {

	public HashMap<Character, GLCharacter> chars;
	public int textureId;
	public java.awt.Font font;
	public int state;
	
	public Font(int texId) {
		this.textureId = texId;
		state = Registry.INVALID;
	}
	
	public void initializeFont(HashMap<Character, GLCharacter> chars, java.awt.Font font) {
		this.chars = chars;
		this.font = font;
		this.state = 0;
	}
}
