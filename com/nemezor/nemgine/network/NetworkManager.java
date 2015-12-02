package com.nemezor.nemgine.network;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;

import com.nemezor.nemgine.exceptions.NetworkException;
import com.nemezor.nemgine.misc.Registry;

public class NetworkManager {

	private static boolean initialized = false;
	private static HashMap<Integer, Socket> sockets = new HashMap<Integer, Socket>();
	private static int socketCounter = 0;
	
	public static synchronized void initialize() {
		
		
		
		initialized = true;
	}
	
	public static synchronized int generateSockets() {
		socketCounter++;
		sockets.put(socketCounter, null);
		return socketCounter;
	}
	
	public static synchronized boolean connect(int id, Address address) {
		if (sockets.get(id) != null) {
			return false;
		}
		try {
			Socket sock = new Socket(address.getIp(), address.getPort());
			sockets.put(id, sock);
		} catch (UnknownHostException e) {
			NetworkException ex = new NetworkException(Registry.NETWORK_MANAGER_UNKNOWN_HOST_ERROR);
			ex.setThrower(Registry.NETWORK_MANAGER_NAME);
			ex.setAddress(address);
			ex.printStackTrace();
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			NetworkException ex = new NetworkException(Registry.NETWORK_MANAGER_IO_ERROR);
			ex.setThrower(Registry.NETWORK_MANAGER_NAME);
			ex.setAddress(address);
			ex.printStackTrace();
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static void dispose(int id) {
		Socket sock = sockets.get(id);
		if (sock != null && !sock.isClosed()) {
			try {
				sock.close();
			} catch (IOException e) {
				NetworkException ex = new NetworkException(Registry.NETWORK_MANAGER_IO_ERROR);
				ex.setThrower(Registry.NETWORK_MANAGER_NAME);
				ex.setAddress(new Address(sock));
				ex.printStackTrace();
				e.printStackTrace();
			}
		}
		sockets.remove(id);
	}
	
	public static void disposeAll() {
		Iterator<Integer> i = sockets.keySet().iterator();
		
		while (i.hasNext()) {
			Socket sock = sockets.get(i.next());
			if (sock != null && !sock.isClosed()) {
				try {
					sock.close();
				} catch (IOException e) {
					NetworkException ex = new NetworkException(Registry.NETWORK_MANAGER_IO_ERROR);
					ex.setThrower(Registry.NETWORK_MANAGER_NAME);
					ex.setAddress(new Address(sock));
					ex.printStackTrace();
					e.printStackTrace();
				}
			}
		}
		sockets.clear();
	}
}
