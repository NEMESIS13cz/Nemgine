package com.nemezor.nemgine.tests.network;

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
import com.nemezor.nemgine.network.NetworkInfoType;
import com.nemezor.nemgine.network.NetworkManager;
import com.nemezor.nemgine.network.NetworkObject;

public class NetworkTestServer implements IMainTickLoop {

	public static void main(String[] args) {
		Nemgine.start(args, NetworkTestServer.class);
	}
	
	private volatile NetworkObject client;
	private volatile int socketId = 0;
	
	@Application(contained=true, path="tests/network", name="Network Test | Server", side=Side.SERVER)
	public void entry() {
		int thread = Nemgine.generateThreads("Tick", false);
		Nemgine.bindTickLoop(thread, this);
		Nemgine.startThread(thread);
	}
	
	@Input
	public void consoleInput(String input) {
		if (input.equals("exit")) {
			Nemgine.shutDown();
		}
	}

	@Network
	public void connectionEstablished(NetworkObject obj) {
		Logger.log("Connection established");
		client = obj;
	}
	
	@Network
	public void networkInfo(NetworkObject obj, NetworkInfo info) {
		if (info.getType() == NetworkInfoType.CONNECTION_LOST) {
			Logger.log("Connection closed");
		}
	}

	@Network
	public void packetReceived(NetworkObject obj, IPacket packet) {
		
	}
	
	@Override
	public void tick() {
		if (client == null) {
			return;
		}
		client.sendPacket(new DebugPacket("blargh"));
	}

	@Override
	public void setUpTick() {
		NetworkManager.registerListenerClass(this);
		socketId = NetworkManager.generateServerSockets();
		NetworkManager.connect(socketId, new Address("localhost", 65535));
		Console.registerInputListener(this);
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
