package com.nemezor.nemgine.network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.nemezor.nemgine.exceptions.NetworkException;
import com.nemezor.nemgine.misc.Registry;

public class TransmitterThread extends Thread {

	private Socket sock;
	private OutputStream output;
	
	protected TransmitterThread(Socket sock, OutputStream output) {
		this.output = output;
		this.sock = sock;
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
		try {
			while (sock.getInternalSocket().isConnected() && !sock.isClosed()) {
				objectOutput.writeObject(sock.pop());
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
