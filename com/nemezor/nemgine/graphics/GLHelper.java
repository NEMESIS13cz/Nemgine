package com.nemezor.nemgine.graphics;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.nemezor.nemgine.graphics.util.Camera;
import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.misc.Color;
import com.nemezor.nemgine.misc.Registry;
import com.nemezor.nemgine.misc.Side;

public class GLHelper {
	
	private GLHelper() {}
	
	public static Matrix4f initTransformationMatrix(Camera camera, Vector3f translation, Vector3f rotation, Vector3f scale) {
		Matrix4f mat = initTransformationMatrix(translation, rotation, scale);
		Matrix4f mat2 = initViewMatrix(camera);
		return Matrix4f.mul(mat2, mat, new Matrix4f());
	}
	
	public static Matrix4f initViewMatrix(Camera camera) {
		Matrix4f mat = new Matrix4f();
		Vector3f rotation = camera.getRotation();
		
		Matrix4f.rotate(rotation.getX(), new Vector3f(1, 0, 0), mat, mat);
		Matrix4f.rotate(rotation.getY(), new Vector3f(0, 1, 0), mat, mat);
		Matrix4f.rotate(rotation.getZ(), new Vector3f(0, 0, 1), mat, mat);
		Matrix4f.translate(camera.getPosition().negate(new Vector3f()), mat, mat);
		
		return mat;
	}
	
	public static Matrix4f initTransformationMatrix(Vector3f translation, Vector3f rotation, Vector3f scale) {
		Matrix4f mat = new Matrix4f();

		Matrix4f.translate(translation, mat, mat);
		Matrix4f.rotate(rotation.getY(), new Vector3f(0, 1, 0), mat, mat);
		Matrix4f.rotate(rotation.getZ(), new Vector3f(0, 0, 1), mat, mat);
		Matrix4f.rotate(rotation.getX(), new Vector3f(1, 0, 0), mat, mat);
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
	
	public static Matrix4f init2DOrthographicProjectionMatrix(float width, float height) {
		return initOrthographicProjectionMatrix(0, width, 0, height, 0, 1);
	}
	
	public static Matrix4f initOrthographicProjectionMatrix(float left, float right, float top, float bottom, float zNear, float zFar) {
		Matrix4f mat = new Matrix4f();
		
		mat.m00 = 2.0f / (right - left);
		mat.m11 = 2.0f / (top - bottom);
		mat.m22 = -2.0f / (zFar - zNear);
		mat.m30 = -((right + left) / (right - left));
		mat.m31 = -((top + bottom) / (top - bottom));
		mat.m32 = -((zFar + zNear) / (zFar - zNear));
		
		return mat;
	}
	
	public static Matrix4f initOrthographicProjectionMatrix(float width, float height, float length) {
		Matrix4f mat = new Matrix4f();
		
		mat.m00 = 2.0f / width;
		mat.m11 = 2.0f / height;
		mat.m22 = -2.0f / length;
		
		return mat;
	}
	
	public static Matrix4f initBasicOrthographicProjectionMatrix() {
		return initOrthographicProjectionMatrix(0, 1, 0, 1, -1, 1);
	}
	
	public static int createBufferAndStore(int attrNum, int size, float[] data) {
		if (Nemgine.getSide() == Side.SERVER) {
			return Registry.INVALID;
		}
		int vbo = GL15.glGenBuffers();
		storeIntoBuffer(vbo, attrNum, size, data);
		return vbo;
	}
	
	public static void storeIntoBuffer(int buffer, int attrNum, int size, float[] data) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer);
		FloatBuffer buf = BufferUtils.createFloatBuffer(data.length);
		buf.put(data);
		buf.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buf, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attrNum, size, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public static void enableBlending() {
		if (Nemgine.getSide().isServer()) {
			return;
		}
		GL11.glEnable(GL11.GL_BLEND);
	}
	
	public static void disableBlending() {
		if (Nemgine.getSide().isServer()) {
			return;
		}
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void fillDisplay(Color c) {
		if (Nemgine.getSide().isServer()) {
			return;
		}
		GL11.glClearColor(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
	}
}
