package com.nemezor.nemgine.misc;

public class NemgineModelException extends Exception {

	private static final long serialVersionUID = 1L;

	private String thrower;
	private String name;
	
	public NemgineModelException(String message) {
		super(message);
	}
	
	public NemgineModelException(String message, String thrower) {
		super(message);
		this.thrower = thrower;
	}
	
	public void setModelInfo(String name) {
		this.name = name;
	}
	
	public void setThrower(String thrower) {
		this.thrower = thrower;
	}
	
	public String getModelName() {
		return name;
	}
	
	public String getThrower() {
		return thrower;
	}
	
	public void printStackTrace() {
		Logger.log(thrower == null ? Registry.TEXTURE_EXCEPTION_NO_ACCESSOR : thrower, getLocalizedMessage());
		if (name != null) {
			Logger.log(null, name);
		}
	}
}
