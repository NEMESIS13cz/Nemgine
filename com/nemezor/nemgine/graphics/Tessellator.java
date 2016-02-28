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

	private static HashMap<Integer, ArrayList<TessObject>> objects = new HashMap<Integer, ArrayList<TessObject>>();
	private static HashMap<Integer, Integer> index = new HashMap<Integer, Integer>();
	private static ArrayList<Float> currentVertex = new ArrayList<Float>();
	private static ArrayList<Float> currentTexture = new ArrayList<Float>();
	private static ArrayList<Float> currentNormal = new ArrayList<Float>();
	private static int current = 0;
	private static boolean tessellating = false;
	
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
		
		Iterator<Integer> iter = index.keySet().iterator();
		while (iter.hasNext()) {
			index.put(iter.next(), 0);
		}
	}
	
	public static void start() {
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
		float[] verData = Utils.toPrimitiveArray(currentVertex);
		float[] texData = Utils.toPrimitiveArray(currentTexture);
		float[] norData = Utils.toPrimitiveArray(currentNormal);
		
		ModelData data = new ModelData(verData, norData, texData, Utils.range(0, verData.length));
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
		obj.render();
		
		index.put(DisplayManager.getCurrentDisplayID(), index.get(DisplayManager.getCurrentDisplayID()) + 1);
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
	
	public static void addVertex(float x, float y, float z) {
		currentVertex.add(x);
		currentVertex.add(y);
		currentVertex.add(z);
	}
}
