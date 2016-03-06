package com.nemezor.nemgine.graphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.nemezor.nemgine.graphics.util.ModelData;
import com.nemezor.nemgine.graphics.util.TessObject;
import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.misc.Logger;
import com.nemezor.nemgine.misc.Registry;
import com.nemezor.nemgine.misc.Utils;

public class Tessellator {

	public static final int POINTS = 0x1;
	public static final int TRIANGLES = 0x2;
	public static final int QUADS = 0x3;
	public static final int LINES = 0x4;
	
	private static HashMap<Integer, ArrayList<TessObject>> objects = new HashMap<Integer, ArrayList<TessObject>>();
	private static HashMap<Integer, Integer> index = new HashMap<Integer, Integer>();
	private static ArrayList<Float> currentVertex = new ArrayList<Float>();
	private static ArrayList<Float> currentTexture = new ArrayList<Float>();
	private static ArrayList<Float> currentNormal = new ArrayList<Float>();
	private static int current = 0;
	private static boolean tessellating = false;
	private static int mode = 0;
	
	private Tessellator() {}
	
	/**
	 * Do not call this!!!
	 */
	public static void refresh() {
		if (Nemgine.getSide().isServer()) {
			return;
		}
		current = 0;
		currentVertex.clear();
		currentTexture.clear();
		currentNormal.clear();
		mode = 0;
		
		Iterator<Integer> iter = index.keySet().iterator();
		while (iter.hasNext()) {
			index.put(iter.next(), 0);
		}
	}
	
	public static void start(int tessellationMode) {
		if (tessellating) {
			Logger.log(Registry.TESSELLATOR_ALREADY_RUNNING);
			return;
		}
		if (DisplayManager.getCurrentDisplayID() == Registry.INVALID || Nemgine.getSide().isServer()) {
			return;
		}
		
		currentVertex.clear();
		currentTexture.clear();
		currentNormal.clear();
		current = DisplayManager.getCurrentDisplayID();
		mode = tessellationMode;
		tessellating = true;
	}
	
	public static void finish() {
		if (DisplayManager.getCurrentDisplayID() == Registry.INVALID || Nemgine.getSide().isServer()) {
			return;
		}
		if (!tessellating) {
			Logger.log(Registry.TESSELLATOR_NOT_RUNNING);
			return;
		}
		if (current != DisplayManager.getCurrentDisplayID()) {
			Logger.log(Registry.TESSELLATOR_SWITCHED_CONTEXTS);
			return;
		}
		float[] verData = Utils.toPrimitiveArrayf(currentVertex);
		float[] texData = Utils.toPrimitiveArrayf(currentTexture);
		float[] norData = Utils.toPrimitiveArrayf(currentNormal);
		ModelData data = null;
		
		if (mode == Tessellator.QUADS) {
			if (verData.length % 4 != 0) {
				Logger.log(Registry.TESSELLATOR_INVALID_DATA);
				return;
			}
			int[] indices = new int[(int)(verData.length * 1.5f)];
			for (int i = 0, j = 0; i < verData.length; i += 12, j += 6) {
				indices[j] = i;
				indices[j + 1] = i + 1;
				indices[j + 2] = i + 2;
				indices[j + 3] = i + 2;
				indices[j + 4] = i + 3;
				indices[j + 5] = i;
			}
			data = new ModelData(verData, norData, texData, indices);
		}else{
			data = new ModelData(verData, norData, texData, Utils.range(0, verData.length / 3));
		}
		ArrayList<TessObject> objs = objects.get(DisplayManager.getCurrentDisplayID());
		if (objs == null) {
			objs = new ArrayList<TessObject>();
			objects.put(DisplayManager.getCurrentDisplayID(), objs);
		}
		if (index.get(DisplayManager.getCurrentDisplayID()) == null) {
			index.put(DisplayManager.getCurrentDisplayID(), 0);
		}
		int index_ = index.get(DisplayManager.getCurrentDisplayID());
		TessObject obj;
		
		if (index_ >= objs.size()) {
			obj = new TessObject(data);
			objs.add(obj);
		}else{
			obj = objs.get(index_);
			obj.upload(data);
		}
		obj.render(mode);
		
		index.put(DisplayManager.getCurrentDisplayID(), index.get(DisplayManager.getCurrentDisplayID()) + 1);
		mode = 0;
		tessellating = false;
	}
	
	public static void addTexCoord(float u, float v) {
		currentTexture.add(u);
		currentTexture.add(v);
	}
	
	public static void addNormal(float x, float y, float z) {
		currentNormal.add(x);
		currentNormal.add(y);
		currentNormal.add(z);
	}
	
	public static void addVertex(float x, float y) {
		currentVertex.add(x);
		currentVertex.add(y);
		currentVertex.add(0.0f);
	}
	
	public static void addVertex(float x, float y, float z) {
		currentVertex.add(x);
		currentVertex.add(y);
		currentVertex.add(z);
	}
}
