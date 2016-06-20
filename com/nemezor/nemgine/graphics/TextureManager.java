package com.nemezor.nemgine.graphics;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.nemezor.nemgine.exceptions.TextureException;
import com.nemezor.nemgine.graphics.util.Texture;
import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.misc.Logger;
import com.nemezor.nemgine.misc.Registry;
import com.nemezor.nemgine.misc.Side;

public class TextureManager {

	private static HashMap<Integer, Texture> textures = new HashMap<Integer, Texture>();
	private static int textureCounter = 0;
	private static int currentTexture = 0;
	private static int invalidTexture = 0;
	
	private TextureManager() {}
	
	public static synchronized int generateTextures() {
		if (Nemgine.getSide() == Side.SERVER) {
			return Registry.INVALID;
		}
		textureCounter++;
		textures.put(textureCounter, new Texture(Registry.INVALID, Registry.INVALID, Registry.INVALID));
		if (Loader.loading()) {
			Loader.textureCounter++;
		}
		return textureCounter;
	}
	
	public static void bindTexture(int id) {
		if (currentTexture == id) {
			return;
		}
		Texture tex = textures.get(id);
		if (tex == null || tex.id == Registry.INVALID) {
			if (currentTexture != Registry.INVALID) {
				currentTexture = Registry.INVALID;
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, invalidTexture);
			}
			return;
		}
		currentTexture = id;
		FrameBufferManager.unbindFrameBufferTexture();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex.id);
	}
	
	public static void unbindTexture() {
		if (currentTexture != 0 && Nemgine.getSide() == Side.CLIENT) {
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
		if (tex.id != Registry.INVALID) {
			GL11.glDeleteTextures(tex.id);
		}
		textures.remove(id);
	}
	
	public static void disposeAll() {
		if (Nemgine.getSide() == Side.SERVER) {
			return;
		}
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		Iterator<Integer> keys = textures.keySet().iterator();
		
		while (keys.hasNext()) {
			Texture tex = textures.get(keys.next());
			if (tex.id != Registry.INVALID) {
				GL11.glDeleteTextures(tex.id);
			}
		}
		textures.clear();
	}
	
	public static Dimension textureDimensions(int id) {
		Texture tex = textures.get(id);
		if (tex == null) {
			return null;
		}
		return new Dimension(tex.width, tex.height);
	}
	
	public static boolean initializeTextureFile(int id, String file) {
		if (currentTexture == id) {
			return false;
		}
		Texture tex = textures.get(id);
		if (tex == null || tex.id != Registry.INVALID) {
			return false;
		}
		if (Loader.loading()) {
			Loader.loadingTexture(new File(file).getName());
		}
		BufferedImage image = readImage(file);
		if (image == null) {
			TextureException ex = new TextureException(Registry.TEXTURE_MANAGER_LOADER_GLOBAL_ERROR);
			ex.setThrower(Registry.TEXTURE_MANAGER_NAME);
			ex.setTextureInfo(file);
			ex.printStackTrace();
			if (Loader.loading()) { 
				Loader.failedToLoadResource(Registry.LOADING_SCREEN_ERROR);
			}
			return false;
		}
		return initializeTextureImageARGB(id, image);
	}
	
	public static boolean initializeTextureImageGrayscale(int id, BufferedImage image) {
		return initializeTexturePixelsGrayscale(id, image.getRGB(0, 0, image.getWidth(), image.getHeight(), new int[image.getWidth() * image.getHeight()], 0, image.getWidth()), image.getWidth(), image.getHeight());
	}
	
	public static boolean initializeTextureImageARGB(int id, BufferedImage image) {
		return initializeTexturePixelsARGB(id, image.getRGB(0, 0, image.getWidth(), image.getHeight(), new int[image.getWidth() * image.getHeight()], 0, image.getWidth()), image.getWidth(), image.getHeight());
	}
	
	public static boolean initializeTextureImageABGR(int id, BufferedImage image) {
		return initializeTexturePixelsABGR(id, image.getRGB(0, 0, image.getWidth(), image.getHeight(), new int[image.getWidth() * image.getHeight()], 0, image.getWidth()), image.getWidth(), image.getHeight());
	}
	
	public static boolean initializeTexturePixelsGrayscale(int id, int[] pixels_, int width, int height) {
		if (currentTexture == id) {
			return false;
		}
		Texture tex = textures.get(id);
		if (tex == null || tex.id != Registry.INVALID) {
			return false;
		}
		byte[] pixels = new byte[width * height];
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = (byte)(pixels_[i] >> 16);
		}
		
		ByteBuffer res = BufferUtils.createByteBuffer(pixels.length);
		res.put(pixels).flip();
		
		if (Loader.loading()) {
			Loader.requestContext();
		}
		int glid = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, glid);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RED, width, height, 0, GL11.GL_RED, GL11.GL_UNSIGNED_BYTE, res);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		textures.put(id, new Texture(glid, width, height));
		if (Loader.loading()) {
			Loader.textureLoaded();
			Loader.handOffContext();
		}
		return true;
	}
	
	public static boolean initializeTexturePixelsARGB(int id, int[] pixels, int width, int height) {
		for (int i = 0; i < width * height; i++) {
			int alpha = (pixels[i] & 0xFF000000) >> 24;
			int red = (pixels[i] & 0xFF0000) >> 16;
			int green = (pixels[i] & 0xFF00) >> 8;
			int blue = (pixels[i] & 0xFF);

			pixels[i] = alpha << 24 | blue << 16 | green << 8 | red;
		}
		return initializeTexturePixelsABGR(id, pixels, width, height);
	}
	
	public static boolean initializeTexturePixelsABGR(int id, int[] pixels, int width, int height) {
		if (currentTexture == id) {
			return false;
		}
		Texture tex = textures.get(id);
		if (tex == null || tex.id != Registry.INVALID) {
			return false;
		}
		IntBuffer res = BufferUtils.createIntBuffer(pixels.length);
		res.put(pixels).flip();

		if (Loader.loading()) {
			Loader.requestContext();
		}
		int glid = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, glid);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, res);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		textures.put(id, new Texture(glid, width, height));
		if (Loader.loading()) {
			Loader.textureLoaded();
			Loader.handOffContext();
		}
		return true;
	}
	
	private static BufferedImage readImage(String file) {
		try {
			InputStream stream = Nemgine.class.getResourceAsStream("/" + file);
			BufferedImage image = ImageIO.read(stream);
			stream.close();
			return image;
		} catch (IOException e) {
			TextureException ex = new TextureException(Registry.TEXTURE_LOADER_NOT_FOUND);
			ex.setThrower(Registry.TEXTURE_LOADER_NAME);
			ex.setTextureInfo(file);
			ex.printStackTrace();
			e.printStackTrace();
			return null;
		}
	}
	
	protected static int loadMissingTextureAndLogo() {
		int[] pixels = null;
		int w = Registry.INVALID;
		int h = Registry.INVALID;
		
		try {
			InputStream stream = Nemgine.class.getResourceAsStream("/" + Registry.TEXTURE_MISSING_PATH);
			BufferedImage image = ImageIO.read(stream);
			w = image.getWidth();
			h = image.getHeight();
			pixels = new int[w * h];
			image.getRGB(0, 0, w, h, pixels, 0, w);
			stream.close();
		} catch (IOException e) {
			Logger.log(Registry.NEMGINE_NAME, Registry.TEXTURE_LOADER_MISSING_ERROR, false);
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

		IntBuffer res = BufferUtils.createIntBuffer(data.length * 4);
		res.put(data).flip();
		
		invalidTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, invalidTexture);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, w, h, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, res);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		int id = generateTextures();
		Loader.silent = true;
		initializeTextureFile(id, Registry.TEXTURE_LOGO_PATH);
		Loader.silent = false;
		return id;
	}
}
