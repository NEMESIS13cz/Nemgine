package com.nemezor.nemgine.network;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NetworkListener {

	private Method[] methods;
	private Object instance;
	
	protected NetworkListener(Object instance, Method[] methods) {
		this.methods = methods;
		this.instance = instance;
	}
	
	protected void invoke(IPacket packet) {
		for (Method m : methods) {
			try {
				m.invoke(instance, packet);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
}
