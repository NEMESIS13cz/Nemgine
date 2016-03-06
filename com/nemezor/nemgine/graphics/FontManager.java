package com.nemezor.nemgine.graphics;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.lwjgl.util.vector.Matrix4f;

import com.nemezor.nemgine.graphics.util.Font;
import com.nemezor.nemgine.graphics.util.GLCharacter;
import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.misc.Color;
import com.nemezor.nemgine.misc.FontMetrics;
import com.nemezor.nemgine.misc.Logger;
import com.nemezor.nemgine.misc.Platform;
import com.nemezor.nemgine.misc.Registry;

public class FontManager {

	private static HashMap<Integer, Font> fonts = new HashMap<Integer, Font>();
	private static int fontCounter = 0;
	private static int defaultFont;
	
	private FontManager() {}
	
	public static synchronized int generateFonts() {
		if (Nemgine.getSide().isServer()) {
			return Registry.INVALID;
		}
		fontCounter++;
		fonts.put(fontCounter, new Font());
		if (Loader.loading()) {
			Loader.fontCounter++;
		}
		return fontCounter;
	}
	
	public static void drawString(int fontId, float x, float y, String string, Matrix4f transformation, Matrix4f projection) {
		drawString(fontId, x, y, string, Registry.FONT_DEFAULT_COLOR, transformation, projection);
	}
	
	public static void drawString(int fontId, float x, float y, String string, Color color, Matrix4f transformation, Matrix4f projection) {
		Font font = fonts.get(fontId);
		
		if (font.state == Registry.INVALID || Nemgine.getSide().isServer()) {
			return;
		}
		
		ShaderManager.bindShader(ShaderManager.getFontShaderID());
		ShaderManager.loadMatrix4(ShaderManager.getFontShaderID(), Registry.FONT_SHADER_TRANSFORMATION_ATTRIBUTE, transformation);
		ShaderManager.loadMatrix4(ShaderManager.getFontShaderID(), Registry.FONT_SHADER_PROJECTION_ATTRIBUTE, projection);
		ShaderManager.loadVector4(ShaderManager.getFontShaderID(), Registry.FONT_SHADER_COLOR_ATTRIBUTE, color.getColorAsVector());
		GLHelper.enableBlending();
		Tessellator.start(Tessellator.QUADS);
		
		for (char c : string.toCharArray()) {
			GLCharacter glchar = font.chars.get(c);
			if (glchar == null) {
				continue;
			}
			TextureManager.bindTexture(glchar.glTex);
			float[] texCoords = glchar.textureCoords;
			Tessellator.addVertex(x, y - glchar.height, 0);
			Tessellator.addTexCoord(texCoords[0], texCoords[1]);
			Tessellator.addVertex(x + glchar.width, y - glchar.height, 0);
			Tessellator.addTexCoord(texCoords[2], texCoords[1]);
			Tessellator.addVertex(x + glchar.width, y, 0);
			Tessellator.addTexCoord(texCoords[2], texCoords[3]);
			Tessellator.addVertex(x, y, 0);
			Tessellator.addTexCoord(texCoords[0], texCoords[3]);
			
			x += glchar.width;
		}
		
		Tessellator.finish();
		GLHelper.disableBlending();
	}
	
	public static int getStringHeight(int fontId, String string) {
		Font font = fonts.get(fontId);
		
		if (font.state == Registry.INVALID || Nemgine.getSide().isServer()) {
			return Registry.INVALID;
		}
		int height = Registry.INVALID;
		
		for (char c : string.toCharArray()) {
			GLCharacter glc = font.chars.get(c);
			if (glc == null) {
				return Registry.INVALID;
			}
			int cHeight = glc.height;
			if (cHeight > height) {
				height = cHeight;
			}
		}
		
		return height;
	}
	
	public static int getStringWidth(int fontId, String string) {
		Font font = fonts.get(fontId);
		
		if (font.state == Registry.INVALID || Nemgine.getSide().isServer()) {
			return Registry.INVALID;
		}
		int width = 0;
		
		for (char c : string.toCharArray()) {
			GLCharacter glc = font.chars.get(c);
			if (glc == null) {
				return Registry.INVALID;
			}
			width += glc.width;
		}
		
		return width;
	}
	
	public static Rectangle getStringBounds(int fontId, String string) {
		Font font = fonts.get(fontId);
		
		if (font.state == Registry.INVALID || Nemgine.getSide().isServer()) {
			return new Rectangle(Registry.INVALID, Registry.INVALID);
		}
		int width = 0;
		int height = Registry.INVALID;
		
		for (char c : string.toCharArray()) {
			GLCharacter glc = font.chars.get(c);
			if (glc == null) {
				return new Rectangle(Registry.INVALID, Registry.INVALID);
			}
			width += glc.width;
			if (glc.height > height) {
				height = glc.height;
			}
		}
		
		return new Rectangle(width, height);
	}
	
