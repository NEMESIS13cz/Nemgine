package com.nemezor.nemgine.network.packet;

import java.security.PublicKey;

import com.nemezor.nemgine.network.IPacket;

public class RSAPublicKeyPacket implements IPacket 	{

	private static final long serialVersionUID = 1L;

	private PublicKey key;
	
	public RSAPublicKeyPacket(PublicKey key) {
		this.key = key;
	}
	
	public PublicKey getKey() {
		return key;
	}
}
