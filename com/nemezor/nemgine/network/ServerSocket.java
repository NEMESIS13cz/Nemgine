package com.nemezor.nemgine.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.nemezor.nemgine.misc.Registry;
import com.nemezor.nemgine.misc.Side;

public class ServerSocket implements ISocket {

	private java.net.ServerSocket sock;
	private int state;
	protected HashMap<NetworkObject, ArrayList<IPacket>> queue = new HashMap<NetworkObject, ArrayList<IPacket>>();
	
	public ServerSocket(int id, Address addr) {
		try {
			sock = new java.net.ServerSocket(addr.getPort());
		} catch (IOException e) {
			NetworkManager.callInfoEvent(null, new NetworkInfo(NetworkInfoType.IO_ERROR, e));
			return;
		} catch (SecurityException e) {
			NetworkManager.callInfoEvent(null, new NetworkInfo(NetworkInfoType.SECURITY, e));
			return;
		}
		startAcceptorThread(id);
	}
	
	protected ServerSocket(int state) {
		this.state = state;
	}
	
	public NetworkObject conn(int id, Address addr) {
		if (state == Registry.INVALID) {
			state = 0;
			try {
				sock = new java.net.ServerSocket(addr.getPort());
			} catch (IOException e) {
				NetworkManager.callInfoEvent(null, new NetworkInfo(NetworkInfoType.IO_ERROR, e));
				return new NetworkObject(Registry.INVALID);
			} catch (SecurityException e) {
				NetworkManager.callInfoEvent(null, new NetworkInfo(NetworkInfoType.SECURITY, e));
				return new NetworkObject(Registry.INVALID);
			}
			startAcceptorThread(id);
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
		return "";
	}
	
	public int getPort() {
		return sock.getLocalPort();
	}
	
	public int getState() {
		return state;
	}

	public boolean isConnected() {
		return true;
	}
	
	public synchronized void sendPacket(NetworkObject client, IPacket packet) {
		if (queue.get(client) == null) {
			return;
		}
		queue.get(client).add(packet);
	}
	
	public synchronized IPacket pop(NetworkObject client) {
		ArrayList<IPacket> packets = queue.get(client);
		if (packets == null || packets.size() == 0) {
			return null;
		}
		return queue.get(client).remove(0);
	}
	
	private void startAcceptorThread(int id) {
		Thread t = new Thread() {
			
			public void run() {
				NetworkObject obj = null;
				
				while (!sock.isClosed()) {
					try {
						java.net.Socket client = sock.accept();
						obj = new NetworkObject(id);
						queue.put(obj, new ArrayList<IPacket>());
						NetworkManager.objects.add(obj);
						startThread(obj, client.getInputStream(), client.getOutputStream());
						NetworkManager.callConnectionEstablishedEvent(obj);
					} catch (IOException e) {
						NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.IO_ERROR, e));
					} catch (SecurityException e) {
						NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.SECURITY, e));
					}
					obj = null;
				}
			}
		};
		t.start();
	}
	
	private void startThread(NetworkObject client, InputStream input, OutputStream output) {
		Thread threadIn = new ReceiverThread(client, this, input);
		Thread threadOut = new TransmitterThread(client, this, output);
		threadIn.start();
		threadOut.start();
	}
	
	public Side type() {
		return Side.SERVER;
	}
	
	public NetworkObject getObj() {
		return null;
	}
}
