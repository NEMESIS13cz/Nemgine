package com.nemezor.nemgine.graphics;

import java.awt.Graphics;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import com.nemezor.nemgine.graphics.util.Font;
import com.nemezor.nemgine.graphics.util.GLCharacter;
import com.nemezor.nemgine.main.Nemgine;
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
		fonts.put(fontCounter, new Font());
		return fontCounter;
	}
	
	public static void drawString(int fontId, String string, Matrix4f transformation, Matrix4f projection, String transformationAttribName, String projectionAttribName) {
		char[] chars = string.toCharArray();
		Font font = fonts.get(fontId);
		
		if (font.state == Registry.INVALID || Nemgine.getSide().isServer()) {
			return;
		}
		
		int VAOid = ModelManager.fontModelInstance.id.get(DisplayManager.getCurrentDisplayID());
		
		for (char c : chars) {
			GLCharacter ch = font.chars.get(c);
			GL30.glBindVertexArray(VAOid);
			FloatBuffer buf = BufferUtils.createFloatBuffer(8);
			buf.put(ch.textureCoords);
			buf.flip();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, ModelManager.fontModelInstance.VBOids.get(DisplayManager.getCurrentDisplayID())[2]);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buf, GL15.GL_STATIC_DRAW);
			GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
			GL30.glBindVertexArray(0);
			
			
		}
	}
	
	public static boolean initializeFont(int id, java.awt.Font font) {
		if (Nemgine.getSide().isServer()) {
			return false;
		}
		ArrayList<GLCharacter> chars = new ArrayList<GLCharacter>();
		
		int currX = 0;
		int currY = 0;
		int highest = 0;
		int width = 0;
		int height = 0;
		FontRenderContext context = new FontRenderContext(null, true, true);
		
		for (int i = 0; i < 0xFFFF; i++) {
			if (font.canDisplay(i)) {
				Rectangle2D r = font.getMaxCharBounds(context);
				if (currX + r.getWidth() > Platform.getOpenGLTextureSize()) {
					currX = 0;
					currY += highest;
				}
				if (r.getHeight() > highest) {
					highest = (int)r.getHeight();
				}
				chars.add(new GLCharacter((char)i, currX, currY, (int)r.getWidth(), (int)r.getHeight()));
				currX += r.getWidth();
				if (currX + 1 > width) {
					width = currX + 1;
				}
			}
		}
		height = currY + highest;
		
		BufferedImage texture = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = texture.getGraphics();
		g.setFont(font);
		
		for (GLCharacter c : chars) {
			g.drawString(String.valueOf(c.character), c.x, c.y);
			c.initialize(texture.getWidth(), texture.getHeight());
		}
		g.dispose();
		/*
		try {
			ImageIO.write(texture, "PNG", new File("fonts.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		
		return false;
	}
}