	public static boolean initializeFont(int id, String name, int style, int size) {
		if (Nemgine.getSide().isServer()) {
			return false;
		}
		java.awt.Font font;
		if (Platform.isFontAvailable(name)) {
			font = new java.awt.Font(name, style, size);
		}else{
			Logger.log(Registry.FONT_MANAGER_NAME, Registry.FONT_NOT_FOUND_MESSAGE_1 + name + Registry.FONT_NOT_FOUND_MESSAGE_2 + Registry.FONT_FALLBACK_FONT + Registry.FONT_NOT_FOUND_MESSAGE_3, false);
			font = new java.awt.Font(Registry.FONT_FALLBACK_FONT, style, size);
		}
		Font nFont = fonts.get(id);
		if (nFont == null || nFont.state != Registry.INVALID) {
			return false;
		}
		if (Loader.loading()) {
			Loader.loadingFont(font.getFontName());
		}
		HashMap<Character, GLCharacter> charMap = new HashMap<Character, GLCharacter>();
		
		int currX = 0;
		int currY = 0;
		int highest = 0;
		int width = 0;
		int height = 0;
		ArrayList<Integer> textures = new ArrayList<Integer>();
		FontRenderContext context = new FontRenderContext(null, true, true);
		BufferedImage temp = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics tempGraphics = temp.getGraphics();
		tempGraphics.setFont(font);
		Rectangle2D r = font.getMaxCharBounds(context);
		highest = (int)r.getHeight() + tempGraphics.getFontMetrics().getAscent();
		Loader.silent = true;
		int currentTexture = TextureManager.generateTextures();
		Loader.silent = false;
		if (Loader.loading()) {
			Loader.stepLoader();
		}
		
		for (int i = 0; i < 0xFFFF; i++) {
			if (font.canDisplay(i)) {
				if (currY + highest >= Platform.getOpenGLTextureSize()) {
					textures.add(currentTexture);
					Loader.silent = true;
					currentTexture = TextureManager.generateTextures();
					Loader.silent = false;
					currY = 0;
					currX = 0;
				}
				if (currX + r.getWidth() >= Platform.getOpenGLTextureSize()) {
					currX = 0;
					currY += highest;
				}
				GLCharacter glchar = new GLCharacter((char)i, currX, currY, tempGraphics.getFontMetrics().charWidth((char)i), highest + tempGraphics.getFontMetrics().getDescent(), currentTexture);
				charMap.put(glchar.character, glchar);
				currX += r.getWidth();
				if (currX + 1 > width) {
					width = currX + 1;
				}
			}
		}
		if (Loader.loading()) {
			Loader.stepLoader();
		}
		height = currY + highest;
		textures.add(currentTexture);
		
		tempGraphics.dispose();
		HashMap<Integer, BufferedImage> images = new HashMap<Integer, BufferedImage>();
		HashMap<Integer, Graphics> graphics = new HashMap<Integer, Graphics>();
		for (int i = 0; i < textures.size(); i++) {
			BufferedImage img = new BufferedImage(width, i + 1 == textures.size() ? height : Platform.getOpenGLTextureSize(), BufferedImage.TYPE_BYTE_GRAY);
			images.put(textures.get(i), img);
			graphics.put(textures.get(i), img.createGraphics());
			graphics.get(textures.get(i)).setFont(font);
		}
		if (Loader.loading()) {
			Loader.stepLoader();
		}
		
		Iterator<Character> iter = charMap.keySet().iterator();
		while (iter.hasNext()) {
			GLCharacter c = charMap.get(iter.next());
			BufferedImage img = images.get(c.glTex);
			graphics.get(c.glTex).drawString(String.valueOf(c.character), c.x, c.y + highest);
			c.initialize(img.getWidth(), img.getHeight());
		}
		if (Loader.loading()) {
			Loader.stepLoader();
		}
		Loader.silent = true;
		for (int tId : textures) {
			graphics.get(tId).dispose();
			TextureManager.initializeTextureImageGrayscale(tId, images.get(tId));
		}
		Loader.silent = false;
		nFont.initializeFont(charMap, new FontMetrics(font.getFamily(), font.getFontName(), font.getSize(), font.getStyle()));
		if (Loader.loading()) {
			Loader.fontLoaded();
		}
		
		return true;
	}
	
	public static int getDefaultFontID() {
		return defaultFont;
	}
	
	protected static void initializeDefaultFont() {
		Loader.silent = true;
		defaultFont = FontManager.generateFonts();
		FontManager.initializeFont(defaultFont, Registry.FONT_FALLBACK_FONT, Registry.FONT_DEFAULT_FONT_STYLE, Registry.FONT_DEFAULT_FONT_SIZE);
	}
}
