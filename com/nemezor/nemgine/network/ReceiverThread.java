package com.nemezor.nemgine.network;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.net.SocketException;

import com.nemezor.nemgine.exceptions.NetworkException;
import com.nemezor.nemgine.misc.Registry;

public class ReceiverThread extends Thread {

	private InputStream input;
	private ISocket sock;
	private NetworkObject obj;

	protected ReceiverThread(NetworkObject obj, ISocket sock, InputStream input) {
		this.input = input;
		this.sock = sock;
		this.obj = obj;
	}
	
	public void run() {
		ObjectInputStream objectInput = null;
		try {
			objectInput = new ObjectInputStream(input);
		} catch (IOException e) {
			NetworkException ex = new NetworkException(Registry.NETWORK_MANAGER_IO_ERROR);
			ex.setThrower(Registry.NETWORK_MANAGER_NAME);
			ex.setAddress(new Address(sock.getAddress(), sock.getPort()));
			ex.printStackTrace();
			e.printStackTrace();
			return;
		}
		while (sock.isConnected() && !sock.isClosed()) {
			try {
				IPacket next = (IPacket) objectInput.readObject();
				if (next != null) {
					NetworkManager.callPacketReceivedEvent(obj, next);
				}
			} catch (EOFException | SocketException e) {
				NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.CONNECTION_LOST, e));
				return;
			} catch (InvalidClassException e) {
				NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.SERIALIZATION_ERROR, e));
			} catch (IOException e) {
				NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.IO_ERROR, e));
			} catch (ClassNotFoundException e) {
				NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.SERIALIZATION_ERROR, e));
			}
		}
	}
}
