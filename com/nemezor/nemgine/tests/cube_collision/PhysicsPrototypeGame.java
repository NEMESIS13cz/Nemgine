package com.nemezor.nemgine.tests.cube_collision;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import com.nemezor.nemgine.debug.ImmediateRender;
import com.nemezor.nemgine.main.Application;
import com.nemezor.nemgine.main.IMainRenderLoop;
import com.nemezor.nemgine.main.IMainTickLoop;
import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.misc.MathHelper;

public class PhysicsPrototypeGame implements IMainRenderLoop, IMainTickLoop {
	
	private ArrayList<Cube> cubes = new ArrayList<Cube>();
	
	@Application(width = 1280, height = 720, name = "Physics Prototype Test Game", path = "tests/cube_collision", contained = false)
	public void AppEntryPoint() {
		int id = Nemgine.generateThreads("Main Thread", true);
		Nemgine.bindRenderLoop(id, this);
		Nemgine.bindTickLoop(id, this);
		Nemgine.startThread(id);
		Cube cube1 = new Cube(new Vector3f(-25, -5, -50), new Vector3f(0.5f, 0.2f, 0), 5);
		Cube cube2 = new Cube(new Vector3f(20, 0, -50), new Vector3f(-0.5f, 0, 0), 10);
		Cube cube3 = new Cube(new Vector3f(-20, 14, -50), new Vector3f(0, 0, 0), 7);
		
		cubes.add(cube1);
		cubes.add(cube2);
		cubes.add(cube3);
	}
	
	public void setUpRender() {
		ImmediateRender.initialize();
	}
	
	public void loadResources() {
		
	}
	
	public void render() {
		if (ImmediateRender.closeRequested()) {
			Nemgine.shutDown();
		}
		ImmediateRender.resize();
		ImmediateRender.prepareRender();
		
		for (Cube c : cubes) {
			c.render();
		}
		
		ImmediateRender.finishRender();
	}
	
	public void cleanUpRender() {
		Nemgine.exit(0);
	}
	
	public void updateRenderSecond(int frames, long interval) {
		
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
