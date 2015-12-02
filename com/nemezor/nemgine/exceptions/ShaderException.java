package com.nemezor.nemgine.exceptions;

import com.nemezor.nemgine.misc.EnumShaderType;
import com.nemezor.nemgine.misc.Logger;
import com.nemezor.nemgine.misc.Registry;

public class ShaderException extends Exception {

	private static final long serialVersionUID = 1L;

	private String thrower;
	private String name;
	private EnumShaderType type;
	
	public ShaderException(String message) {
		super(message);
	}

	public ShaderException(String message, String thrower) {
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
		Logger.log(thrower == null ? Registry.SHADER_EXCEPTION_NO_ACCESSOR : thrower, getLocalizedMessage());
		if (type != null) {
			Logger.log(null, name + " (" + type.toString() + ")");
		}
	}
}
