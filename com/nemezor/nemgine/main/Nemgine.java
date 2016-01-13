package com.nemezor.nemgine.main;

import java.awt.EventQueue;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;

import com.nemezor.nemgine.exceptions.ThreadException;
import com.nemezor.nemgine.graphics.DisplayManager;
import com.nemezor.nemgine.graphics.FrameBufferManager;
import com.nemezor.nemgine.graphics.ModelManager;
import com.nemezor.nemgine.graphics.ShaderManager;
import com.nemezor.nemgine.graphics.TextureManager;
import com.nemezor.nemgine.misc.InputParams;
import com.nemezor.nemgine.misc.Logger;
import com.nemezor.nemgine.misc.Platform;
import com.nemezor.nemgine.misc.Registry;
import com.nemezor.nemgine.misc.Side;
import com.nemezor.nemgine.network.NetworkManager;

public class Nemgine {

	private static HashMap<Integer, BindableThread> threadPool = new HashMap<Integer, BindableThread>();
	private static int threadCounter = 0;
	private static boolean isRunning = true;
	private static boolean headless = false;
	private static boolean compat = false;

	protected static boolean hasRenderThread = false;
	protected static int w, h;
	protected static String name;
	
	private Nemgine() {}

	public static void start(String[] args, Class<?> application) {
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				Method entry = null;
				Application ann = null;
				for (Method method : application.getDeclaredMethods()) {
					if (method.getAnnotation(Application.class) != null) {
						if (entry != null) {
							Logger.log(Registry.NEMGINE_NAME, Registry.NEMGINE_RESOLVE_MULTIPLE, false);
							System.exit(Registry.INVALID);
						}
						ann = method.getAnnotation(Application.class);
						entry = method;
					}
				}
				if (entry == null) {
					Logger.log(Registry.NEMGINE_NAME, Registry.NEMGINE_RESOLVE_NONE, false);
					System.exit(Registry.INVALID);
				}
				
				InputParams.resolve(args);
				w = InputParams.containsEntry("width") ? InputParams.getInteger("width") : ann.width();
				h = InputParams.containsEntry("height") ? InputParams.getInteger("height") : ann.height();
				boolean contained = InputParams.containsEntry("contained") ? InputParams.getBoolean("contained") : ann.contained();
				headless = InputParams.containsEntry("server") ? InputParams.getBoolean("server") : ann.side() == Side.SERVER;
				name = ann.name();
				compat = InputParams.containsEntry("compat") ? InputParams.getBoolean("compat") : ann.compatibilityMode();
				
				if (!contained) {
					Logger.initialize(ann.path().endsWith("/") ? ann.path().substring(0, ann.path().length() - 1) : ann.path());
				}
				Platform.initialize(headless);
				
				try {
					entry.invoke(application.newInstance());
				} catch (Exception e) {
					Logger.log(Registry.NEMGINE_NAME, Registry.NEMGINE_EXECUTION_FAIL, false);
					e.printStackTrace();
					System.exit(Registry.INVALID);
				}
			}
		});
	}

	public static void shutDown() {
		setStopping();
	}

	public static void exit(int exitCode) {
		if (!isRunning()) {
			Logger.log(Registry.NEMGINE_NAME, Registry.NEMGINE_SHUTDOWN_DISPOSE, false);
			NetworkManager.disposeAll();
			if (Nemgine.getSide() == Side.CLIENT) {
				TextureManager.disposeAll();
				ShaderManager.disposeAll();
				ModelManager.disposeAll();
				FrameBufferManager.disposeAll();
				DisplayManager.dispose();
			}
			Logger.log(Registry.NEMGINE_NAME, Registry.NEMGINE_SHUTDOWN_EXIT + exitCode, false);
			Logger.close();
			System.gc();
			System.exit(exitCode);
		}
	}

	public static boolean threadsRunning() {
		Iterator<Integer> keys = threadPool.keySet().iterator();
		while (keys.hasNext()) {
			if (threadPool.get(keys.next()).isAlive()) {
				return true;
			}
		}
		return false;
	}

	public static synchronized void stopThread(int id) {
		BindableThread thread = threadPool.get(id);
		if (thread == null) {
			return;
		}
		thread.stopThread();
		threadPool.remove(id);
	}

	public static synchronized int generateThreads(String threadName, boolean prefix) {
		BindableThread thread = new BindableThread();
		threadCounter++;
		thread.setName((prefix ? ("#" + threadCounter + " ") : "") + threadName);
		threadPool.put(threadCounter, thread);
		return threadCounter;
	}

	public static synchronized boolean startThread(int id) {
		BindableThread thread = threadPool.get(id);
		if (thread == null) {
			return false;
		}
		if (thread.isRunning()) {
			ThreadException e = new ThreadException("Thread is already running!");
			e.printStackTrace();
			return false;
		}
		thread.start();
		return true;
	}

	public static synchronized boolean bindAuxLoop(int id, IAuxLoop loop) {
		BindableThread thread = threadPool.get(id);
		if (thread == null) {
			return false;
		}
		if (!(loop instanceof IAuxLoop)) {
			return false;
		}
		if (thread.render != null || thread.tick != null) {
			ThreadException e = new ThreadException("Thread is already a main render or main tick thread!");
			e.printStackTrace();
			return false;
		}
		if (thread.isRunning()) {
			ThreadException e = new ThreadException("Thread is already running!");
			e.printStackTrace();
			return false;
		}
		thread.aux.add(new Object[] {loop, -1L});
		return true;
	}

	public static synchronized boolean bindRenderLoop(int id, IMainRenderLoop loop) {
		if (Nemgine.getSide() == Side.SERVER || hasRenderThread) {
			return false;
		}
		BindableThread thread = threadPool.get(id);
		if (thread == null) {
			return false;
		}
		if (thread.render != null) {
			return false;
		}
		if (!(loop instanceof IMainRenderLoop)) {
			return false;
		}
		if (thread.aux.size() > 0) {
			ThreadException e = new ThreadException("Thread is already auxiliary thread!");
			e.printStackTrace();
			return false;
		}
		if (thread.isRunning()) {
			ThreadException e = new ThreadException("Thread is already running!");
			e.printStackTrace();
			return false;
		}
		thread.render = loop;
		hasRenderThread = true;
		return true;
	}

	public static synchronized boolean bindTickLoop(int id, IMainTickLoop loop) {
		BindableThread thread = threadPool.get(id);
		if (thread == null) {
			return false;
		}
		if (thread.tick != null) {
			return false;
		}
		if (!(loop instanceof IMainTickLoop)) {
			return false;
		}
		if (thread.aux.size() > 0) {
			ThreadException e = new ThreadException("Thread is already auxiliary thread!");
			e.printStackTrace();
			return false;
		}
		if (thread.isRunning()) {
			ThreadException e = new ThreadException("Thread is already running!");
			e.printStackTrace();
			return false;
		}
		thread.tick = loop;
		return true;
	}

	public static synchronized boolean isRunning() {
		return isRunning;
	}

	private static synchronized void setStopping() {
		isRunning = false;
	}
	
	public static Side getSide() {
		return headless ? Side.SERVER : Side.CLIENT;
	}
	
	public static boolean isInCompatibilityMode() {
		return compat;
	}
}
