package com.nemezor.nemgine.network;

public final class NetworkInfo {

	private NetworkInfoType type;
	private Exception ex;
	
	protected NetworkInfo(NetworkInfoType type, Exception e) {
		this.type = type;
		this.ex = e;
	}
	
	public NetworkInfoType getType() {
		return type;
	}
	
	public Exception getException() {
		return ex;
	}
}
