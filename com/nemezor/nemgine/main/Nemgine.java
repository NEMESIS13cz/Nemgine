package com.nemezor.nemgine.main;

import java.awt.EventQueue;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;

import com.nemezor.nemgine.console.Console;
import com.nemezor.nemgine.exceptions.ThreadException;
import com.nemezor.nemgine.graphics.DisplayManager;
import com.nemezor.nemgine.graphics.FrameBufferManager;
import com.nemezor.nemgine.graphics.Loader;
import com.nemezor.nemgine.graphics.ModelManager;
import com.nemezor.nemgine.graphics.ShaderManager;
import com.nemezor.nemgine.graphics.TextureManager;
import com.nemezor.nemgine.graphics.util.GLResourceEvent;
import com.nemezor.nemgine.graphics.util.OpenGLResources;
import com.nemezor.nemgine.misc.InputParams;
import com.nemezor.nemgine.misc.Logger;
import com.nemezor.nemgine.misc.Platform;
import com.nemezor.nemgine.misc.Registry;
import com.nemezor.nemgine.misc.Side;
import com.nemezor.nemgine.network.NetworkManager;

public class Nemgine {

	private static HashMap<Integer, BindableThread> threadPool = new HashMap<Integer, BindableThread>();
	private static int threadCounter = 0;
	private static boolean isRunning = false;
	private static boolean headless = false;
	private static boolean compat = false;
	private static long context = 0;
	private static GLFWErrorCallback errCb = null;
	
	protected static boolean hasRenderThread = false;
	protected static String name;
	
	private Nemgine() {}

	public static void start(String[] args, Class<?> application) {
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				isRunning = true;
				Method entry = null;
				Method resources = null;
				Application ann = null;
				
				for (Method method : application.getDeclaredMethods()) {
					if (method.getAnnotation(Application.class) != null) {
						if (entry != null) {
							Logger.log(Registry.NEMGINE_NAME, Registry.NEMGINE_RESOLVE_MULTIPLE, false);
							System.exit(Registry.INVALID);
						}
						ann = method.getAnnotation(Application.class);
						entry = method;
					}else if (method.getAnnotation(OpenGLResources.class) != null && method.getParameterCount() == 1 && method.getParameterTypes()[0] == GLResourceEvent.class) {
						if (resources != null) {
							Logger.log(Registry.NEMGINE_NAME, Registry.NEMGINE_RESOLVE_MULTIPLE, false);
							System.exit(Registry.INVALID);
						}
						resources = method;
					}
				}
				
				InputParams.resolve(args);
				boolean contained = InputParams.containsEntry(Registry.PARAM_CONTAINED) ? InputParams.getBoolean(Registry.PARAM_CONTAINED) : ann.contained();
				headless = InputParams.containsEntry(Registry.PARAM_SERVER) ? InputParams.getBoolean(Registry.PARAM_SERVER) : ann.side().isServer();
				name = ann.name();
				compat = InputParams.containsEntry(Registry.PARAM_COMPAT) ? InputParams.getBoolean(Registry.PARAM_COMPAT) : ann.compatibilityMode();
				
				if (entry == null || (resources == null && !headless)) {
					Logger.log(Registry.NEMGINE_NAME, Registry.NEMGINE_RESOLVE_NONE, false);
					System.exit(Registry.INVALID);
				}
				if (!contained) {
					Logger.initialize(ann.path().endsWith("/") ? ann.path().substring(0, ann.path().length() - 1) : ann.path());
				}
				
				Object instance = null;
				try {
					instance = application.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					Logger.log(Registry.NEMGINE_NAME, Registry.NEMGINE_FAILED_TO_INSTANTIATE_APPLICATION, false);
					System.exit(Registry.INVALID);
				}
				if (!headless) {
					if (GLFW.glfwInit() == GLFW.GLFW_FALSE) {
						Logger.log(Registry.NEMGINE_NAME, Registry.NEMGINE_FAILED_TO_INITIALIZE_GLFW, false);
			            GLFW.glfwTerminate();
						System.exit(Registry.INVALID);
					}
					errCb = GLFW.glfwSetErrorCallback(GLFWErrorCallback.createPrint(Console.err));
				}
				
				Platform.initialize(headless);
				
				if (!headless) {
					context = Loader.initialize(name);
					try {
						resources.invoke(instance, GLResourceEvent.GENERATE_IDS);
					} catch (Exception e) {
						Logger.log(Registry.NEMGINE_NAME, Registry.LOADING_RESOURCES_GENERATE_IDS_FAILED, false);
						e.printStackTrace();
			            GLFW.glfwTerminate();
						System.exit(Registry.INVALID);
					}
					Loader.loadDefaultResources();
					try {
						resources.invoke(instance, GLResourceEvent.LOAD_RESOURCES);
					} catch (Exception e) {
						Logger.log(Registry.NEMGINE_NAME, Registry.LOADING_RESOURCES_LOAD_FAILED, false);
						e.printStackTrace();
			            GLFW.glfwTerminate();
						System.exit(Registry.INVALID);
					}
					Loader.finish();
					GL11.glFinish();
				}
				
				try {
					entry.invoke(instance);
				} catch (Exception e) {
					Logger.log(Registry.NEMGINE_NAME, Registry.NEMGINE_EXECUTION_FAIL, false);
					e.printStackTrace();
					if (!headless) {
						GLFW.glfwTerminate();
					}
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
			if (errCb != null) {
				errCb.release();
			}
			if (Nemgine.getSide() == Side.CLIENT) {
				TextureManager.disposeAll();
				ShaderManager.disposeAll();
				ModelManager.disposeAll();
				FrameBufferManager.disposeAll();
				DisplayManager.disposeAll();
				GLFW.glfwTerminate();
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
			ThreadException e = new ThreadException(Registry.THREAD_ALREADY_RUNNING);
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
			ThreadException e = new ThreadException(Registry.THREAD_ALREADY_MAIN);
			e.printStackTrace();
			return false;
		}
		if (thread.isRunning()) {
			ThreadException e = new ThreadException(Registry.THREAD_ALREADY_RUNNING);
			e.printStackTrace();
			return false;
		}
		thread.aux.add(new Object[] {loop, Registry.INVALID});
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
			ThreadException e = new ThreadException(Registry.THREAD_ALREADY_AUXILIARY);
			e.printStackTrace();
			return false;
		}
		if (thread.isRunning()) {
			ThreadException e = new ThreadException(Registry.THREAD_ALREADY_RUNNING);
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
			ThreadException e = new ThreadException(Registry.THREAD_ALREADY_AUXILIARY);
			e.printStackTrace();
			return false;
		}
		if (thread.isRunning()) {
			ThreadException e = new ThreadException(Registry.THREAD_ALREADY_RUNNING);
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
	
	public static String getApplicationName() {
		return name;
	}
	
	public static long getOpenGLContext() {
		return context;
	}
}
