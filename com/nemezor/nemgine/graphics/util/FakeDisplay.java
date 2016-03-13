package com.nemezor.nemgine.graphics.util;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.nemezor.nemgine.graphics.GLHelper;
import com.nemezor.nemgine.input.IKeyInput;
import com.nemezor.nemgine.misc.Color;
import com.nemezor.nemgine.misc.Registry;

public class FakeDisplay extends Display {

	private float FOV;
	private float width, height;
	private float zNear, zFar;
	private Matrix4f persp, ortho, ortho2D, transform;
	private int x, y;
	
	public FakeDisplay(int w, int h, float fieldOfView, float zn, float zf) {
		super(Registry.INVALID);
		FOV = fieldOfView;
		zNear = zn;
		zFar = zf;
		width = w;
		height = h;
		resize();
	}

	public void initialize(float fieldOfView, int w, int h, float zn, float zf, boolean resizable, long share) {
		
	}
	
	public void setKeyHandler(IKeyInput handler) {
		
	}
	
	public boolean closeRequested() {
		return false;
	}
	
	public void setSize(int w, int h) {
		this.width = w;
		this.height = h;
		resize();
	}
	
	public void setTranslation(int x, int y) {
		this.x = x;
		this.y = y;
		resize();
	}
	
	public void setStatus(int s) {
		
	}
	
	public int getStatus() {
		return 0;
	}
	
	public Matrix4f getPerspectiveProjectionMatrix() {
		return persp;
	}
	
	public Matrix4f getOrthographicProjectionMatrix() {
		return ortho;
	}
	
	public Matrix4f get2DOrthographicProjectionMatrix() {
		return ortho2D;
	}
	
	public Matrix4f getTransformationMatrix() {
		return transform;
	}
	
	public void recalcTransform(Display masterWindow) {
		Vector3f translate = new Vector3f(x, y, 0);
		Vector3f rotate = new Vector3f(0, 0, 0);
		Vector3f scale = new Vector3f(1, 1, 1);
		transform = Matrix4f.mul(masterWindow.getTransformationMatrix(), GLHelper.initTransformationMatrix(translate, rotate, scale), new Matrix4f());
		ortho2D = masterWindow.get2DOrthographicProjectionMatrix();
	}
	
	public void prepareRender() {
		
	}
	
	public void finishRender() {
		
	}
	
	public void dispose() {
		
	}
	
	public void changeTitle(String newTitle) {
		
	}
	
	public void fill(Color c) {
		
	}
	
	public int getWidth() {
		return (int)width;
	}
	
	public int getHeight() {
		return (int)height;
	}
	
	public long getGLFWId() {
		return Registry.INVALID;
	}
	
	public boolean isInvalid() {
		return false;
	}
	
	public void setWireframeRender(boolean on) {
		
	}
	
	public boolean displayResized() {
		return false;
	}
	
	private void resize() {
		persp = GLHelper.initPerspectiveProjectionMatrix(FOV, width, height, zNear, zFar);
		ortho = GLHelper.initOrthographicProjectionMatrix(-width / 2, width / 2, -height / 2, height / 2, zNear, zFar);
	}
}
