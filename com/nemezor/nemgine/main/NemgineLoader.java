package com.nemezor.nemgine.main;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;

import com.nemezor.nemgine.misc.LoaderPanel;
import com.nemezor.nemgine.misc.Registry;

public class NemgineLoader {

	private static String currentState;
	private static JFrame frame = new JFrame();
	private static LoaderPanel panel;
	private static JFrame displayFrame;
	private static int w;
	private static int h;
	private static boolean isCloseRequested = false;
	private static volatile boolean isLoading = true;
	private static int appW;
	private static int appH;
	private static String appTitle;

	protected static synchronized void initialize(int appWidth, int appHeight, String title) {
		w = Registry.LOADING_SCREEN_WIDTH;
		h = Registry.LOADING_SCREEN_HEIGHT;
		appW = appWidth;
		appH = appHeight;
		appTitle = title;
		
		panel = new LoaderPanel(w, h);
		
		frame.getContentPane().add(panel);

		WindowListener closeListener = new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				setClosing();
			}
		};

		frame.addWindowListener(closeListener);

		frame.setResizable(false);
		frame.setUndecorated(true);
		frame.setTitle(title);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.pack();
		frame.setSize(new Dimension(w, h));
		frame.setLocationRelativeTo(null);

		Thread t = new Thread() {

			public void run() {
				while (isLoading) {

					panel.render(currentState);

					try {
						sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};

		frame.setVisible(true);

		panel.postinitialize();
		t.setDaemon(true);
		t.start();
	}

	public static void initializeOpenGL(boolean imm, ContextAttribs a) {
		Canvas c = new Canvas();
		displayFrame = new JFrame();
		displayFrame.getContentPane().add(c);
		
		WindowListener closeListener = new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				setClosing();
			}
		};
		displayFrame.addWindowListener(closeListener);
		displayFrame.setTitle(appTitle);
		displayFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		displayFrame.pack();
		displayFrame.setSize(new Dimension(appW, appH));
		displayFrame.setLocationRelativeTo(null);

		System.setProperty("org.lwjgl.util.NoChecks", "true");
		try {
			Display.setParent(c);
			if (imm) {
				Display.create();
			}else{
				Display.create(new PixelFormat(), a);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		System.setProperty("org.lwjgl.util.NoChecks", "false");
	}
	
	public static Dimension getSize() {
		return new Dimension(appW, appH);
	}

	public static Point getLocation() {
		frame.setSize(getSize());
		frame.setLocationRelativeTo(null);
		return frame.getLocation();
	}

	public static String getTitle() {
		return appTitle;
	}

	public static synchronized void stop() {
		isLoading = false;
		panel.switchToOpenGL();
		frame.dispose();
		displayFrame.setVisible(true);
	}

	public static synchronized void dispose() {
		displayFrame.setVisible(false);
		displayFrame.dispose();
	}

	public static synchronized void updateState(String state) {
		currentState = state;
	}

	public static synchronized boolean isCloseRequested() {
		return isCloseRequested;
	}

	private static synchronized void setClosing() {
		isCloseRequested = true;
	}
}
