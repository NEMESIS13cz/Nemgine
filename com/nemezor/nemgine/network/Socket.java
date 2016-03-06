package com.nemezor.nemgine.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import com.nemezor.nemgine.misc.Registry;
import com.nemezor.nemgine.misc.Side;

public class Socket implements ISocket {

	private java.net.Socket sock;
	private int state;
	private NetworkObject obj;
	private int timeout;
	protected ArrayList<IPacket> queue = new ArrayList<IPacket>();
	
	protected Socket(int id, Address addr, int timeout) {
		if (timeout > 0) {
			this.timeout = timeout;
		}else{
			this.timeout = Registry.SOCKET_DEFAULT_TIMEOUT;
		}
		obj = new NetworkObject(id);
		try {
			sock = new java.net.Socket();
			sock.connect(new InetSocketAddress(addr.getIp(), addr.getPort()), timeout);
		} catch (UnknownHostException e) {
			NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.UNKNOWN_HOST, e));
			return;
		} catch (SocketTimeoutException e) {
			NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.CONNECTION_TIMED_OUT, e));
			return;
		} catch (IOException e) {
			NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.IO_ERROR, e));
			return;
		} catch (SecurityException e) {
			NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.SECURITY, e));
			return;
		}
		try {
			startThread(sock.getInputStream(), sock.getOutputStream());
		} catch (IOException e) {
			NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.IO_ERROR, e));
			return;
		}
		obj.addr = addr.clone();
		NetworkManager.objects.add(obj);
		NetworkManager.callConnectionEstablishedEvent(obj);
	}
	
	protected Socket(int state, int timeout) {
		this.state = state;
		if (timeout > 0) {
			this.timeout = timeout;
		}else{
			this.timeout = Registry.SOCKET_DEFAULT_TIMEOUT;
		}
	}
	
	public NetworkObject conn(int id, Address addr) {
		if (state == Registry.INVALID) {
			state = 0;
			obj = new NetworkObject(id);
			try {
				sock = new java.net.Socket();
				sock.connect(new InetSocketAddress(addr.getIp(), addr.getPort()), timeout);
			} catch (UnknownHostException e) {
				NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.UNKNOWN_HOST, e));
				return null;
			} catch (SocketTimeoutException e) {
				NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.CONNECTION_TIMED_OUT, e));
				return null;
			} catch (IOException e) {
				NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.IO_ERROR, e));
				return null;
			} catch (SecurityException e) {
				NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.SECURITY, e));
				return null;
			}
			try {
				startThread(sock.getInputStream(), sock.getOutputStream());
			} catch (IOException e) {
				NetworkManager.callInfoEvent(obj, new NetworkInfo(NetworkInfoType.IO_ERROR, e));
				return null;
			}
			obj.addr = addr.clone();
			NetworkManager.callConnectionEstablishedEvent(obj);
			return obj;
		}
		return null;
	}
	
	public boolean isClosed() {
		if (sock == null) {
			return true;
		}
		return sock.isClosed();
	}
	
	public void close() throws IOException {
		sock.close();
	}
	
	public String getAddress() {
		return sock.getRemoteSocketAddress().toString();
	}
	
	public int getPort() {
		return sock.getPort();
	}
	
	public int getState() {
		return state;
	}

	public boolean isConnected() {
		return sock.isConnected();
	}
	
	public synchronized void sendPacket(NetworkObject client, IPacket packet) {
		queue.add(packet);
	}
	
	public synchronized IPacket pop(NetworkObject client) {
		if (queue.size() > 0) {
			return queue.remove(0);
		}
		return null;
	}
	
	protected void startThread(InputStream input, OutputStream output) {
		Socket sock = this;
		Thread t = new Thread() {
			
			public void run() {
				try {
					KeyGenerator gen = KeyGenerator.getInstance(Registry.CONNECTION_ENCRYPTION_ALGORITHM);
					gen.init(Registry.AES_ENCRYPTION_KEY_LENGTH);

					Cipher ecipher = Cipher.getInstance(Registry.CONNECTION_ENCRYPTION_ALGORITHM);
					Cipher dcipher = Cipher.getInstance(Registry.CONNECTION_ENCRYPTION_ALGORITHM);
					
					RTLock lock = new RTLock(new SecretKeySpec(gen.generateKey().getEncoded(), Registry.CONNECTION_ENCRYPTION_ALGORITHM));
					Thread threadIn = new ReceiverThread(obj, sock, input, dcipher, lock);
					Thread threadOut = new TransmitterThread(obj, sock, output, ecipher, lock);
					threadIn.start();
					threadOut.start();
				} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {}
			}
		};
		t.start();
	}
	
	public Side type() {
		return Side.CLIENT;
	}
	
	public NetworkObject getObj() {
		return obj;
	}
}
