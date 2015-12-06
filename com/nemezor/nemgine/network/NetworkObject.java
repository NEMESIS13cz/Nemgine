package com.nemezor.nemgine.network;

public final class NetworkObject {

	private int id;
	
	protected NetworkObject(int id) {
		this.id = id;
	}
	
	public void sendPacket(IPacket packet) {
		NetworkManager.sendPacket(this, id, packet);
	}
}
