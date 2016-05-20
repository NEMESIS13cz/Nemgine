package com.nemezor.nemgine.physics;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import com.nemezor.nemgine.misc.Color;

public class Collider {

	private static ArrayList<Body> bodies = new ArrayList<Body>();
	
	public static Body addBody(ArrayList<Vector3f> vecs) {
		Body b = new Body(vecs);
		bodies.add(b);
		return b;
	}
	
	public static Body addBody(ArrayList<Vector3f> vecs, int shader, Color color) {
		Body b = new Body(vecs);
		b.color = color.clone();
		b.shader = shader;
		bodies.add(b);
		return b;
	}
	
	public static void render() {
		for (Body b : bodies) {
			b.render();
		}
	}
	
	public static void tick() {
		for (Body b : bodies) {
			if (b.stationary) {
				continue;
			}
			for (Vector3f vec : b.points) {
				Vector3f newPos = new Vector3f(vec);
				newPos.y -= 0.1f;
				
			}
		}
	}
}
