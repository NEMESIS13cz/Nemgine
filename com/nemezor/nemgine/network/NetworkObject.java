package com.nemezor.nemgine.network;

public final class NetworkObject {

	protected int id;
	protected Address addr;
	
	protected NetworkObject(int id) {
		this.id = id;
	}
	
	public void sendPacket(IPacket packet) {
		NetworkManager.sendPacket(this, id, packet);
	}
	
	public Address getAddress() {
		return addr;
	}
}
