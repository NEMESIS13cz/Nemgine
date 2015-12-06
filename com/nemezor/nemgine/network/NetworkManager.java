package com.nemezor.nemgine.network;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.nemezor.nemgine.exceptions.NetworkException;
import com.nemezor.nemgine.misc.Registry;

public class NetworkManager {

	private static HashMap<Integer, Socket> sockets = new HashMap<Integer, Socket>();
	private static int socketCounter = 0;
	private static ArrayList<NetworkListener> listeners = new ArrayList<NetworkListener>();
	
	public static synchronized int generateSockets() {
		socketCounter++;
		sockets.put(socketCounter, new Socket(Registry.INVALID));
		return socketCounter;
	}
	
	public static synchronized boolean connect(int id, Address address) {
		if (sockets.get(id) != null || sockets.get(id).getState() != Registry.INVALID) {
			return false;
		}
		try {
			sockets.get(id).conn(address);
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
	
	public static boolean sendPacket(int id, IPacket packet) {
		if (sockets.get(id) == null || sockets.get(id).getState() == Registry.INVALID) {
			return false;
		}
		sockets.get(id).sendPacket(packet);
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
	
	public static synchronized void registerListenerClass(Object listener) {
		Method[] methods = listener.getClass().getMethods();
		ArrayList<Method> temp = new ArrayList<Method>();
		for (Method m : methods) {
			Annotation[] ann = m.getDeclaredAnnotationsByType(Network.class);
			if (ann.length > 0 && m.getParameterCount() == 1 && m.getParameters()[0].getType() == IPacket.class) {
				temp.add(m);
			}
		}
		Method[] methods_ = new Method[temp.size()];
		for (int i = 0; i < methods_.length; i++) {
			methods_[i] = temp.get(i);
		}
		listeners.add(new NetworkListener(listener, methods_));
	}
	
	protected static synchronized void callPacketReceivedEvent(IPacket packet) {
		for (NetworkListener m : listeners) {
			m.invoke(packet);
		}
	}
}
