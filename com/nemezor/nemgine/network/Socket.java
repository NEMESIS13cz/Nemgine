package com.nemezor.nemgine.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.nemezor.nemgine.misc.Registry;
import com.nemezor.nemgine.misc.Side;

public class Socket implements ISocket {

	private java.net.Socket sock;
	private int state;
	private NetworkObject obj;
	protected ArrayList<IPacket> queue = new ArrayList<IPacket>();
	
	public Socket(int id, Address addr) throws UnknownHostException, IOException {
		sock = new java.net.Socket(addr.getIp(), addr.getPort());
		startThread(sock.getInputStream(), sock.getOutputStream());
		obj = new NetworkObject(id);
		NetworkManager.objects.add(obj);
	}
	
	protected Socket(int state) {
		this.state = state;
	}
	
	public NetworkObject conn(int id, Address addr) throws UnknownHostException, IOException {
		if (state == Registry.INVALID) {
			state = 0;
			sock = new java.net.Socket(addr.getIp(), addr.getPort());
			startThread(sock.getInputStream(), sock.getOutputStream());
			obj = new NetworkObject(id);
			NetworkManager.callPacketReceivedEvent(obj, null);
			return obj;
		}
		return null;
	}
	
	public boolean isClosed() {
		if (sock == null) {
			return true;
		}
		return sock.isClosed();
	}
	
	public void close() throws IOException {
		sock.close();
	}
	
	public String getAddress() {
		return sock.getRemoteSocketAddress().toString();
	}
	
	public int getPort() {
		return sock.getPort();
	}
	
	public int getState() {
		return state;
	}

	public boolean isConnected() {
		return sock.isConnected();
	}
	
	public synchronized void sendPacket(NetworkObject client, IPacket packet) {
		queue.add(packet);
	}
	
	public synchronized IPacket pop(NetworkObject client) {
		if (queue.size() > 0) {
			return queue.remove(0);
		}
		return null;
	}
	
	protected void startThread(InputStream input, OutputStream output) {
		Thread threadIn = new ReceiverThread(obj, this, input);
		Thread threadOut = new TransmitterThread(this, output);
		threadIn.start();
		threadOut.start();
	}
	
	public Side type() {
		return Side.CLIENT;
	}
	
	public NetworkObject getObj() {
		return obj;
	}
}
