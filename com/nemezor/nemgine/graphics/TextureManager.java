package com.nemezor.nemgine.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import com.nemezor.nemgine.misc.Logger;
import com.nemezor.nemgine.misc.NemgineTextureException;
import com.nemezor.nemgine.misc.Registry;

public class TextureManager {

	private static HashMap<Integer, Texture> textures = new HashMap<Integer, Texture>();
	private static int invalidTexture = 0;
	private static int textureCounter = 0;
	private static int currentTexture = 0;
	
	private TextureManager() {}
	
	public static synchronized int generateTextures() {
		textureCounter++;
		textures.put(textureCounter, new Texture(Registry.INVALID, Registry.INVALID, Registry.INVALID));
		return textureCounter;
	}
	
	public static void bindTexture(int id) {
		if (currentTexture == id) {
			return;
		}
		Texture tex = textures.get(id);
		if (tex == null || tex.getId() == Registry.INVALID) {
			if (currentTexture != Registry.INVALID) {
				currentTexture = Registry.INVALID;
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, invalidTexture);
			}
			return;
		}
		currentTexture = id;
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex.getId());
	}
	
	public static void unbindTexture() {
		if (currentTexture != 0) {
			currentTexture = 0;
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		}
	}
	
	public static void dispose(int id) {
		if (currentTexture == id) {
			return;
		}
		Texture tex = textures.get(id);
		if (tex == null) {
			return;
		}
		if (tex.getId() != Registry.INVALID) {
			GL11.glDeleteTextures(tex.getId());
		}
		textures.remove(id);
	}
	
	public static void disposeAll() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		Iterator<Integer> keys = textures.keySet().iterator();
		
		while (keys.hasNext()) {
			Texture tex = textures.get(keys.next());
			if (tex.getId() != Registry.INVALID) {
				GL11.glDeleteTextures(tex.getId());
			}
		}
		textures.clear();
	}
	
	public static boolean initializeTexture(int id, String file) {
		if (currentTexture == id) {
			return false;
		}
		Texture tex = textures.get(id);
		if (tex == null || tex.getId() != Registry.INVALID) {
			return false;
		}
		tex = loadTexture(file);
		if (tex == null) {
			NemgineTextureException ex = new NemgineTextureException(Registry.TEXTURE_MANAGER_LOADER_GLOBAL_ERROR);
			ex.setThrower(Registry.TEXTURE_MANAGER_NAME);
			ex.setTextureInfo(file);
			ex.printStackTrace();
			return false;
		}
		textures.put(id, tex);
		return true;
	}
	
	public static Texture loadTexture(String file) {
		int[] pixels = null;
		int w = Registry.INVALID;
		int h = Registry.INVALID;
		
		try {
			BufferedImage image = ImageIO.read(ClassLoader.getSystemResourceAsStream(file));
			w = image.getWidth();
			h = image.getHeight();
			pixels = new int[w * h];
			image.getRGB(0, 0, w, h, pixels, 0, w);
		} catch (IOException e) {
			NemgineTextureException ex = new NemgineTextureException(Registry.TEXTURE_LOADER_NOT_FOUND);
			ex.setThrower(Registry.TEXTURE_LOADER_NAME);
			ex.setTextureInfo(file);
			ex.printStackTrace();
			e.printStackTrace();
			return null;
		}

		int[] data = new int[w * h];
		for (int i = 0; i < w * h; i++) {
			int alpha = (pixels[i] & 0xFF000000) >> 24;
			int red = (pixels[i] & 0xFF0000) >> 16;
			int green = (pixels[i] & 0xFF00) >> 8;
			int blue = (pixels[i] & 0xFF);

			data[i] = alpha << 24 | blue << 16 | green << 8 | red;
		}

		IntBuffer res = ByteBuffer.allocateDirect(data.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
		res.put(data).flip();

		int id = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, w, h, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, res);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		return new Texture(id, w, h);
	}
	
	public static void loadMissingTexture() {
		int[] pixels = null;
		int w = Registry.INVALID;
		int h = Registry.INVALID;
		
		try {
			BufferedImage image = ImageIO.read(ClassLoader.getSystemResourceAsStream(Registry.TEXTURE_MISSING_PATH));
			w = image.getWidth();
			h = image.getHeight();
			pixels = new int[w * h];
			image.getRGB(0, 0, w, h, pixels, 0, w);
		} catch (IOException e) {
			Logger.log(Registry.NEMGINE_NAME, Registry.TEXTURE_LOADER_MISSING_ERROR);
			System.exit(Registry.INVALID);
		}

		int[] data = new int[w * h];
		for (int i = 0; i < w * h; i++) {
			int alpha = (pixels[i] & 0xFF000000) >> 24;
			int red = (pixels[i] & 0xFF0000) >> 16;
			int green = (pixels[i] & 0xFF00) >> 8;
			int blue = (pixels[i] & 0xFF);

			data[i] = alpha << 24 | blue << 16 | green << 8 | red;
		}

		IntBuffer res = ByteBuffer.allocateDirect(data.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
		res.put(data).flip();

		invalidTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, invalidTexture);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, w, h, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, res);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
}
