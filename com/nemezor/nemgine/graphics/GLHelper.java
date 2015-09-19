package com.nemezor.nemgine.graphics;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class GLHelper {
	
	public static Matrix4f initTransformationMatrix(Vector3f translation, Vector3f rotation, Vector3f scale) {
		Matrix4f mat = new Matrix4f();
		
		Matrix4f.translate(translation, mat, mat);
		Matrix4f.rotate(rotation.getX(), new Vector3f(1, 0, 0), mat, mat);
		Matrix4f.rotate(rotation.getY(), new Vector3f(0, 1, 0), mat, mat);
		Matrix4f.rotate(rotation.getZ(), new Vector3f(0, 0, 1), mat, mat);
		Matrix4f.scale(scale, mat, mat);
		
		return mat;
	}
	
	public static Matrix4f initPerspectiveProjectionMatrix(float fieldOfView, float width, float height, float zNear, float zFar) {
		float a = width / height;
		return initPerspectiveProjectionMatrix(fieldOfView, a, zNear, zFar);
	}
	
	public static Matrix4f initPerspectiveProjectionMatrix(float fieldOfView, float aspectRatio, float zNear, float zFar) {
		Matrix4f mat = new Matrix4f();
		float fov = 1.0f / ((float)Math.tan(fieldOfView / 2.0f));
		float zp = zFar + zNear;
		float zm = zFar - zNear;
		
		mat.m00 = fov / aspectRatio;
		mat.m11 = fov;
		mat.m22 = -zp / zm;
		mat.m23 = -1.0f;
		mat.m32 = -(2.0f * zFar * zNear) / zm;
		mat.m33 = 0.0f;
		
		return mat;
	}
}
