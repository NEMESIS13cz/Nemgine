package com.nemezor.nemgine.misc;

public class NemgineShaderException extends Exception {

	private static final long serialVersionUID = 1L;

	private String thrower;
	private String name;
	private EnumShaderType type;
	
	public NemgineShaderException(String message) {
		super(message);
	}

	public NemgineShaderException(String message, String thrower) {
		super(message);
		this.thrower = thrower;
	}
	
	public void setShaderInfo(String name, EnumShaderType type) {
		this.name = name;
		this.type = type;
	}
	
	public void setThrower(String thrower) {
		this.thrower = thrower;
	}
	
	public String getShaderName() {
		return name;
	}
	
	public EnumShaderType getShaderType() {
		return type;
	}
	
	public String getThrower() {
		return thrower;
	}
	
	public void printStackTrace() {
		System.err.println("[" + (thrower == null ? Registry.SHADER_EXCEPTION_NO_ACCESSOR : thrower) + "]: " + getLocalizedMessage());
		if (type != null) {
			System.err.println(" >>> " + name + " (" + type.toString() + ")");
		}
	}
}
