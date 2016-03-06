package com.nemezor.nemgine.network;

import java.io.IOException;

import com.nemezor.nemgine.misc.Side;

public interface ISocket {
	
	boolean isConnected();
	boolean isClosed();
	String getAddress();
	int getPort();
	IPacket pop(NetworkObject client);
	int getState();
	NetworkObject conn(int id, Address address);
	void sendPacket(NetworkObject client, IPacket packet);
	void close() throws IOException;
	Side type();
	NetworkObject getObj();
}
