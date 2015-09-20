package com.nemezor.nemgine.graphics;

public class ModelData {

	protected float[] vertices;
	protected float[] normals;
	protected float[] textures;
	protected int[] indices;
	
	public ModelData(float[] vert, float[] norm, float[] tex, int[] ind) {
		vertices = vert;
		normals = norm;
		textures = tex;
		indices = ind;
	}
	
}
