package com.nemezor.nemgine.network;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NetworkListener {

	private Method[] packetMethods;
	private Method[] infoMethods;
	private Method[] connMethods;
	private Object instance;
	
	protected NetworkListener(Object instance, Method[] methods, Method[] exMethods, Method[] connMethods) {
		this.packetMethods = methods;
		this.connMethods = connMethods;
		this.infoMethods = exMethods;
		this.instance = instance;
	}
	
	protected void invokePacket(NetworkObject netObject, IPacket packet) {
		for (Method m : packetMethods) {
			try {
				m.invoke(instance, netObject, packet);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void invokeInfo(NetworkObject netObject, NetworkInfo info) {
		for (Method m : infoMethods) {
			try {
				m.invoke(instance, netObject, info);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void invokeConn(NetworkObject netObject) {
		for (Method m : connMethods) {
			try {
				m.invoke(instance, netObject);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
}
