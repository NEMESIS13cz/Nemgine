package com.nemezor.nemgine.exceptions;

import com.nemezor.nemgine.misc.Logger;
import com.nemezor.nemgine.misc.Registry;

public class ModelException extends Exception {

	private static final long serialVersionUID = 1L;

	private String thrower;
	private String name;
	
	public ModelException(String message) {
		super(message);
	}
	
	public ModelException(String message, String thrower) {
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
		Logger.log(thrower == null ? Registry.MODEL_EXCEPTION_NO_ACCESSOR : thrower, getLocalizedMessage(), false);
		if (name != null) {
			Logger.log(null, name, false);
		}
	}
}
