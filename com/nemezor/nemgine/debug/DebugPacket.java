package com.nemezor.nemgine.debug;

import com.nemezor.nemgine.network.IPacket;

public class DebugPacket implements IPacket {

	private String data;
	
	public DebugPacket(String data) {
		this.data = data;
	}
	
	public String getData() {
		return data;
	}
}
