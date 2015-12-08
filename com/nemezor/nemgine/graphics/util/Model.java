package com.nemezor.nemgine.graphics.util;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class Model {

	public ModelData data;
	public int id;
	public int[] VBOids;
	
	public Model(int id, int[] VBOids, ModelData raw) {
		this.id = id;
		this.data = raw;
		this.VBOids = VBOids;
	}
	
	public void dispose() {
		GL30.glDeleteVertexArrays(id);
		for (int vboId : VBOids) {
			GL15.glDeleteBuffers(vboId);
		}
	}
	
	public ModelData getData() {
		return data;
	}
	
	public boolean isTextured() {
		return data.textures.length > 0;
	}
}
