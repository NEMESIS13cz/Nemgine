package com.nemezor.nemgine.exceptions;

import com.nemezor.nemgine.misc.Logger;
import com.nemezor.nemgine.misc.Registry;
import com.nemezor.nemgine.network.Address;

public class NetworkException extends Exception {

	private static final long serialVersionUID = 1L;

	private String thrower;
	private Address IP;
	
	public NetworkException(String message) {
		super(message);
	}
	
	public NetworkException(String message, String thrower) {
		super(message);
		this.thrower = thrower;
	}
	
	public void setAddress(Address ip) {
		this.IP = ip;
	}
	
	public void setThrower(String thrower) {
		this.thrower = thrower;
	}
	
	public Address getAddress() {
		return IP;
	}
	
	public String getThrower() {
		return thrower;
	}
	
	public void printStackTrace() {
		Logger.log(thrower == null ? Registry.NETWORK_EXCEPTION_NO_ACCESSOR : thrower, getLocalizedMessage());
		if (IP != null) {
			Logger.log(null, IP.toString());
		}
	}
}
