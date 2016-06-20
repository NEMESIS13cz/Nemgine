package com.nemezor.nemgine.physics;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import com.nemezor.nemgine.graphics.ShaderManager;
import com.nemezor.nemgine.misc.Color;

public class Collider {

	private static ArrayList<Body> bodies = new ArrayList<Body>();
	
	public static Body addBody(ArrayList<Vector3f> vecs, Vector3f normal) {
		Body b = new Body(vecs, normal);
		bodies.add(b);
		return b;
	}
	
	public static Body addBody(ArrayList<Vector3f> vecs, int shader, Color color, Vector3f normal) {
		Body b = new Body(vecs, normal);
		b.color = color.clone();
		b.shader = shader;
		bodies.add(b);
		return b;
	}
	
	public static void clearAll() {
		bodies.clear();
	}
	
	public static void render(int overrideShader, Color overrideColor, String overrideColorName) {
		if (overrideColor != null) {
			ShaderManager.loadVector4(overrideShader, overrideColorName, overrideColor.getColorAsVector());
		}
		for (Body b : bodies) {
			b.render(overrideColor);
		}
	}
	
	public static void tick() {
		for (Body b : bodies) {
			if (b.stationary) {
				continue;
			}
			for (int i = 0; i < b.points.size(); i += 3) {
				Vector3f p1 = b.points.get(i);
				Vector3f p2 = b.points.get(i + 1);
				Vector3f p3 = b.points.get(i + 2);
				Vector3f np1 = new Vector3f(p1);
				Vector3f np2 = new Vector3f(p2);
				Vector3f np3 = new Vector3f(p3);
				
				// physics
				
				np1.y -= 0.1f;
				np2.y -= 0.1f;
				np3.y -= 0.1f;
				
				// collision and reaction
				
				
				
				p1.x = np1.x;
				p1.y = np1.y;
				p1.z = np1.z;
				p2.x = np2.x;
				p2.y = np2.y;
				p2.z = np2.z;
				p3.x = np3.x;
				p3.y = np3.y;
				p3.z = np3.z;
			}
		}
	}
}
