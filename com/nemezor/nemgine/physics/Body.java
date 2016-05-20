package com.nemezor.nemgine.physics;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import com.nemezor.nemgine.graphics.ShaderManager;
import com.nemezor.nemgine.graphics.Tessellator;
import com.nemezor.nemgine.misc.Color;

public class Body {

	protected ArrayList<Vector3f> points = new ArrayList<Vector3f>();
	public Color color = new Color(0xFFFFFFFF);
	public int shader = ShaderManager.getColorShaderID();
	public boolean stationary;
	
	public Body(ArrayList<Vector3f> data) {
		points.addAll(data);
	}
	
	public void render() {
		ShaderManager.loadVector4(shader, "color", color.getColorAsVector());
		Tessellator.start(Tessellator.TRIANGLES);
		
		for (Vector3f v : points) {
			Tessellator.addVertex(v.x, v.y, v.z);
			Tessellator.addTexCoord(0, 0);
		}
		
		Tessellator.finish();
	}
}
