package com.nemezor.nemgine.network.packet;

import javax.crypto.spec.SecretKeySpec;

import com.nemezor.nemgine.network.IPacket;

public class AESKeyPacket implements IPacket 	{

	private static final long serialVersionUID = 1L;

	private SecretKeySpec key;
	
	public AESKeyPacket(SecretKeySpec key) {
		this.key = key;
	}
	
	public SecretKeySpec getKeySpec() {
		return key;
	}
}
