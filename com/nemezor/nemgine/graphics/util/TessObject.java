package com.nemezor.nemgine.graphics.util;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.nemezor.nemgine.graphics.GLHelper;

public class TessObject {

	private int VAOid;
	private int verVBO;
	private int texVBO;
	private int norVBO;
	private int indVBO;
	private boolean textured;
	private int indicesLength;
	
	public TessObject(ModelData data) {
		VAOid = GL30.glGenVertexArrays();
		verVBO = GL15.glGenBuffers();
		texVBO = GL15.glGenBuffers();
		norVBO = GL15.glGenBuffers();
		indVBO = GL15.glGenBuffers();
		upload(data);
	}
	
	public void upload(ModelData data) {
		GL30.glBindVertexArray(VAOid);
		IntBuffer ind = BufferUtils.createIntBuffer(data.indices.length);
		ind.put(data.indices);
		ind.flip();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indVBO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, ind, GL15.GL_STATIC_DRAW);
		GLHelper.storeIntoBuffer(verVBO, 0, 3, data.vertices);
		GLHelper.storeIntoBuffer(texVBO, 1, 2, data.textures);
		GLHelper.storeIntoBuffer(norVBO, 2, 3, data.normals);
		GL30.glBindVertexArray(0);
		textured = data.textures.length > 0;
		indicesLength = data.indices.length;
	}
	
	public void render() {
		GL30.glBindVertexArray(VAOid);
		GL20.glEnableVertexAttribArray(0);
		if (textured) {
			GL20.glEnableVertexAttribArray(1);
		}
		GL20.glEnableVertexAttribArray(2);
		if (textured) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
		}
		GL11.glDrawElements(GL11.GL_TRIANGLES, indicesLength, GL11.GL_UNSIGNED_INT, 0);
		GL20.glDisableVertexAttribArray(0);
		if (textured) {
			GL20.glDisableVertexAttribArray(1);
		}
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}
}
