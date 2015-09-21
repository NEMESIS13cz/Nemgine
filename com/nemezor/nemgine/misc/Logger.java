package com.nemezor.nemgine.misc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

	private Logger() {}
	
	private static final SimpleDateFormat fileFormat = new SimpleDateFormat(Registry.LOG_FILE_DATE_FORMAT);
	private static final SimpleDateFormat msgFormat = new SimpleDateFormat(Registry.LOG_MESSAGE_DATE_FORMAT);
	
	private static boolean initialized = false;
	private static FileWriter writer;
	private static String lastDate;
	private static int fileNum = 2;
	private static String appFile;
	
	public static void initialize(String appFilePath) {
		appFile = appFilePath;
		String path = appFile + Registry.LOG_FILE_PATH;
		File dir = new File(path);
		String curr = getCurrentLogName();
		lastDate = getCurrentLogName();
		File file = new File(path + "/" + curr + ".1" + Registry.LOG_FILE_FORMAT);
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
				file = new File(path + "/" + curr + "." + fileNum + Registry.LOG_FILE_FORMAT);
				fileNum++;
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
	
	public static void close() {
		if (initialized) {
			try {
				writer.close();
			} catch (IOException e) {}
		}
	}
	
	public static synchronized void log(String sender, String msg) {
		String time = getCurrentMessageTime();
		String message = "|" + time + "|" + (sender == null ? " >>> " : " [" + sender + "]: ") + msg;
		System.out.println(message);
		if (initialized) {
			String curr = getCurrentLogName();
			if (!lastDate.equals(curr)) {
				initialized = false;
				initialize(appFile);
			}
			try {
				if (initialized) {
					writer.append(message + "\n");
				}
			} catch (IOException e) {}
		}
	}
	
	private static String getCurrentMessageTime() {
		return msgFormat.format(new Date());
	}
	
	private static String getCurrentLogName() {
		return fileFormat.format(new Date());
	}
}
