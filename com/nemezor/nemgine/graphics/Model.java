package com.nemezor.nemgine.graphics;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class Model {

	protected ModelData data;
	protected int id;
	protected int[] VBOids;
	
	public Model(int id, int[] VBOids, ModelData raw) {
		this.id = id;
		this.data = raw;
		this.VBOids = VBOids;
	}
	
	protected void dispose() {
		GL30.glDeleteVertexArrays(id);
		for (int vboId : VBOids) {
			GL15.glDeleteBuffers(vboId);
		}
	}
	
	protected ModelData getData() {
		return data;
	}
	
	protected boolean isTextured() {
		return data.textures.length > 0;
	}
}
