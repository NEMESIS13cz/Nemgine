package com.nemezor.nemgine.misc;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.Pbuffer;
import org.lwjgl.opengl.PixelFormat;

public class Platform {
	
	private static String javaVersion;
	private static String javaVendor;
	private static String operatingSystem;
	private static String architecture;
	private static String systemVersion;
	private static String openGLVendor;
	private static String openGLRenderer;
	private static String[] openGLExtensions;
	private static int openGLVersion = Registry.INVALID;
	private static int openGLTextureSize = Registry.INVALID;
	
	private static Runtime runtime = Runtime.getRuntime();
	
	private static boolean initialized = false;
	
	public static void initialize(boolean headless) {
		if (initialized) {
			return;
		}
		javaVersion = System.getProperty("java.version");
		javaVendor = System.getProperty("java.vendor");
		operatingSystem = System.getProperty("os.name");
		architecture = System.getProperty("os.arch");
		systemVersion = System.getProperty("os.version");
		
		if (headless) {
			return;
		}
		try {
			Pbuffer gl = new Pbuffer(1, 1, new PixelFormat(), null);
			gl.makeCurrent();
			
			openGLVersion = GL30.glGetInteger(GL30.GL_MAJOR_VERSION, 0);
			openGLVendor = GL11.glGetString(GL11.GL_VENDOR);
			openGLRenderer = GL11.glGetString(GL11.GL_RENDERER);
			openGLExtensions = GL11.glGetString(GL11.GL_EXTENSIONS).split(" ");
			openGLTextureSize = GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);
			
			gl.destroy();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	public static int getOpenGLVersion() {
		return openGLVersion;
	}

	public static String getOpenGLVendor() {
		return openGLVendor;
	}

	public static String getOpenGLRenderer() {
		return openGLRenderer;
	}

	public static String[] getOpenGLExtensions() {
		return openGLExtensions.clone();
	}
	
	public static int getOpenGLTextureSize() {
		return openGLTextureSize;
	}
	
	public static String getOperatingSystem() {
		return operatingSystem;
	}

	public static String getArchitecture() {
		return architecture;
	}

	public static String getSystemVersion() {
		return systemVersion;
	}

	public static String getJavaVersion() {
		return javaVersion;
	}

	public static String getJavaVendor() {
		return javaVendor;
	}

	public static int getCpuCores() {
		return runtime.availableProcessors();
	}

	public static long getAllocatedMemory() {
		return runtime.totalMemory();
	}

	public static long getMaximumMemory() {
		return runtime.maxMemory();
	}

	public static long getFreeMemory() {
		return runtime.freeMemory();
	}

	public static long getUsedMemory() {
		return runtime.totalMemory() - runtime.freeMemory();
	}

	public static void freeUpMemory() {
		runtime.gc();
	}
}
