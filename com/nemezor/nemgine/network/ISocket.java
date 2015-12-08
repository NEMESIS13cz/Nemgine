package com.nemezor.nemgine.network;

import java.io.IOException;

import com.nemezor.nemgine.misc.Side;

public interface ISocket {

	public boolean isConnected();
	public boolean isClosed();
	public String getAddress();
	public int getPort();
	public IPacket pop(NetworkObject client);
	public int getState();
	public NetworkObject conn(int id, Address address);
	public void sendPacket(NetworkObject client, IPacket packet);
	public void close() throws IOException;
	public Side type();
	public NetworkObject getObj();
}
