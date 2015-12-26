package com.nemezor.nemgine.tests.network_test;

import com.nemezor.nemgine.console.Console;
import com.nemezor.nemgine.console.Input;
import com.nemezor.nemgine.debug.DebugPacket;
import com.nemezor.nemgine.main.Application;
import com.nemezor.nemgine.main.IMainTickLoop;
import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.misc.Logger;
import com.nemezor.nemgine.misc.Side;
import com.nemezor.nemgine.network.Address;
import com.nemezor.nemgine.network.IPacket;
import com.nemezor.nemgine.network.Network;
import com.nemezor.nemgine.network.NetworkInfo;
import com.nemezor.nemgine.network.NetworkManager;
import com.nemezor.nemgine.network.NetworkObject;

public class NetworkTestServer implements IMainTickLoop {

	private volatile NetworkObject client;
	
	@Application(contained=true, path="tests/network", name="Network Test - Server", side=Side.SERVER)
	public void entry() {
		int thread = Nemgine.generateThreads("Tick", false);
		Nemgine.bindTickLoop(thread, this);
		Nemgine.startThread(thread);
	}
	
	public static void main(String[] args) {
		Nemgine.start(args, NetworkTestServer.class);
	}
	
	@Input
	public void consoleInput(String input) {
		if (input.equals("exit")) {
			Nemgine.shutDown();
		}
	}
	
	@Network
	public void connectionEstablished(NetworkObject obj) {
		setClient(obj);
		Logger.log("Connection established!");
	}
	
	@Network
	public void networkInfo(NetworkObject obj, NetworkInfo info) {
		Console.out.println(info.getType());
	}
	
	@Network
	public void packetReceived(NetworkObject obj, IPacket packet) {
		
	}
	
	private synchronized void setClient(NetworkObject obj) {
		client = obj;
	}
	
	private int rotation = 0;
	
	@Override
	public void tick() {
		if (client != null) {
			client.sendPacket(new DebugPacket(String.valueOf(rotation)));
			rotation++;
		}
	}
	
	@Override
	public void setUpTick() {
		NetworkManager.registerListenerClass(this);
		Console.registerInputListener(this);
		int sock = NetworkManager.generateServerSockets();
		if (NetworkManager.connect(sock, new Address(null, 1338))) {
			Logger.log("Bleh");
		}
	}

	@Override
	public void cleanUpTick() {
		Nemgine.exit(0);
	}

	@Override
	public void updateTickSecond(int ticks, long averageInterval) {
		
	}

	@Override
	public long getTickSleepInterval() {
		return 16;
	}

	@Override
	public int getTickTickskipTreshold() {
		return 10;
	}
}
