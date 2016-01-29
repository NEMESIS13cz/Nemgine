package com.nemezor.nemgine.network;

import com.nemezor.nemgine.exceptions.NetworkException;
import com.nemezor.nemgine.misc.Registry;

public class Address {

	private String IP;
	private int port;
	
	public Address(String IP, int port) {
		this.IP = IP;
		this.port = port;
		if (port < Registry.IP_ADDRESS_MINIMUM_PORT_VALUE || port > Registry.IP_ADDRESS_MAXIMUM_PORT_VALUE) {
			NetworkException ex = new NetworkException(Registry.IP_ADDRESS_INVALID_PORT);
			ex.setThrower(Registry.IP_ADDRESS_NAME);
			ex.setAddress(this);
			ex.printStackTrace();
		}
	}
	
	public Address(ISocket socket) {
		IP = socket.getAddress();
		port = socket.getPort();
	}
	
	public String getIp() {
		return IP;
	}
	
	public int getPort() {
		return port;
	}
	
	public String toString() {
		return IP + ":" + port;
	}
	
	public Address clone() {
		return new Address(IP, port);
	}
}
