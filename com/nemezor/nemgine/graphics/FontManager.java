package com.nemezor.nemgine.graphics;

import java.awt.Graphics;
import java.awt.Point;
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
	
	public static int getFontHeight(int fontId) {
		Font font = fonts.get(fontId);
		
		if (font == null || font.state == Registry.INVALID || Nemgine.getSide().isServer()) {
			return Registry.INVALID;
		}
		return font.heightPx;
	}
	
	public static String[] split(int fontId, String string, int maxLength) {
		Font font = fonts.get(fontId);
		
		if (font == null || font.state == Registry.INVALID || Nemgine.getSide().isServer()) {
			return null;
		}
		if (string.contains("\n")) {
			return null;
		}
		if (getStringWidth(fontId, string) <= maxLength) {
			return new String[] {string, null};
		}
		String[] strings = new String[2];
		int width = 0;
		int index = 0;
		
		for (char c : string.toCharArray()) {
			if (c == '\r' || c == '\b' || c == '\f' || c == '\0') {
				index++;
				continue;
			}else if (c == '\t') {
				if (width + font.tabWidthPx > maxLength) {
					if (index == 0) {
						return null;
					}
					strings[0] = string.substring(0, index);
					strings[1] = string.substring(index);
					return strings;
				}else{
					width += font.tabWidthPx;
				}
				index++;
				continue;
			}
			GLCharacter glchar = font.chars.get(c);
			if (width + glchar.width > maxLength) {
				if (index == 0) {
					return null;
				}
				strings[0] = string.substring(0, index);
				strings[1] = string.substring(index);
				return strings;
			}else{
				width += glchar.width;
			}
			index++;
		}
		return null;
	}
	
	public static String[] ellipsize(int fontId, String string, int maxLength) {
		Font font = fonts.get(fontId);
		
		if (font == null || font.state == Registry.INVALID || Nemgine.getSide().isServer()) {
			return null;
		}
		if (string.contains("\n")) {
			return null;
		}
		if (getStringWidth(fontId, string) <= maxLength) {
			return new String[] {string, null};
		}
		String[] strings = new String[2];
		int width = 0;
		int max = maxLength - getStringWidth(fontId, "...");
		int index = 0;
		
		if (max <= 0) {
			return null;
		}
		for (char c : string.toCharArray()) {
			if (c == '\r' || c == '\b' || c == '\f' || c == '\0') {
				index++;
				continue;
			}else if (c == '\t') {
				if (width + font.tabWidthPx > max) {
					if (index == 0) {
						return null;
					}
					strings[0] = string.substring(0, index) + "...";
					strings[1] = string.substring(index);
					return strings;
				}else{
					width += font.tabWidthPx;
				}
				index++;
				continue;
			}
			GLCharacter glchar = font.chars.get(c);
			if (width + glchar.width > max) {
				if (index == 0) {
					return null;
				}
				strings[0] = string.substring(0, index) + "...";
				strings[1] = string.substring(index);
				return strings;
			}else{
				width += glchar.width;
			}
			index++;
		}
		return null;
	}
	
	public static void drawStringInBoundsSingleLine(int fontId, float x, float y, String string, Rectangle bounds, Color color, boolean ellipsize, Matrix4f transformation, Matrix4f projection, int highlightBegin, int highlightEnd) {
		Font font = fonts.get(fontId);
		
		if (font == null || font.state == Registry.INVALID || Nemgine.getSide().isServer()) {
			return;
		}
		String[] lines = string.split("\n");
		
		if (x + getStringWidth(fontId, lines[0]) >= bounds.x + bounds.width) {
			String[] text = ellipsize ? ellipsize(fontId, lines[0], (int)(bounds.x + bounds.width - x)) : new String[] {lines[0]};
			if (text == null || text[0] == null) {
				return;
			}
			drawString(fontId, x, y, text[0], color, transformation, projection, ellipsize ? null : bounds, highlightBegin, highlightEnd);
		}else{
			drawString(fontId, x, y, lines[0], color, transformation, projection, null, highlightBegin, highlightEnd);
		}
	}
	
	public static void drawStringInBounds(int fontId, float x, float y, String string, Rectangle bounds, Color color, boolean ellipsize, Matrix4f transformation, Matrix4f projection, int highlightBegin, int highlightEnd) {
		Font font = fonts.get(fontId);
		
		if (font == null || font.state == Registry.INVALID || Nemgine.getSide().isServer()) {
			return;
		}
		String[] lines = string.split("\n");
		float beginY = y;
		int currentBegin = 0;
		
		for (String s : lines) {
			int offset = drawStringWrapped(font, fontId, beginY, x, y, s, bounds, color, ellipsize, transformation, projection, 0, highlightBegin - currentBegin, highlightEnd - currentBegin);
			if (offset == Registry.INVALID) {
				return;
			}
			y += offset;
			if (y + font.heightPx > beginY + bounds.height) {
				return;
			}
			currentBegin += s.length();
		}
	}
	
	private static int drawStringWrapped(Font font, int fontId, float beginY, float x, float y, String string, Rectangle bounds, Color color, boolean ellipsize, Matrix4f transformation, Matrix4f projection, int offsetIn, int highlightBegin, int highlightEnd) {
		if (offsetIn == Registry.INVALID) {
			return Registry.INVALID;
		}
		String[] strings;
		int offset = 0;
		if (ellipsize) {
			strings = ellipsize(fontId, string, bounds.width);
		}else{
			strings = split(fontId, string, bounds.width);
		}
		if (strings != null) {
			if (y + offsetIn >= beginY + bounds.height) {
				return Registry.INVALID;
			}
			drawString(fontId, x, y + offsetIn, strings[0], color, transformation, projection, bounds, highlightBegin, highlightEnd);
			offset += font.heightPx;
			if (strings[1] != null) {
				offset += drawStringWrapped(font, fontId, beginY, x, y, strings[1], bounds, color, ellipsize, transformation, projection, offsetIn + offset, highlightBegin - strings[0].length(), highlightEnd - strings[0].length());
			}
		}
		return offset;
	}
	
	public static void drawCenteredString(int fontId, float x, float y, String string, Matrix4f transformation, Matrix4f projection, int highlightBegin, int highlightEnd) {
		drawCenteredString(fontId, x, y, string, Registry.FONT_DEFAULT_COLOR, transformation, projection, highlightBegin, highlightEnd);
	}
	
	public static void drawCenteredString(int fontId, float x, float y, String string, Color color, Matrix4f transformation, Matrix4f projection, int highlightBegin, int highlightEnd) {
		Font font = fonts.get(fontId);
		
		if (font == null || font.state == Registry.INVALID) {
			return;
		}
		Rectangle bounds = getStringBounds(fontId, string);
		drawString(fontId, x - bounds.width / 2, y + font.heightPx - bounds.height / 2, string, color, transformation, projection, null, highlightBegin, highlightEnd);
	}
	
	public static void drawHorizCenteredString(int fontId, float x, float y, String string, Matrix4f transformation, Matrix4f projection, int highlightBegin, int highlightEnd) {
		drawHorizCenteredString(fontId, x, y, string, Registry.FONT_DEFAULT_COLOR, transformation, projection, highlightBegin, highlightEnd);
	}
	
	public static void drawHorizCenteredString(int fontId, float x, float y, String string, Color color, Matrix4f transformation, Matrix4f projection, int highlightBegin, int highlightEnd) {
		int width = getStringWidth(fontId, string);
		drawString(fontId, x - width / 2, y, string, color, transformation, projection, null, highlightBegin, highlightEnd);
	}
	
	public static void drawVertCenteredString(int fontId, float x, float y, String string, Matrix4f transformation, Matrix4f projection, int highlightBegin, int highlightEnd) {
		drawVertCenteredString(fontId, x, y, string, Registry.FONT_DEFAULT_COLOR, transformation, projection, highlightBegin, highlightEnd);
	}
	
	public static void drawVertCenteredString(int fontId, float x, float y, String string, Color color, Matrix4f transformation, Matrix4f projection, int highlightBegin, int highlightEnd) {
		Font font = fonts.get(fontId);
		
		if (font == null || font.state == Registry.INVALID) {
			return;
		}
		int height = getStringHeight(fontId, string);
		drawString(fontId, x, y + font.heightPx - height / 2, string, color, transformation, projection, null, highlightBegin, highlightEnd);
	}
	
	public static void drawString(int fontId, float x, float y, String string, Matrix4f transformation, Matrix4f projection, int highlightBegin, int highlightEnd) {
		drawString(fontId, x, y, string, Registry.FONT_DEFAULT_COLOR, transformation, projection, highlightBegin, highlightEnd);
	}
	
	public static void drawString(int fontId, float x, float y, String string, Color color, Matrix4f transformation, Matrix4f projection, int highlightBegin, int highlightEnd) {
		drawString(fontId, x, y, string, color, transformation, projection, null, highlightBegin, highlightEnd);
	}
	
	public static void drawString(int fontId, float x, float y, String string, Color color, Matrix4f transformation, Matrix4f projection, Rectangle bounds, int highlightBegin, int highlightEnd) {
		Font font = fonts.get(fontId);
		
		if (font == null || font.state == Registry.INVALID || Nemgine.getSide().isServer()) {
			return;
		}
		if (Nemgine.isDebugMode()) {
			Rectangle bounds_ = getStringBounds(fontId, string);
			ShaderManager.bindShader(ShaderManager.getColorShaderID());
			ShaderManager.loadMatrix4(ShaderManager.getColorShaderID(), "transformation", transformation);
			ShaderManager.loadMatrix4(ShaderManager.getColorShaderID(), "projection", projection);
			ShaderManager.loadVector4(ShaderManager.getColorShaderID(), "color", Registry.DEBUG_TEXT_OUTLINE_COLOR.getColorAsVector());
			
			Tessellator.start(Tessellator.LINES);
			Tessellator.addVertex(x, y - font.heightPx);
			Tessellator.addVertex(x + (float)bounds_.getWidth(), y - font.heightPx);

			Tessellator.addVertex(x, y - font.heightPx + (float)bounds_.getHeight());
			Tessellator.addVertex(x + (float)bounds_.getWidth(), y - font.heightPx + (float)bounds_.getHeight());
			
			Tessellator.addVertex(x, y - font.heightPx);
			Tessellator.addVertex(x, y - font.heightPx + (float)bounds_.getHeight());
			
			Tessellator.addVertex(x + (float)bounds_.getWidth(), y - font.heightPx);
			Tessellator.addVertex(x + (float)bounds_.getWidth(), y - font.heightPx + (float)bounds_.getHeight());
			Tessellator.finish();
			
			ShaderManager.unbindShader();
		}

		if (highlightBegin != highlightEnd) {
			ShaderManager.bindShader(ShaderManager.getColorShaderID());
			ShaderManager.loadMatrix4(ShaderManager.getColorShaderID(), "transformation", transformation);
			ShaderManager.loadMatrix4(ShaderManager.getColorShaderID(), "projection", projection);
		}
		ShaderManager.bindShader(ShaderManager.getFontShaderID());
		ShaderManager.loadMatrix4(ShaderManager.getFontShaderID(), Registry.FONT_SHADER_TRANSFORMATION_ATTRIBUTE, transformation);
		ShaderManager.loadMatrix4(ShaderManager.getFontShaderID(), Registry.FONT_SHADER_PROJECTION_ATTRIBUTE, projection);
		ShaderManager.loadVector4(ShaderManager.getFontShaderID(), Registry.FONT_SHADER_COLOR_ATTRIBUTE, color.getColorAsVector());
		GLHelper.enableBlending();
		float beginX = x;
		int current = 0;

		for (char c : string.toCharArray()) {
			if (c == '\r' || c == '\b' || c == '\f' || c == '\0') {
				current++;
				continue;
			}else if (c == '\n') {
				y += font.heightPx;
				x = beginX;
				current++;
				continue;
			}else if (c == '\t') {
				if (highlightBegin != highlightEnd && current >= highlightBegin && current <= highlightEnd) {
					ShaderManager.bindShader(ShaderManager.getColorShaderID());
					ShaderManager.loadMatrix4(ShaderManager.getColorShaderID(), "transformation", transformation);
					ShaderManager.loadMatrix4(ShaderManager.getColorShaderID(), "projection", projection);
					ShaderManager.loadVector4(ShaderManager.getColorShaderID(), "color", Registry.FONT_HIGHLIGHT_COLOR.getColorAsVector());
					Tessellator.start(Tessellator.QUADS);

					Tessellator.addVertex(x, y);
					Tessellator.addVertex(x + font.tabWidthPx, y);
					Tessellator.addVertex(x + font.tabWidthPx, y - font.heightPx);
					Tessellator.addVertex(x, y - font.heightPx);
					
					Tessellator.finish();
					ShaderManager.bindShader(ShaderManager.getFontShaderID());
					ShaderManager.loadVector4(ShaderManager.getFontShaderID(), Registry.FONT_SHADER_COLOR_ATTRIBUTE, color.getColorAsVector());
				}
				x += font.tabWidthPx;
				current++;
				continue;
			}
			GLCharacter glchar = font.chars.get(c);
			if (glchar == null) {
				current++;
				continue;
			}
			TextureManager.bindTexture(glchar.glTex);
			float[] texCoords = glchar.textureCoords;

			float top = y - glchar.height;
			float bottom = y;
			float left = x;
			float right = x + glchar.width;
			float ttop = texCoords[1];
			float tbottom = texCoords[3];
			float tleft = texCoords[0];
			float tright = texCoords[2];
			
			if (bounds != null) {
				if (right < bounds.x || left > bounds.x + bounds.width || bottom < bounds.y || top > bounds.y + bounds.height) {
					x += glchar.width;
					current++;
					continue;
				}
				if (left < bounds.x) {
					float diff = bounds.x - left;
					float tdiff = diff / (right - left) * (tright - tleft);
					left += diff;
					tleft += tdiff;
				}else if (right > bounds.x + bounds.width) {
					float diff = right - (bounds.x + bounds.width);
					float tdiff = diff / (right - left) * (tright - tleft);
					right -= diff;
					tright -= tdiff;
				}
				if (top < bounds.y) {
					float diff = bounds.y - top;
					float tdiff = diff / (bottom - top) * (tbottom - ttop);
					top += diff;
					ttop += tdiff;
				}else if (bottom > bounds.y + bounds.height) {
					float diff = bottom - (bounds.y + bounds.height);
					float tdiff = diff / (bottom - top) * (tbottom - ttop);
					bottom -= diff;
					tbottom -= tdiff;
				}
			}
			if (highlightBegin != highlightEnd && current >= highlightBegin && current <= highlightEnd) {
				ShaderManager.bindShader(ShaderManager.getColorShaderID());
				ShaderManager.loadMatrix4(ShaderManager.getColorShaderID(), "transformation", transformation);
				ShaderManager.loadMatrix4(ShaderManager.getColorShaderID(), "projection", projection);
				ShaderManager.loadVector4(ShaderManager.getColorShaderID(), "color", Registry.FONT_HIGHLIGHT_COLOR.getColorAsVector());
				Tessellator.start(Tessellator.QUADS);

				Tessellator.addVertex(left, top);
				Tessellator.addVertex(right, top);
				Tessellator.addVertex(right, bottom);
				Tessellator.addVertex(left, bottom);
				
				Tessellator.finish();
				ShaderManager.bindShader(ShaderManager.getFontShaderID());
				ShaderManager.loadVector4(ShaderManager.getFontShaderID(), Registry.FONT_SHADER_COLOR_ATTRIBUTE, color.getColorAsVector());
			}
			Tessellator.start(Tessellator.QUADS);
			
			Tessellator.addVertex(left, top);
			Tessellator.addTexCoord(tleft, ttop);
			Tessellator.addVertex(right, top);
			Tessellator.addTexCoord(tright, ttop);
			Tessellator.addVertex(right, bottom);
			Tessellator.addTexCoord(tright, tbottom);
			Tessellator.addVertex(left, bottom);
			Tessellator.addTexCoord(tleft, tbottom);

			Tessellator.finish();
			x += glchar.width;
			current++;
		}
		
		GLHelper.disableBlending();
	}
	
	public static Point getPositionInText(int fontId, String string, int charPos, int wrapWidth) {
		Font font = fonts.get(fontId);
		
		if (font == null || font.state == Registry.INVALID || Nemgine.getSide().isServer()) {
			return null;
		}
		if (charPos == 0) {
			return new Point(0, 0);
		}
		int x = 0;
		int y = 0;
		int counter = 0;
		
		for (char c : string.toCharArray()) {
			if (c == '\r' || c == '\b' || c == '\f' || c == '\0') {
				
			}else if (c == '\n') {
				y += font.heightPx;
				x = 0;
			}else if (c == '\t') {
				if (x + font.tabWidthPx > wrapWidth) {
					x = 0;
					y += font.heightPx;
				}
				x += font.tabWidthPx;
			}else{
				GLCharacter glc = font.chars.get(c);
				if (glc != null) {
					if (x + glc.width > wrapWidth) {
						x = 0;
						y += font.heightPx;
					}
					x += glc.width;
				}
			}
			if (counter == charPos - 1) {
				break;
			}
			counter++;
		}
		
		return new Point(x, y);
	}
	
	public static int getCharIndexFromPosition(int fontId, String string, int x, int y, int wrapWidth) {
		if (x < 0 || x > wrapWidth || y < 0) {
			return Registry.INVALID;
		}
		Font font = fonts.get(fontId);
		
		if (font == null || font.state == Registry.INVALID || Nemgine.getSide().isServer()) {
			return Registry.INVALID;
		}
		int tx = 0;
		int ty = 0;
		int counter = 0;
		
		for (char c : string.toCharArray()) {
			if (c == '\r' || c == '\b' || c == '\f' || c == '\0') {
				
			}else if (c == '\n') {
				if (ty < y && ty + font.heightPx >= y) {
					return counter;
				}
				ty += font.heightPx;
				tx = 0;
			}else if (c == '\t') {
				if (tx + font.tabWidthPx > wrapWidth) {
					tx = 0;
					ty += font.heightPx;
				}
				if (tx < x && tx + font.tabWidthPx >= x && ty < y && ty + font.heightPx >= y) {
					return counter;
				}
				tx += font.tabWidthPx;
			}else{
				GLCharacter glc = font.chars.get(c);
				if (glc != null) {
					if (tx + glc.width > wrapWidth) {
						tx = 0;
						ty += font.heightPx;
					}
					if (tx < x && tx + glc.width >= x && ty < y && ty + font.heightPx >= y) {
						return counter;
					}
					tx += glc.width;
				}
			}
			counter++;
		}
		return counter;
	}
	
	public static int getStringHeight(int fontId, String string) {
		Font font = fonts.get(fontId);
		
		if (font == null || font.state == Registry.INVALID || Nemgine.getSide().isServer() || string.length() == 0) {
			return Registry.INVALID;
		}
		int height = font.heightPx;
		int lines = 1;
		
		for (char c : string.toCharArray()) {
			if (c == '\n') {
				lines++;
			}
		}
		
		return height * lines;
	}
	
	public static int getStringWidth(int fontId, String string) {
		Font font = fonts.get(fontId);
		
		if (font == null || font.state == Registry.INVALID || Nemgine.getSide().isServer()) {
			return Registry.INVALID;
		}
		int width = 0;
		int tempWidth = 0;
		
		for (char c : string.toCharArray()) {
			if (c == '\r' || c == '\b' || c == '\f' || c == '\0') {
				continue;
			}else if (c == '\n') {
				if (tempWidth > width) {
					width = tempWidth;
				}
				tempWidth = 0;
				continue;
			}else if (c == '\t') {
				tempWidth += font.tabWidthPx;
				continue;
			}
			GLCharacter glc = font.chars.get(c);
			if (glc != null) {
				tempWidth += glc.width;
			}
		}
		if (tempWidth > width) {
			width = tempWidth;
		}
		
		return width;
	}
	
	public static Rectangle getStringBounds(int fontId, String string) {
		Font font = fonts.get(fontId);
		
		if (font == null || font.state == Registry.INVALID || Nemgine.getSide().isServer() || string.length() == 0) {
			return new Rectangle(Registry.INVALID, Registry.INVALID);
		}
		int width = 0;
		int tempWidth = 0;
		int height = font.heightPx;
		int lines = 1;
		
		for (char c : string.toCharArray()) {
			if (c == '\r' || c == '\b' || c == '\f' || c == '\0') {
				continue;
			}else if (c == '\n') {
				lines++;
				if (tempWidth > width) {
					width = tempWidth;
				}
				tempWidth = 0;
				continue;
			}else if (c == '\t') {
				tempWidth += font.tabWidthPx;
				continue;
			}
			GLCharacter glc = font.chars.get(c);
			if (glc != null) {
				tempWidth += glc.width;
			}
		}
		if (tempWidth > width) {
			width = tempWidth;
		}
		
		return new Rectangle(width, height * lines);
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
		int avgWidth = 0;
		int charCount = 0;
		
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
				int charWidth = tempGraphics.getFontMetrics().charWidth((char)i);
				GLCharacter glchar = new GLCharacter((char)i, currX, currY, charWidth, highest + tempGraphics.getFontMetrics().getDescent(), currentTexture);
				charMap.put(glchar.character, glchar);
				currX += r.getWidth();
				if (currX + 1 > width) {
					width = currX + 1;
				}
				charCount++;
				avgWidth += charWidth;
			}
		}
		if (Loader.loading()) {
			Loader.stepLoader();
		}
		avgWidth /= charCount;
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
		nFont.initializeFont(charMap, new FontMetrics(font.getFamily(), font.getFontName(), font.getSize(), font.getStyle()), highest / 2, avgWidth * Registry.FONT_TAB_WIDTH_IN_CHARS);
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
