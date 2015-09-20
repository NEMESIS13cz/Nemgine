package com.nemezor.nemgine.tests;

import java.util.ArrayList;

import org.lwjgl.LWJGLException;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.nemezor.nemgine.graphics.DisplayManager;
import com.nemezor.nemgine.graphics.GLHelper;
import com.nemezor.nemgine.graphics.ModelManager;
import com.nemezor.nemgine.graphics.NemGL;
import com.nemezor.nemgine.graphics.ShaderManager;
import com.nemezor.nemgine.main.Application;
import com.nemezor.nemgine.main.IMainRenderLoop;
import com.nemezor.nemgine.main.IMainTickLoop;
import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.misc.MathHelper;
import com.nemezor.nemgine.misc.Registry;

public class PhysicsPrototypeGame implements IMainRenderLoop, IMainTickLoop {
	
	private ArrayList<Cube> cubes = new ArrayList<Cube>();
	private int shader;
	private int model;
	
	@Application(width = Registry.LOADING_SCREEN_WIDTH, height = Registry.LOADING_SCREEN_HEIGHT, name = "Physics Prototype Test Game")
	public void AppEntryPoint() {
		int id = Nemgine.generateThreads("Main Thread", true);
		Nemgine.bindRenderLoop(id, this);
		Nemgine.bindTickLoop(id, this);
		Nemgine.startThread(id);/*
		Cube cube1 = new Cube(new Vector3f(-25, -5, -50), new Vector3f(0.5f, 0.2f, 0), 5);
		Cube cube2 = new Cube(new Vector3f(20, 0, -50), new Vector3f(-0.5f, 0, 0), 10);
		Cube cube3 = new Cube(new Vector3f(-20, 14, -50), new Vector3f(0, 0, 0), 7);
		
		cubes.add(cube1);
		cubes.add(cube2);
		cubes.add(cube3);*/
		Cube cube = new Cube(new Vector3f(0, -10, -50), new Vector3f(0, 0, 0), 5);
		cubes.add(cube);
	}
	
	public void setUpRender() {
		try {
			DisplayManager.initialize(70.0f, 0.002f, 500.0f);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	public void loadResources() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		shader = ShaderManager.generateShaders();
		ShaderManager.initializeShader(shader, "com/nemezor/nemgine/tests/vertex.shader", "com/nemezor/nemgine/tests/fragment.shader", new String[] {"projection", "translation", "light"}, new String[] {"vertex", "texCoords", "normal"}, new int[] {0, 1, 2});
		ShaderManager.bindShader(shader);
		ShaderManager.loadMatrix4(shader, "projection", NemGL.getCurrentPerspectiveProjectionMatrix());
		ShaderManager.loadMatrix4(shader, "translation", new Matrix4f());
		ShaderManager.loadVector3(shader, "light", new Vector3f(-100, 50, 10));
		ShaderManager.unbindShader();
		model = ModelManager.generateModels();
		ModelManager.initializeModel(model, "com/nemezor/nemgine/tests/dragon.obj");
	}
	
	public void render() {
		if (DisplayManager.closeRequested()) {
			Nemgine.shutDown();
		}
		DisplayManager.resize();
		DisplayManager.prepareRender();
		
		Matrix4f projection = NemGL.getCurrentPerspectiveProjectionMatrix();
		
		for (Cube c : cubes) {
			Matrix4f transformation = GLHelper.initTransformationMatrix(c.getRenderLocation(), c.getRotation(), c.getRenderSize());
			ModelManager.renderModel(model, Registry.INVALID, shader, transformation, projection, "translation", "projection");
			//c.render();
		}
		ModelManager.finishRendering();
		
		DisplayManager.finishRender();
	}
	
	public void cleanUpRender() {
		Nemgine.exit(0);
	}
	
	public void updateRenderSecond(int frames, long interval) {
		DisplayManager.changeTitle("Physics Prototype Test Game | FPS: " + frames);
	}
	
	public void setUpTick() {
		
	}
	
	int startCounter = 0;
	
	public void tick() {
		for (Cube c : cubes) {
			c.getRotation().y += 0.05f;
		}
		if (startCounter > 2) {
			for (Cube c : cubes) {
				c.setLocation(Vector3f.add(c.getLocation(), c.getVelocity(), null));
				c.wasChecked = false;
			}
			for (Cube c : cubes) {
				for (Cube c2 : cubes) {
					if (!c.equals(c2) && !c2.wasChecked) {
						collide(c, c2);
					}
				}
			}
		}
	}
	
	private void collide(Cube cube1, Cube cube2) {
		Vector3f pos1 = Vector3f.add(cube1.getLocation(), cube1.getVelocity(), null);
		Vector3f pos2 = Vector3f.add(cube2.getLocation(), cube2.getVelocity(), null);
		float size1 = cube1.getSize();
		float size2 = cube2.getSize();
		
		boolean collide = true;
		
		if (pos1.getX() + size1 < pos2.getX()) {
			collide = false;
		}
		if (pos1.getX() > pos2.getX() + size2) {
			collide = false;
		}
		if (pos1.getY() - size1 > pos2.getY()) {
			collide = false;
		}
		if (pos1.getY() < pos2.getY() - size2) {
			collide = false;
		}
		if (pos1.getZ() - size1 > pos2.getZ()) {
			collide = false;
		}
		if (pos1.getZ() < pos2.getZ() - size2) {
			collide = false;
		}
		
		if (collide) {
			Vector3f vel1 = cube1.getVelocity();
			Vector3f vel2 = cube2.getVelocity();
			
			float xDiff = MathHelper.difference(pos1.getX(), pos2.getX());
			float yDiff = MathHelper.difference(pos1.getY(), pos2.getY());
			float zDiff = MathHelper.difference(pos1.getZ(), pos2.getZ());
			float max = Math.max(xDiff, Math.max(yDiff, zDiff));
			
			if (max == xDiff) {
				vel1.setX(-vel1.getX());
				vel2.setX(-vel2.getX());
				pos1.setX(pos2.getX() + (pos2.getX() > pos1.getX() ? -size1 : size2));
				cube1.setLocation(pos1);
			}
			if (max == yDiff) {
				vel1.setY(-vel1.getY());
				vel2.setY(-vel2.getY());
				pos1.setY(pos2.getY() + (pos2.getY() > pos1.getY() ? -size2 : size1));
				cube1.setLocation(pos1);
			}
			if (max == zDiff) {
				vel1.setZ(-vel1.getZ());
				vel2.setZ(-vel2.getZ());
				pos1.setZ(pos2.getZ() + (pos2.getZ() > pos1.getZ() ? -size2 : size1));
				cube1.setLocation(pos1);
			}
			
			cube1.setVelocity(vel1);
			cube2.setVelocity(vel2);
		}
		cube1.wasChecked = true;
	}
	
	public void cleanUpTick() {
	
	}
	
	public void updateTickSecond(int ticks, long interval) {
		startCounter++;
	}
	
	public long getTickSleepInterval() {
		return 50;
	}
	
	public int getTickTickskipTreshold() {
		return 5;
	}
	
	public long getRenderSleepInterval() {
		return 16;
	}
	
	public int getRenderFrameskipTreshold() {
		return 10;
	}
	
	public static void main(String[] args) {
		Nemgine.start(args, PhysicsPrototypeGame.class);
	}
}
