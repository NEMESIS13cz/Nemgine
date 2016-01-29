package com.nemezor.nemgine.graphics.util;

public class ModelData {

	public float[] vertices;
	public float[] normals;
	public float[] textures;
	public int[] indices;
	
	public ModelData(float[] vert, float[] norm, float[] tex, int[] ind) {
		vertices = vert;
		normals = norm;
		textures = tex;
		indices = ind;
	}
}
