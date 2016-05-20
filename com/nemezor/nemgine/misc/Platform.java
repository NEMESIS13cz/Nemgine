package com.nemezor.nemgine.misc;

import java.awt.GraphicsEnvironment;
import java.lang.management.ManagementFactory;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;

import com.nemezor.nemgine.exceptions.WindowException;
import com.nemezor.nemgine.main.Nemgine;
import com.sun.management.OperatingSystemMXBean;

public class Platform {
	
	private static String javaVersion;
	private static String javaVendor;
	private static String operatingSystem;
	private static String architecture;
	private static String systemVersion;
	private static String openGLVendor;
	private static String openGLRenderer;
	private static String[] openGLExtensions;
	private static int openGLTextureSize = Registry.INVALID;
	private static GLVersion openGLVersion = new GLVersion(Registry.INVALID, Registry.INVALID);
	private static GLVersion GLSLVersion = new GLVersion(Registry.INVALID, Registry.INVALID);
	private static String[] availableFonts;
	private static volatile int cpuCores;
	private static volatile long freeMem;
	private static volatile long usedMem;
	private static volatile long allocMem;
	private static volatile long maxMem;
	private static volatile long totalPhysicalMemory;
	private static volatile long freePhysicalMemory;
	private static volatile long usedPhysicalMemory;
	private static volatile long totalSwapMemory;
	private static volatile long freeSwapMemory;
	private static volatile long usedSwapMemory;
	private static volatile double cpuUsage;
	
	private static Runtime runtime = Runtime.getRuntime();
	private static OperatingSystemMXBean sys;
	private static int memRefreshCounter;
	
	private static boolean initialized = false;
	
	private Platform() {}
	
	public static void initialize(boolean headless) {
		if (initialized) {
			return;
		}
		javaVersion = System.getProperty("java.version");
		javaVendor = System.getProperty("java.vendor");
		operatingSystem = System.getProperty("os.name");
		architecture = System.getProperty("os.arch");
		systemVersion = System.getProperty("os.version");

		sys = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
		totalPhysicalMemory = sys.getTotalPhysicalMemorySize();
		
		Thread t = new Thread() {
			
			public void run() {
				setName(Registry.PLATFORM_NAME);
				while (Nemgine.isRunning()) {
					refreshCPUAndMemory();
					try {
						Thread.sleep(Registry.PLATFORM_RESOURCES_POLL);
					} catch (InterruptedException e) {}
				}
			}
		};
		t.start();
		
		if (headless) {
			return;
		}
		setDefaultGLFWWindowConfigurations();
		long window = GLFW.glfwCreateWindow(1, 1, Registry.NEMGINE_NAME, MemoryUtil.NULL, MemoryUtil.NULL);
		if (window == MemoryUtil.NULL) {
			WindowException e = new WindowException(Registry.WINDOW_EXCEPTION_PLATFORM_DATA_EXTRACTION);
			e.printStackTrace();
			String stack = "";
			for (StackTraceElement el : e.getStackTrace()) {
				stack += el.toString() + "\n";
			}
			ErrorScreen.show(Registry.WINDOW_EXCEPTION_PLATFORM_DATA_EXTRACTION + "\n\n" + e.getLocalizedMessage() + "\n" + stack, true);
		}
		GLFW.glfwMakeContextCurrent(window);
		GL.createCapabilities(Registry.OPENGL_FORWARD_COMPATIBLE);
		
		openGLVendor = GL11.glGetString(GL11.GL_VENDOR);
		openGLRenderer = GL11.glGetString(GL11.GL_RENDERER);
		String[] SLVerTemp = GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION).split("\\.");
		String[] GLVerTemp = GL11.glGetString(GL11.GL_VERSION).split("\\.");
		String extTemp = GL11.glGetString(GL11.GL_EXTENSIONS);
		openGLTextureSize = GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);
		
		if (extTemp != null) {
			openGLExtensions = extTemp.split(" ");
		}else{
			openGLExtensions = new String[0];
		}
		try{
			openGLVersion = new GLVersion(Integer.parseInt(GLVerTemp[0]), Integer.parseInt(GLVerTemp[1]));
		} catch (NumberFormatException e) {}
		try{
			GLSLVersion = new GLVersion(Integer.parseInt(SLVerTemp[0]), Integer.parseInt(SLVerTemp[1]));
		} catch (NumberFormatException e) {}

		GLFW.glfwDestroyWindow(window);
		
		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
		availableFonts = e.getAvailableFontFamilyNames();
	}
	
	private static void refreshCPUAndMemory() {
		if (memRefreshCounter == 4) {
			memRefreshCounter = 0;
			
			cpuCores = runtime.availableProcessors();
			freeMem = runtime.freeMemory();
			maxMem = runtime.maxMemory();
			allocMem = runtime.totalMemory();
			usedMem = allocMem - freeMem;
			freePhysicalMemory = sys.getFreePhysicalMemorySize();
			usedPhysicalMemory = totalPhysicalMemory - freePhysicalMemory;
			totalSwapMemory = sys.getTotalSwapSpaceSize();
			freeSwapMemory = sys.getFreeSwapSpaceSize();
			usedSwapMemory = totalSwapMemory - freeSwapMemory;
		}
		cpuUsage = sys.getSystemCpuLoad();
		
		memRefreshCounter++;
	}
	
	public static GLVersion getOpenGLVersion() {
		return openGLVersion.clone();
	}
	
	public static GLVersion getOpenGLShadingLanguageVersion() {
		return GLSLVersion.clone();
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
		return cpuCores;
	}

	public static long getAllocatedMemory() {
		return allocMem;
	}

	public static long getMaximumMemory() {
		return maxMem;
	}

	public static long getFreeMemory() {
		return freeMem;
	}

	public static long getUsedMemory() {
		return usedMem;
	}

	public static void freeUpMemory() {
		runtime.gc();
	}
	
	public static long getTotalPhysicalMemory() {
		return totalPhysicalMemory;
	}
	
	public static long getFreePhysicalMemory() {
		return freePhysicalMemory;
	}
	
	public static long getUsedPhysicalMemory() {
		return usedPhysicalMemory;
	}
	
	public static long getTotalSwapMemory() {
		return totalSwapMemory;
	}
	
	public static long getFreeSwapMemory() {
		return freeSwapMemory;
	}
	
	public static long getUsedSwapMemory() {
		return usedSwapMemory;
	}
	
	public static double getCPUUsage() {
		return cpuUsage;
	}
	
	public static void setDefaultGLFWWindowConfigurations() {
		if (Nemgine.getSide().isServer()) {
			return;
		}
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_DOUBLE_BUFFER, GLFW.GLFW_TRUE);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, Registry.SUPPORTED_OPENGL_VERSION.getMajorVersion());
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, Registry.SUPPORTED_OPENGL_VERSION.getMinorVersion());
	}
	
	public static boolean isFontAvailable(String name) {
		for (String s : availableFonts) {
			if (s.equals(name)) {
				return true;
			}
		}
		return false;
	}
}
