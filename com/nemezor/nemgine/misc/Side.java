package com.nemezor.nemgine.misc;

public enum Side {

	CLIENT, SERVER;
	
	public boolean isClient() {
		return this == CLIENT;
	}
	
	public boolean isServer() {
		return this == SERVER;
	}
}
