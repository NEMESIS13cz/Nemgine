package com.nemezor.nemgine.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.nemezor.nemgine.misc.Registry;

public class Socket {

	private java.net.Socket sock;
	private int state;
	protected ArrayList<IPacket> queue = new ArrayList<IPacket>();
	
	public Socket(Address addr) throws UnknownHostException, IOException {
		sock = new java.net.Socket(addr.getIp(), addr.getPort());
		startThread(sock.getInputStream(), sock.getOutputStream());
	}
	
	protected Socket(int state) {
		this.state = state;
	}
	
	protected void conn(Address addr) throws UnknownHostException, IOException {
		if (state == Registry.INVALID) {
			state = 0;
			sock = new java.net.Socket(addr.getIp(), addr.getPort());
			startThread(sock.getInputStream(), sock.getOutputStream());
		}
	}
	
	public boolean isClosed() {
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
	
	protected int getState() {
		return state;
	}
	
	protected java.net.Socket getInternalSocket() {
		return sock;
	}
	
	protected synchronized void sendPacket(IPacket packet) {
		queue.add(packet);
	}
	
	protected synchronized IPacket pop() {
		while (queue.size() == 0) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return queue.remove(0);
	}
	
	private void startThread(InputStream input, OutputStream output) {
		Thread threadIn = new ReceiverThread(this, input);
		Thread threadOut = new TransmitterThread(this, output);
		threadIn.start();
		threadOut.start();
	}
}
