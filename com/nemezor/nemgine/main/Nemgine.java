package com.nemezor.nemgine.main;

import java.awt.EventQueue;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;

import com.nemezor.nemgine.debug.ImmediateRender;
import com.nemezor.nemgine.graphics.DisplayManager;
import com.nemezor.nemgine.graphics.ShaderManager;
import com.nemezor.nemgine.graphics.TextureManager;
import com.nemezor.nemgine.misc.NemgineThreadException;
import com.nemezor.nemgine.misc.Registry;

public class Nemgine {

	private static HashMap<Integer, BindableThread> threadPool = new HashMap<Integer, BindableThread>();
	private static int threadCounter = 0;
	private static boolean isRunning = true;
	private static Runtime runtime = Runtime.getRuntime();

	private Nemgine() {
	};

	public static void start(String[] args, Class<?> application) {
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				Method entry = null;
				Application ann = null;
				for (Method method : application.getDeclaredMethods()) {
					if (method.getAnnotation(Application.class) != null) {
						if (entry != null) {
							System.err.println("[" + Registry.NEMGINE_NAME + "]: Multiple entry points specified!");
							System.exit(Registry.INVALID);
						}
						ann = method.getAnnotation(Application.class);
						entry = method;
					}
				}
				if (entry == null) {
					System.err.println("[" + Registry.NEMGINE_NAME + "]: No entry point specified!");
					System.exit(Registry.INVALID);
				}
				NemgineLoader.initialize(ann.width(), ann.height(), ann.name());
				
				try {
					entry.invoke(application.newInstance());
				} catch (Exception e) {
					System.err.println("[" + Registry.NEMGINE_NAME + "]: Application execution failed!");
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
			TextureManager.disposeAll();
			if (ImmediateRender.isRenderModeImmediate()) {
				ImmediateRender.dispose();
			} else {
				ShaderManager.disposeAll();
				DisplayManager.dispose();
			}
			NemgineLoader.dispose();
			System.exit(exitCode);
		}
	}

	public static boolean threadsRunning() {
		BindableThread[] threads = new BindableThread[threadPool.size()];
		int index = 0;
		Iterator<Integer> keys = threadPool.keySet().iterator();
		while (keys.hasNext()) {
			threads[index] = threadPool.get(keys.next());
			index++;
		}
		boolean repeat = true;
		repeat = false;
		for (BindableThread thread : threads) {
			if (thread.isAlive()) {
				repeat = true;
			}
		}
		return repeat;
	}

	public static synchronized void stopThread(int id) {
		BindableThread thread = threadPool.get(id);
		if (thread == null) {
			return;
		}
		thread.stopThread();
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
			NemgineThreadException e = new NemgineThreadException("Thread is already running!");
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
			NemgineThreadException e = new NemgineThreadException(
					"Thread is already a main render or main tick thread!");
			e.printStackTrace();
			return false;
		}
		if (thread.isRunning()) {
			NemgineThreadException e = new NemgineThreadException("Thread is already running!");
			e.printStackTrace();
			return false;
		}
		thread.aux.add(new Object[] { loop, -1L });
		return true;
	}

	public static synchronized boolean bindRenderLoop(int id, IMainRenderLoop loop) {
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
			NemgineThreadException e = new NemgineThreadException("Thread is already auxiliary thread!");
			e.printStackTrace();
			return false;
		}
		if (thread.isRunning()) {
			NemgineThreadException e = new NemgineThreadException("Thread is already running!");
			e.printStackTrace();
			return false;
		}
		thread.render = loop;
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
			NemgineThreadException e = new NemgineThreadException("Thread is already auxiliary thread!");
			e.printStackTrace();
			return false;
		}
		if (thread.isRunning()) {
			NemgineThreadException e = new NemgineThreadException("Thread is already running!");
			e.printStackTrace();
			return false;
		}
		thread.tick = loop;
		return true;
	}

	protected static synchronized boolean isRunning() {
		return isRunning;
	}

	private static synchronized void setStopping() {
		isRunning = false;
	}

	public static int cpuCores() {
		return runtime.availableProcessors();
	}

	public static long allocatedMemory() {
		return runtime.totalMemory();
	}

	public static long maximumMemory() {
		return runtime.maxMemory();
	}

	public static long freeMemory() {
		return runtime.freeMemory();
	}

	public static long usedMemory() {
		return runtime.totalMemory() - runtime.freeMemory();
	}

	public static void freeUpMemory() {
		runtime.gc();
	}
}
