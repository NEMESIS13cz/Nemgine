package com.nemezor.nemgine.file;

public class Directory {

	private static String pathSeparator;
	private static String temporaryDir;
	private static String userHomeDir;
	private static String currentDir;
	
	private static boolean init = false;
	
	private Directory() {}
	
	public static void initialize() {
		if (init) {
			return;
		}
		pathSeparator = System.getProperty("file.separator");
		temporaryDir = System.getProperty("java.io.tmpdir");
		userHomeDir = System.getProperty("user.home");
		currentDir = System.getProperty("user.dir");
		
		init = true;
	}
	
	public static String getPathSeparator() {
		return pathSeparator;
	}
	
	public static String getHomeDirectory() {
		return userHomeDir;
	}
	
	public static String getTemporaryDirectory() {
		return temporaryDir;
	}
	
	public static String getCurrentWorkingDirectory() {
		return currentDir;
	}
}
