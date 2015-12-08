package com.nemezor.nemgine.network;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.SocketException;

import com.nemezor.nemgine.exceptions.NetworkException;
import com.nemezor.nemgine.misc.Registry;

public class TransmitterThread extends Thread {

	private ISocket sock;
	private OutputStream output;
	private NetworkObject obj;

	protected TransmitterThread(NetworkObject client, ISocket sock, OutputStream output) {
		this.output = output;
		this.sock = sock;
		this.obj = client;
	}
	
	public void run() {
		ObjectOutputStream objectOutput = null;
		try {
			objectOutput = new ObjectOutputStream(output);
		} catch (IOException e) {
			NetworkException ex = new NetworkException(Registry.NETWORK_MANAGER_IO_ERROR);
			ex.setThrower(Registry.NETWORK_MANAGER_NAME);
			ex.setAddress(new Address(sock.getAddress(), sock.getPort()));
			ex.printStackTrace();
			e.printStackTrace();
			return;
		}
		while (sock.isConnected() && !sock.isClosed()) {
			IPacket packet = sock.pop(obj);
			if (packet != null) {
				try {
					objectOutput.writeObject(packet);
				} catch (SocketException e) {
					NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.CONNECTION_LOST, e));
					return;
				} catch (InvalidClassException e) {
					NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.SERIALIZATION_ERROR, e));
				} catch (IOException e) {
					NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.IO_ERROR, e));
				}
			}
			try {
				sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
