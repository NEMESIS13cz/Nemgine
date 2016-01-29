package com.nemezor.nemgine.network;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.net.SocketException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;

import com.nemezor.nemgine.exceptions.NetworkException;
import com.nemezor.nemgine.misc.Registry;
import com.nemezor.nemgine.network.packet.AESKeyPacket;
import com.nemezor.nemgine.network.packet.RSAPublicKeyPacket;

public class ReceiverThread extends Thread {

	private InputStream input;
	private ISocket sock;
	private NetworkObject obj;
	private Cipher cipher;
	private RTLock lock;

	protected ReceiverThread(NetworkObject obj, ISocket sock, InputStream input, Cipher cipher, RTLock lock) {
		this.input = input;
		this.sock = sock;
		this.obj = obj;
		this.cipher = cipher;
		this.lock = lock;
	}
	
	public void run() {
		ObjectInputStream objectInput = null;
		try {
			objectInput = new ObjectInputStream(input);
		} catch (IOException e) {
			NetworkException ex = new NetworkException(Registry.NETWORK_MANAGER_IO_ERROR);
			ex.setThrower(Registry.NETWORK_MANAGER_NAME);
			ex.setAddress(new Address(sock.getAddress(), sock.getPort()));
			ex.printStackTrace();
			e.printStackTrace();
			return;
		}
		
		if (lock.side.isClient()) {
			try {
				RSAPublicKeyPacket packet = (RSAPublicKeyPacket) objectInput.readObject();
				lock.encrKey = packet.getKey();
				lock.keyReceived = true;
			} catch (ClassNotFoundException | IOException e) {
				NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.INITIALIZATION_ERROR, e));
				return;
			}
			
			while (!lock.AESinit) {
				try {
					sleep(1);
				} catch (InterruptedException e) {}
			}
		}else{
			while (!lock.keySent) {
				try {
					sleep(1);
				} catch (InterruptedException e) {}
			}
			
			try {
				Cipher tempCipher = Cipher.getInstance(Registry.KEY_EXCHANGE_ENCRYPTION_ALGORITHM);
				tempCipher.init(Cipher.DECRYPT_MODE, lock.pair.getPrivate());
				SealedObject epacket = (SealedObject) objectInput.readObject();
				AESKeyPacket packet = (AESKeyPacket) epacket.getObject(tempCipher);
				lock.keySpec = packet.getKeySpec();
			} catch (ClassNotFoundException | IOException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
				NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.INITIALIZATION_ERROR, e));
				return;
			}

			lock.AESinit = true;
		}
		
		try {
			cipher.init(Cipher.DECRYPT_MODE, lock.keySpec);
		} catch (InvalidKeyException e) {
			NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.INITIALIZATION_ERROR, e));
			return;
		}
		
		while (sock.isConnected() && !sock.isClosed()) {
			try {
				SealedObject enext = (SealedObject) objectInput.readObject();
				IPacket next = (IPacket) enext.getObject(cipher);
				if (next != null) {
					NetworkManager.callPacketReceivedEvent(obj, next);
				}
			} catch (EOFException | SocketException e) {
				NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.CONNECTION_LOST, e));
				return;
			} catch (InvalidClassException | IllegalBlockSizeException | BadPaddingException e) {
				NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.SERIALIZATION_ERROR, e));
			} catch (IOException e) {
				NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.IO_ERROR, e));
			} catch (ClassNotFoundException e) {
				NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.SERIALIZATION_ERROR, e));
			}
		}
	}
}
