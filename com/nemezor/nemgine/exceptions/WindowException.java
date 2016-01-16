package com.nemezor.nemgine.exceptions;

import com.nemezor.nemgine.misc.Logger;
import com.nemezor.nemgine.misc.Registry;

public class WindowException extends Exception {

	private static final long serialVersionUID = 1L;

	private String thrower;
	
	public WindowException(String message) {
		super(message);
	}
	
	public WindowException(String message, String thrower) {
		super(message);
		this.thrower = thrower;
	}
	
	public void setThrower(String thrower) {
		this.thrower = thrower;
	}
	
	public String getThrower() {
		return thrower;
	}
	
	public void printStackTrace() {
		Logger.log(thrower == null ? Registry.WINDOW_EXCEPTION_NO_ACCESSOR : thrower, getLocalizedMessage(), false);
	}
}
