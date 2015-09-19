package com.nemezor.nemgine.misc;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.nemezor.nemgine.main.Nemgine;

public class LoaderPanel extends Canvas {

	private static final long serialVersionUID = 1L;
	
	private BufferedImage displayImage;
	private BufferStrategy bufferStrategy;
	private Graphics gfx;
	private Graphics2D image;
	private BufferedImage logo = null;
	private int loadIndicator = 0;
	private long lastIndicatorUpdate = 0;
	private volatile boolean isOpenGL = false;
	
	public LoaderPanel(int width, int height) {
		displayImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		try {
			logo = ImageIO.read(ClassLoader.getSystemResource(Registry.LOGO_IMAGE_PATH));
		} catch (IOException e) {
			System.err.println("[" + Registry.NEMGINE_NAME + "]: Failed to load logo image!");
			e.printStackTrace();
			Nemgine.shutDown();
		}
	}
	
	public void postinitialize() {
		createBufferStrategy(1);
		bufferStrategy = getBufferStrategy();
		gfx = bufferStrategy.getDrawGraphics();
		image = (Graphics2D)displayImage.getGraphics();
		image.setFont(new Font(Registry.LOADING_SCREEN_FONT, Registry.LOADING_SCREEN_FONT_PROPERTIES, Registry.LOADING_SCREEN_FONT_SIZE));
		lastIndicatorUpdate = System.currentTimeMillis();
	}
	
	private void drawCenteredString(String s, int x, int y) {
		FontMetrics fm = image.getFontMetrics();
		x = x - (fm.stringWidth(s)) / 2;
		image.drawString(s, x, y);
	}
	
	public void render(String state) {
		if (!isOpenGL) {
			if (lastIndicatorUpdate < System.currentTimeMillis()) {
				lastIndicatorUpdate = System.currentTimeMillis() + (Registry.ONE_SECOND_IN_MILLIS / Registry.LOADING_SCREEN_INDICATOR_REFRESH);
				loadIndicator++;
				if (loadIndicator > Registry.LOADING_SCREEN_INDICATOR_LENGTH) {
					loadIndicator = 0;
				}
			}
			image.clearRect(0, 0, displayImage.getWidth(), displayImage.getHeight());
			if (logo != null) {
				image.drawImage(logo, 0, 0, displayImage.getWidth(), displayImage.getHeight(), null);
			}
			image.setColor(new Color(Registry.LOADING_SCREEN_FONT_COLOR));
			if (state != null) {
				drawCenteredString(state, Registry.LOADING_SCREEN_FONT_X, Registry.LOADING_SCREEN_FONT_Y);
			}
			drawIndicator();
			gfx.drawImage(displayImage, 0, 0, displayImage.getWidth(), displayImage.getHeight(), null);
			bufferStrategy.show();
		}
	}
	
	private void drawIndicator() {
		int x = Registry.LOADING_SCREEN_INDICATOR_X - (Registry.LOADING_SCREEN_INDICATOR_LENGTH * Registry.LOADING_SCREEN_INDICATOR_SIZE + Registry.LOADING_SCREEN_INDICATOR_PADDING) / 2;
		int y = Registry.LOADING_SCREEN_INDICATOR_Y;
		
		for (int i = 0; i < loadIndicator; i++) {
			image.drawOval(x, y, Registry.LOADING_SCREEN_INDICATOR_SIZE, Registry.LOADING_SCREEN_INDICATOR_SIZE);
			x += Registry.LOADING_SCREEN_INDICATOR_SIZE + Registry.LOADING_SCREEN_INDICATOR_PADDING;
		}
	}
	
	public void switchToOpenGL() {
		isOpenGL = true;
		bufferStrategy.dispose();
		gfx.dispose();
		image.dispose();
	}
}
