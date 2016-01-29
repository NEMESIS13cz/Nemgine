package com.nemezor.nemgine.network;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;

import com.nemezor.nemgine.exceptions.NetworkException;
import com.nemezor.nemgine.misc.Registry;
import com.nemezor.nemgine.network.packet.AESKeyPacket;
import com.nemezor.nemgine.network.packet.RSAPublicKeyPacket;

public class TransmitterThread extends Thread {

	private ISocket sock;
	private OutputStream output;
	private NetworkObject obj;
	private Cipher cipher;
	private RTLock lock;

	protected TransmitterThread(NetworkObject client, ISocket sock, OutputStream output, Cipher cipher, RTLock lock) {
		this.output = output;
		this.sock = sock;
		this.obj = client;
		this.cipher = cipher;
		this.lock = lock;
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
		
		if (lock.side.isClient()) {
			while (!lock.keyReceived) {
				try {
					sleep(1);
				} catch (InterruptedException e) {}
			}
			
			try {
				Cipher tempCipher = Cipher.getInstance(Registry.KEY_EXCHANGE_ENCRYPTION_ALGORITHM);
				tempCipher.init(Cipher.ENCRYPT_MODE, lock.encrKey);
				AESKeyPacket packet = new AESKeyPacket(lock.keySpec);
				objectOutput.writeObject(new SealedObject(packet, tempCipher));
			} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | IOException e) {
				NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.INITIALIZATION_ERROR, e));
				return;
			}
			
			lock.AESinit = true;
		}else{
			try {
				objectOutput.writeObject(new RSAPublicKeyPacket(lock.pair.getPublic()));
				lock.keySent = true;
			} catch (IOException e) {
				NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.INITIALIZATION_ERROR, e));
				return;
			}
			
			while (!lock.AESinit) {
				try {
					sleep(1);
				} catch (InterruptedException e) {}
			}
		}
		
		try {
			cipher.init(Cipher.ENCRYPT_MODE, lock.keySpec);
		} catch (InvalidKeyException e) {
			NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.INITIALIZATION_ERROR, e));
			return;
		}
		
		while (sock.isConnected() && !sock.isClosed()) {
			IPacket packet = sock.pop(obj);
			if (packet != null) {
				try {
					objectOutput.writeObject(new SealedObject(packet, cipher));
				} catch (SocketException e) {
					NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.CONNECTION_LOST, e));
					return;
				} catch (InvalidClassException | IllegalBlockSizeException e) {
					NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.SERIALIZATION_ERROR, e));
				} catch (IOException e) {
					NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.IO_ERROR, e));
				}
			}
			try {
				sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
