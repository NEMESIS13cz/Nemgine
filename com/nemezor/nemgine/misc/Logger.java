package com.nemezor.nemgine.misc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.nemezor.nemgine.console.Console;
import com.nemezor.nemgine.file.Directory;

public class Logger {
	
	private static final SimpleDateFormat fileFormat = new SimpleDateFormat(Registry.LOG_FILE_DATE_FORMAT);
	private static final SimpleDateFormat msgFormat = new SimpleDateFormat(Registry.LOG_MESSAGE_DATE_FORMAT);
	
	private static boolean initialized = false;
	private static FileWriter writer;
	private static String lastDate;
	private static int fileNum = 1;
	private static String appFile;
	private static String lineSeparator;
	
	private Logger() {}
	
	public static void initialize(String appFilePath) {
		lineSeparator = System.getProperty("line.separator");
		appFile = appFilePath;
		String path = appFile + Registry.LOG_FILE_PATH;
		File dir = new File(path);
		String curr = getCurrentLogName();
		lastDate = getCurrentLogName();
		File file = new File(path + Directory.getPathSeparator() + curr + "." + fileNum + Registry.LOG_FILE_FORMAT);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				return;
			}
		}else{
			while (file.exists()) {
				fileNum++;
				file = new File(path + Directory.getPathSeparator() + curr + "." + fileNum + Registry.LOG_FILE_FORMAT);
			}
			try {
				file.createNewFile();
			} catch (IOException e) {}
		}
		
		try {
			writer = new FileWriter(file);
		} catch (IOException e) {
			return;
		}
		initialized = true;
	}
	
	public static boolean isContained() {
		return !initialized;
	}
	
	public static void close() {
		if (initialized) {
			try {
				writer.close();
			} catch (IOException e) {}
		}
	}
	
	public static synchronized void logConsoleOnly(String msg) {
		String time = getCurrentMessageTime();
		String sender = Thread.currentThread().getName();
		String message = "|" + time + "|" + (sender == null ? " >>> " : " [" + sender + "]: ") + msg;
		Console.out.println(message);
	}
	
	public static void logSilently(String msg) {
		log(Thread.currentThread().getName(), msg, true);
	}
	
	public static void log(String msg) {
		log(Thread.currentThread().getName(), msg, false);
	}
	
	public static void log(String sender, String msg) {
		log(sender, msg, false);
	}
	
	public static synchronized void log(String sender, String msg, boolean silent) {
		String time = getCurrentMessageTime();
		String message = "|" + time + "|" + (sender == null ? " >>> " : " [" + sender + "]: ") + msg;
		if (!silent) {
			Console.out.println(message);
		}
		if (initialized) {
			String curr = getCurrentLogName();
			if (!lastDate.equals(curr)) {
				initialized = false;
				initialize(appFile);
			}
			try {
				if (initialized) {
					writer.append(message + System.lineSeparator());
				}
			} catch (IOException e) {}
		}
	}
	
	public static String getLineSeparator() {
		return lineSeparator;
	}
	
	private static String getCurrentMessageTime() {
		return msgFormat.format(new Date());
	}
	
	private static String getCurrentLogName() {
		return fileFormat.format(new Date());
	}
}
