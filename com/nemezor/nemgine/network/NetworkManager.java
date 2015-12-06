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
import com.nemezor.nemgine.misc.Side;

public class NetworkManager {

	private static HashMap<Integer, ISocket> sockets = new HashMap<Integer, ISocket>();
	private static int socketCounter = 0;
	private static ArrayList<NetworkListener> listeners = new ArrayList<NetworkListener>();
	protected static ArrayList<NetworkObject> objects = new ArrayList<NetworkObject>();
	
	public static synchronized int generateSockets(Side type) {
		socketCounter++;
		if (type == Side.CLIENT) {
			sockets.put(socketCounter, new Socket(Registry.INVALID));
		}else{
			sockets.put(socketCounter, new ServerSocket(Registry.INVALID));
		}
		return socketCounter;
	}
	
	public static synchronized boolean connect(int id, Address address) {
		if (sockets.get(id) != null && sockets.get(id).getState() != Registry.INVALID) {
			return false;
		}
		try {
			NetworkObject obj = sockets.get(id).conn(id, address);
			if (sockets.get(id).type() == Side.CLIENT) {
				if (obj == null) {
					return false;
				}
				objects.add(obj);
			}
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
	
	protected static boolean sendPacket(NetworkObject netObject, int id, IPacket packet) {
		ISocket socket = sockets.get(id);
		if (socket == null || socket.getState() == Registry.INVALID) {
			return false;
		}
		socket.sendPacket(netObject, packet);
		return true;
	}
	
	public static void dispose(int id) {
		ISocket sock = sockets.get(id);
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
		if (sock.getObj() != null) {
			objects.remove(sock.getObj());
		}
	}
	
	public static void disposeAll() {
		Iterator<Integer> i = sockets.keySet().iterator();
		
		while (i.hasNext()) {
			ISocket sock = sockets.get(i.next());
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
		objects.clear();
	}
	
	public static synchronized void registerListenerClass(Object listener) {
		Method[] methods = listener.getClass().getMethods();
		ArrayList<Method> temp = new ArrayList<Method>();
		for (Method m : methods) {
			Annotation[] ann = m.getDeclaredAnnotationsByType(Network.class);
			if (ann.length > 0 && m.getParameterCount() == 2 && m.getParameters()[0].getType() == NetworkObject.class && m.getParameters()[1].getType() == IPacket.class) {
				temp.add(m);
			}
		}
		Method[] methods_ = new Method[temp.size()];
		for (int i = 0; i < methods_.length; i++) {
			methods_[i] = temp.get(i);
		}
		listeners.add(new NetworkListener(listener, methods_));
	}
	
	protected static synchronized void callPacketReceivedEvent(NetworkObject netObject, IPacket packet) {
		for (NetworkListener m : listeners) {
			m.invoke(netObject, packet);
		}
	}
}
