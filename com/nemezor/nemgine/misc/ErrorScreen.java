package com.nemezor.nemgine.misc;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import com.nemezor.nemgine.main.Nemgine;

public class ErrorScreen {

	private static boolean exit = false;
	
	public static void show(String message, boolean shouldExit) { //TODO fix - JVM error
		if (Nemgine.getSide() == Side.SERVER) {
			if (!shouldExit) {
				return;
			}
			Nemgine.shutDown();
			Nemgine.exit(Registry.INVALID);
		}
		JFrame frame = new JFrame();
		
		WindowListener action = new WindowListener() {
			
			public void windowOpened(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {
				frame.dispose();
				if (shouldExit) {
					exit = true;
				}
			}
			public void windowClosed(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
		};
		
		JTextArea text = new JTextArea(Registry.NEMGINE_EXCEPTION_SHUTDOWN + "\n\n>>> " + message + "\n\n\n\n" + Registry.NEMGINE_SHUTDOWN_EXIT + Registry.INVALID + "\n\n\n" + (Logger.isContained() ? "" : Registry.NEMGINE_EXCEPTION_SHUTDOWN_MORE));
		text.setEditable(false);
		
		frame.setResizable(false);
		frame.getContentPane().add(text);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(action);
		frame.pack();
		frame.setSize(new Dimension(Registry.ERROR_SCREEN_WIDTH, Registry.ERROR_SCREEN_HEIGHT));
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		if (!shouldExit) {
			return;
		}
		
		while (!exit) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		Nemgine.shutDown();
		Nemgine.exit(Registry.INVALID);
	}
	
}
