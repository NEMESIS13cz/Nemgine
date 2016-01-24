package com.nemezor.nemgine.graphics.util;

import java.util.HashMap;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import com.nemezor.nemgine.graphics.DisplayManager;

public class Model {

	public ModelData data;
	public HashMap<Integer, Integer> id = new HashMap<Integer, Integer>();
	public HashMap<Integer, int[]> VBOids = new HashMap<Integer, int[]>();
	public boolean init = true;
	
	public Model(boolean initialized) {
		init = initialized;
	}
	
	public Model(int id, int[] VBOids, ModelData raw) {
		this.id.put(DisplayManager.getCurrentDisplayID(), id);
		this.data = raw;
		this.VBOids.put(DisplayManager.getCurrentDisplayID(), VBOids);
	}
	
	public void dispose() {
		GL30.glDeleteVertexArrays(id.get(DisplayManager.getCurrentDisplayID()));
		for (int vboId : VBOids.get(DisplayManager.getCurrentDisplayID())) {
			GL15.glDeleteBuffers(vboId);
		}
	}
	
	public ModelData getData() {
		return data;
	}
	
	public boolean isTextured() {
		return data.textures.length > 0;
	}
	
	public void addDisplayResources(int vao, int[] vbo) {
		id.put(DisplayManager.getCurrentDisplayID(), vao);
		VBOids.put(DisplayManager.getCurrentDisplayID(), vbo);
	}
}
