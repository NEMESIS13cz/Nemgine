package com.nemezor.nemgine.misc;

public class NemgineTextureException extends Exception {

	private static final long serialVersionUID = 1L;

	private String thrower;
	private String name;
	private int width = Registry.INVALID;
	private int height = Registry.INVALID;
	
	public NemgineTextureException(String message) {
		super(message);
	}
	
	public NemgineTextureException(String message, String thrower) {
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
	
	public String getShaderName() {
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
		System.err.println("[" + (thrower == null ? Registry.TEXTURE_EXCEPTION_NO_ACCESSOR : thrower) + "]: " + getLocalizedMessage());
		if (name != null) {
			if (width == Registry.INVALID || height == Registry.INVALID) {
				System.err.println(" >>> " + name);
			}else{
				System.err.println(" >>> " + name + " (" + width + "x" + height + ")");
			}
		}
	}
}
