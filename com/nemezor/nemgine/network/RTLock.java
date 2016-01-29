package com.nemezor.nemgine.network;

import java.security.KeyPair;
import java.security.PublicKey;

import javax.crypto.spec.SecretKeySpec;

import com.nemezor.nemgine.misc.Side;

public class RTLock {

	protected final Side side;
	protected volatile KeyPair pair = null;
	protected volatile SecretKeySpec keySpec;
	protected volatile PublicKey encrKey = null;
	protected volatile boolean keySent = false;
	protected volatile boolean keyReceived = false;
	protected volatile boolean AESinit = false;

	protected RTLock(KeyPair pair) {
		this.side = Side.SERVER;
		this.pair = pair;
	}
	
	protected RTLock(SecretKeySpec key) {
		this.side = Side.CLIENT;
		this.keySpec = key;
	}
}
