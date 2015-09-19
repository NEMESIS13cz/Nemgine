package com.nemezor.nemgine.graphics;

import org.lwjgl.util.vector.Matrix4f;

public class NemGL {

	protected static float zNear;
	protected static float zFar;
	protected static float aspect;
	protected static float FOV;
	
	private static Matrix4f projection;
	
	protected static void updatePerspectiveProjection() {
		projection = GLHelper.initPerspectiveProjectionMatrix(FOV, aspect, zNear, zFar);
	}
	
	public static Matrix4f getCurrentPerspectiveProjectionMatrix() {
		return projection;
	}
}
