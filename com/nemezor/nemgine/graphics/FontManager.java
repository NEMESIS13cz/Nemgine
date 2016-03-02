package com.nemezor.nemgine.graphics;

import java.awt.Graphics;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;

import org.lwjgl.util.vector.Matrix4f;

import com.nemezor.nemgine.graphics.util.Font;
import com.nemezor.nemgine.graphics.util.GLCharacter;
import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.misc.Color;
import com.nemezor.nemgine.misc.Logger;
import com.nemezor.nemgine.misc.Platform;
import com.nemezor.nemgine.misc.Registry;

public class FontManager {

	private static HashMap<Integer, Font> fonts = new HashMap<Integer, Font>();
	private static int fontCounter = 0;
	
	public static synchronized int generateFonts() {
		if (Nemgine.getSide().isServer()) {
			return Registry.INVALID;
		}
		fontCounter++;
		fonts.put(fontCounter, new Font(TextureManager.generateTextures()));
		return fontCounter;
	}
	
	public static void drawString(int fontId, int x, int y, String string, Matrix4f transformation, Matrix4f projection) {
		drawString(fontId, x, y, string, Registry.FONT_DEFAULT_COLOR, transformation, projection);
	}
	
	public static void drawString(int fontId, int x, int y, String string, Color color, Matrix4f transformation, Matrix4f projection) {
		Font font = fonts.get(fontId);
		
		if (font.state == Registry.INVALID || Nemgine.getSide().isServer()) {
			return;
		}
		char[] chars = string.toCharArray();
		
		TextureManager.bindTexture(font.textureId);
		ShaderManager.bindShader(ShaderManager.getFontShaderID());
		ShaderManager.loadMatrix4(ShaderManager.getFontShaderID(), Registry.FONT_SHADER_TRANSFORMATION_ATTRIBUTE, transformation);
		ShaderManager.loadMatrix4(ShaderManager.getFontShaderID(), Registry.FONT_SHADER_PROJECTION_ATTRIBUTE, projection);
		ShaderManager.loadVector4(ShaderManager.getFontShaderID(), Registry.FONT_SHADER_COLOR_ATTRIBUTE, color.getColorAsVector());
		GLHelper.enableBlending();
		Tessellator.start();
		
		for (char c : chars) {
			GLCharacter glchar = font.chars.get(c);
			if (glchar == null) {
				continue;
			}
			float[] texCoords = glchar.textureCoords;
			Tessellator.addVertex(x, y, 0);
			Tessellator.addTexCoord(texCoords[0], texCoords[1]);
			Tessellator.addVertex(x + glchar.width, y, 0);
			Tessellator.addTexCoord(texCoords[2], texCoords[1]);
			Tessellator.addVertex(x + glchar.width, y + glchar.height, 0);
			Tessellator.addTexCoord(texCoords[2], texCoords[3]);
			
			Tessellator.addVertex(x + glchar.width, y + glchar.height, 0);
			Tessellator.addTexCoord(texCoords[2], texCoords[3]);
			Tessellator.addVertex(x, y + glchar.height, 0);
			Tessellator.addTexCoord(texCoords[0], texCoords[3]);
			Tessellator.addVertex(x, y, 0);
			Tessellator.addTexCoord(texCoords[0], texCoords[1]);
			
			x += glchar.width;
		}
		
		Tessellator.finish();
		GLHelper.disableBlending();
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
		HashMap<Character, GLCharacter> charMap = new HashMap<Character, GLCharacter>();
		
		int currX = 0;
		int currY = 0;
		int highest = 0;
		int width = 0;
		int height = 0;
		FontRenderContext context = new FontRenderContext(null, true, true);
		BufferedImage temp = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics tempGraphics = temp.getGraphics();
		tempGraphics.setFont(font);
		Rectangle2D r = font.getMaxCharBounds(context);
		highest = (int)r.getHeight();
		
		for (int i = 0; i < 0xFFFF; i++) {
			if (font.canDisplay(i)) {
				if (currX + r.getWidth() > Platform.getOpenGLTextureSize()) {
					currX = 0;
					currY += highest;
				}
				GLCharacter glchar = new GLCharacter((char)i, currX, currY, tempGraphics.getFontMetrics().charWidth((char)i), highest + tempGraphics.getFontMetrics().getDescent() - 1);
				charMap.put(glchar.character, glchar);
				currX += r.getWidth();
				if (currX + 1 > width) {
					width = currX + 1;
				}
			}
		}
		height = currY + highest;
		
		tempGraphics.dispose();
		BufferedImage texture = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		Graphics g = texture.getGraphics();
		g.setFont(font);
		
		Iterator<Character> iter = charMap.keySet().iterator();
		while (iter.hasNext()) {
			GLCharacter c = charMap.get(iter.next());
			g.drawString(String.valueOf(c.character), c.x, c.y + highest);
			c.initialize(texture.getWidth(), texture.getHeight());
		}
		g.dispose();
		TextureManager.initializeTextureImageGrayscale(nFont.textureId, texture);
		nFont.initializeFont(charMap, font);
		
		return true;
	}
}
