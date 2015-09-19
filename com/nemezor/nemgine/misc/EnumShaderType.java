package com.nemezor.nemgine.misc;

import org.lwjgl.opengl.GL20;

public enum EnumShaderType {

	VERTEX, FRAGMENT;
	
	public String toString() {
		return this == VERTEX ? "Vertex" : "Fragment";
	}
	
	public int getGlShaderType() {
		return this == VERTEX ? GL20.GL_VERTEX_SHADER : GL20.GL_FRAGMENT_SHADER;
	}
}
