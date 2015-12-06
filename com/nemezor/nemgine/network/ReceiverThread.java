package com.nemezor.nemgine.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

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
		try {
			while (sock.isConnected() && !sock.isClosed()) {
				try {
					IPacket next = (IPacket) objectInput.readObject();
					if (next != null) {
						NetworkManager.callPacketReceivedEvent(obj, next);
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			NetworkException ex = new NetworkException(Registry.NETWORK_MANAGER_IO_ERROR);
			ex.setThrower(Registry.NETWORK_MANAGER_NAME);
			ex.setAddress(new Address(sock.getAddress(), sock.getPort()));
			ex.printStackTrace();
			e.printStackTrace();
		}
	}
}
