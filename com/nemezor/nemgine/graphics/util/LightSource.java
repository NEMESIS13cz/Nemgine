package com.nemezor.nemgine.graphics.util;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.nemezor.nemgine.graphics.GLHelper;
import com.nemezor.nemgine.misc.Registry;

public class LightSource {

	private Camera cam;
	private Vector3f lightDirection;
	private Matrix4f mat;
	private float minX, maxX, minY, maxY, minZ, maxZ, farHeight, farWidth, nearHeight, nearWidth;
	private Display display;
	
	public LightSource(Vector3f location, Camera userCamera, Display display) {
		this.mat = new Matrix4f();
		this.cam = userCamera;
		this.display = display;
		this.lightDirection = location.negate(null);
		
		calculateWH();
	}
	
	private void calculateWH() {
		farWidth = (float) (Registry.SHADOW_RENDER_DISTANCE * Math.tan(Math.toRadians(display.getFieldOfView())));
		nearWidth = (float) (display.getZNear() * Math.tan(Math.toRadians(display.getFieldOfView())));
		farHeight = farWidth / display.getAspectRatio();
		nearHeight = nearWidth / display.getAspectRatio();
	}
	
	public void updateMatrices() {
		Matrix4f rotation = new Matrix4f();
		rotation.rotate(-cam.getRotation().y, new Vector3f(0, 1, 0));
		rotation.rotate(-cam.getRotation().x, new Vector3f(1, 0, 0));
		Vector3f forward = new Vector3f(Matrix4f.transform(rotation, new Vector4f(0, 0, -1, 0), null));
		
		Vector3f toFar = new Vector3f(forward);
		toFar.scale(Registry.SHADOW_RENDER_DISTANCE);
		Vector3f toNear = new Vector3f(forward);
		toNear.scale(display.getZNear());
		Vector3f centerNear = Vector3f.add(toNear, cam.getPosition(), null);
		Vector3f centerFar = Vector3f.add(toFar, cam.getPosition(), null);
		
		Vector4f[] corners = calculateFrustrumCorners(rotation, forward, centerNear, centerFar);
		
		boolean first = true;
		for (Vector4f corner : corners) {
			if (first) {
				minX = corner.x;
				maxX = corner.x;
				minY = corner.y;
				maxY = corner.y;
				minZ = corner.z;
				maxZ = corner.z;
				first = false;
				continue;
			}
			if (corner.x > maxX) {
				maxX = corner.x;
			}else if (corner.x < minX) {
				minX = corner.x;
			}
			if (corner.y > maxY) {
				maxY = corner.y;
			}else if (corner.y < minY) {
				minY = corner.y;
			}
			if (corner.z > maxZ) {
				maxZ = corner.z;
			}else if (corner.z < minZ) {
				minZ = corner.z;
			}
		}
		
		Vector3f dir = new Vector3f(lightDirection);
		Vector3f center = getCenter();
		dir.normalise();
		center.negate();
		mat.setIdentity();
		float pitch = (float) Math.acos(new Vector2f(dir.x, dir.z).length());
		Matrix4f.rotate(pitch, new Vector3f(1, 0, 0), mat, mat);
		float yaw = (float) ((float) Math.atan(dir.x / dir.z));
		yaw = dir.z > 0 ? yaw - (float)Math.PI : yaw;
		Matrix4f.rotate((float) -yaw, new Vector3f(0, 1, 0), mat, mat);
		Matrix4f.translate(center, mat, mat);
	}
	
	private Vector4f[] calculateFrustrumCorners(Matrix4f rotation, Vector3f forward, Vector3f centerNear, Vector3f centerFar) {
		Vector3f up = new Vector3f(Matrix4f.transform(rotation, new Vector4f(0, 1, 0, 0), null));
		Vector3f right = Vector3f.cross(forward, up, null);
		Vector3f down = new Vector3f(-up.x, -up.y, -up.z);
		Vector3f left = new Vector3f(-right.x, -right.y, -right.z);
		Vector3f farTop = Vector3f.add(centerFar, new Vector3f(up.x * farHeight, up.y * farHeight, up.z * farHeight), null);
		Vector3f farBottom = Vector3f.add(centerFar, new Vector3f(down.x * farHeight, down.y * farHeight, down.z * farHeight), null);
		Vector3f nearTop = Vector3f.add(centerNear, new Vector3f(up.x * nearHeight, up.y * nearHeight, up.z * nearHeight), null);
		Vector3f nearBottom = Vector3f.add(centerNear, new Vector3f(down.x * nearHeight, down.y * nearHeight, down.z * nearHeight), null);
		Vector4f[] points = new Vector4f[8];
		
		points[0] = calculateFrustrumCorner(farTop, right, farWidth);
		points[1] = calculateFrustrumCorner(farTop, left, farWidth);
		points[2] = calculateFrustrumCorner(farBottom, right, farWidth);
		points[3] = calculateFrustrumCorner(farBottom, left, farWidth);
		points[4] = calculateFrustrumCorner(nearTop, right, nearWidth);
		points[5] = calculateFrustrumCorner(nearTop, left, nearWidth);
		points[6] = calculateFrustrumCorner(nearBottom, right, nearWidth);
		points[7] = calculateFrustrumCorner(nearBottom, left, nearWidth);
		
		return points;
	}
	
	private Vector4f calculateFrustrumCorner(Vector3f start, Vector3f direction, float width) {
		Vector3f point = Vector3f.add(start, new Vector3f(direction.x * width, direction.y * width, direction.z * width), null);
		Vector4f point4 = new Vector4f(point.x, point.y, point.z, 1.0f);
		Matrix4f.transform(mat, point4, point4);
		return point4;
	}
	
	private Vector3f getCenter() {
		float x = (minX + maxX) / 2.0f;
		float y = (minY + maxY) / 2.0f;
		float z = (minZ + maxZ) / 2.0f;
		Vector4f cen = new Vector4f(x, y, z, 1.0f);
		Matrix4f invertedLight = new Matrix4f();
		Matrix4f.invert(mat, invertedLight);
		return new Vector3f(Matrix4f.transform(invertedLight, cen, null));
	}
	
	public Matrix4f getProjectionMatrix() {
		return GLHelper.initOrthographicProjectionMatrix(minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	public Matrix4f getTransformationMatrix() {
		return mat;
	}
	
	public void changeLocation(Vector3f location) {
		this.lightDirection = location.negate(null);
	}
	
	public Vector3f getPosition() {
		return lightDirection.negate(null);
	}
	
	public Vector3f getRotation() {
		Vector3f pos = getPosition();
		return new Vector3f((float)Math.tan(pos.y / pos.z), (float)Math.tan(pos.x / pos.z), 0);
	}
}
