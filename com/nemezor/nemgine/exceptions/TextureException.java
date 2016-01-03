package com.nemezor.nemgine.exceptions;

import com.nemezor.nemgine.misc.Logger;
import com.nemezor.nemgine.misc.Registry;

public class TextureException extends Exception {

	private static final long serialVersionUID = 1L;

	private String thrower;
	private String name;
	private int width = Registry.INVALID;
	private int height = Registry.INVALID;
	
	public TextureException(String message) {
		super(message);
	}
	
	public TextureException(String message, String thrower) {
		super(message);
		this.thrower = thrower;
	}
	
	public void setTextureInfo(String name) {
		this.name = name;
	}
	
	public void setTextureSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public void setThrower(String thrower) {
		this.thrower = thrower;
	}
	
	public String getTextureName() {
		return name;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public String getThrower() {
		return thrower;
	}
	
	public void printStackTrace() {
		Logger.log(thrower == null ? Registry.TEXTURE_EXCEPTION_NO_ACCESSOR : thrower, getLocalizedMessage(), false);
		if (name != null) {
			if (width == Registry.INVALID || height == Registry.INVALID) {
				Logger.log(null, name, false);
			}else{
				Logger.log(null, name + " (" + width + "x" + height + ")", false);
			}
		}
	}
}
