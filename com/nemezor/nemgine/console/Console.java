package com.nemezor.nemgine.console;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.nemezor.nemgine.main.Nemgine;

public class Console {
	
	public static final PrintStream out = System.out;
	public static final PrintStream err = System.err;
	
	private static Method input = null;
	private static Object obj = null;
	
	private Console() {}
	
	public static void registerInputListener(Object listener) {
		if (input != null) {
			return;
		}
		Method[] methods = listener.getClass().getDeclaredMethods();
		for (Method m : methods) {
			Annotation[] ann = m.getDeclaredAnnotationsByType(Input.class);
			if (ann.length > 0 && m.getParameterCount() == 1 && m.getParameters()[0].getType() == String.class) {
				input = m;
				obj = listener;
				initializeInputStream();
				return;
			}
		}
	}
	
	private static void initializeInputStream() {
		Thread t = new Thread() {
			
			public void run() {
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
					
					String buffer = "";
					while ((buffer = reader.readLine()) != null) {
						input.invoke(obj, buffer);
					}
				} catch (Exception e) {
					if (!Nemgine.isRunning()) {
						initializeInputStream();
					}
				}
			}
		};
		t.setDaemon(true);
		t.start();
	}
}
