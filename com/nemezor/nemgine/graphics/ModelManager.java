package com.nemezor.nemgine.graphics;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.nemezor.nemgine.misc.NemgineModelException;
import com.nemezor.nemgine.misc.Registry;

public class ModelManager {

	private static HashMap<Integer, Model> models = new HashMap<Integer, Model>();
	private static int modelCounter = 0;
	
	public static synchronized int generateModels() {
		modelCounter++;
		models.put(modelCounter, new Model(Registry.INVALID, null, null));
		return modelCounter;
	}
	
	public static void dispose(int id) {
		Model model = models.get(id);
		if (model == null) {
			return;
		}
		if (model.id != Registry.INVALID) {
			model.dispose();
		}
		models.remove(id);
	}
	
	public static void disposeAll() {
		Iterator<Integer> keys = models.keySet().iterator();
		
		while (keys.hasNext()) {
			Model model = models.get(keys.next());
			if (model == null) {
				continue;
			}
			if (model.id != Registry.INVALID) {
				model.dispose();
			}
		}
		models.clear();
	}
	
	public static void renderModel(int id, int textureID, int shaderID, Matrix4f transformation, Matrix4f projection, String transformationAttribName, String projectionAttribName) {
		Model model = models.get(id);
		if (model == null || model.id == Registry.INVALID) {
			return;
		}
		ShaderManager.bindShader(shaderID);
		ShaderManager.loadMatrix4(shaderID, projectionAttribName, projection);
		ShaderManager.loadMatrix4(shaderID, transformationAttribName, transformation);
		GL30.glBindVertexArray(model.id);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		TextureManager.bindTexture(textureID);
		GL11.glDrawElements(GL11.GL_TRIANGLES, model.data.indices.length, GL11.GL_UNSIGNED_INT, 0);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}
	
	public static void finishRendering() {
		ShaderManager.unbindShader();
		TextureManager.unbindTexture();
	}
	
	public static boolean initializeModel(int id, String file) {
		Model model = models.get(id);
		if (model == null || model.id != Registry.INVALID) {
			return false;
		}
		ModelData data = loadModel(file);
		if (data == null) {
			NemgineModelException ex = new NemgineModelException(Registry.MODEL_MANAGER_LOADER_GLOBAL_ERROR);
			ex.setThrower(Registry.MODEL_MANAGER_NAME);
			ex.setModelInfo(file);
			ex.printStackTrace();
			return false;
		}
		
		int VAOid = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(VAOid);
		IntBuffer ind = BufferUtils.createIntBuffer(data.indices.length);
		ind.put(data.indices);
		ind.flip();
		int indVBOid = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indVBOid);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, ind, GL15.GL_STATIC_DRAW);
		int verVBOid = GLHelper.createBufferAndStore(0, 3, data.vertices);
		int texVBOid = GLHelper.createBufferAndStore(1, 2, data.textures);
		int norVBOid = GLHelper.createBufferAndStore(2, 3, data.normals);
		GL30.glBindVertexArray(0);
		int[] buffers = new int[] {indVBOid, verVBOid, texVBOid, norVBOid};
		
		models.put(id, new Model(VAOid, buffers, data));
		return true;
	}
	
	private static ModelData loadModel(String file) {
		try {
			BufferedReader reader = Files.newBufferedReader(Paths.get(ClassLoader.getSystemResource(file).getPath()));
			
			String buf = "";
			ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
			ArrayList<Vector2f> textures = new ArrayList<Vector2f>();
			ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
			ArrayList<Integer> indices = new ArrayList<Integer>();
			float[] verticesArray = null;
			float[] texturesArray = null;
			float[] normalsArray = null;
			int[] indicesArray = null;
			
			while (true) {
				buf = reader.readLine();
				String[] current = buf.split(" ");
				
				if (buf.startsWith("v ")) {
					Vector3f vertex = new Vector3f(Float.parseFloat(current[1]), Float.parseFloat(current[2]), Float.parseFloat(current[3]));
					vertices.add(vertex);
				}else if (buf.startsWith("vt ")) {
					Vector2f texture = new Vector2f(Float.parseFloat(current[1]), Float.parseFloat(current[2]));
					textures.add(texture);
				}else if (buf.startsWith("vn ")) {
					Vector3f normal = new Vector3f(Float.parseFloat(current[1]), Float.parseFloat(current[2]), Float.parseFloat(current[3]));
					normals.add(normal);
				}else if (buf.startsWith("f ")) {
					texturesArray = new float[vertices.size() * 2];
					normalsArray = new float[vertices.size() * 3];
					break;
				}
			}
			
			while (buf != null) {
				if (!buf.startsWith("f ")) {
					buf = reader.readLine();
					continue;
				}
				String[] current = buf.split(" ");
				String[] vertex1 = current[1].split("/");
				String[] vertex2 = current[2].split("/");
				String[] vertex3 = current[3].split("/");
				
				processVertex(vertex1, indices, textures, normals, texturesArray, normalsArray);
				processVertex(vertex2, indices, textures, normals, texturesArray, normalsArray);
				processVertex(vertex3, indices, textures, normals, texturesArray, normalsArray);
				buf = reader.readLine();
			}
			
			reader.close();
			
			verticesArray = new float[vertices.size() * 3];
			indicesArray = new int[indices.size()];
			
			int vertexPointer = 0;
			for (Vector3f vertex : vertices) {
				verticesArray[vertexPointer++] = vertex.getX();
				verticesArray[vertexPointer++] = vertex.getY();
				verticesArray[vertexPointer++] = vertex.getZ();
			}
			for (int i = 0; i < indices.size(); i++) {
				indicesArray[i] = indices.get(i);
			}
			return new ModelData(verticesArray, normalsArray, texturesArray, indicesArray);
		} catch (IOException e) {
			NemgineModelException ex = new NemgineModelException(Registry.MODEL_LOADER_NOT_FOUND);
			ex.setThrower(Registry.MODEL_LOADER_NAME);
			ex.setModelInfo(file);
			ex.printStackTrace();
			return null;
		}
	}
	
	private static void processVertex(String[] vertexData, ArrayList<Integer> indices, ArrayList<Vector2f> textures, ArrayList<Vector3f> normals, float[] textureArray, float[] normalsArray) {
		int current = Integer.parseInt(vertexData[0]) - 1;
		indices.add(current);
		Vector2f currentTex = textures.get(Integer.parseInt(vertexData[1]) - 1);
		textureArray[current * 2] = currentTex.getX();
		textureArray[current * 2 + 1] = 1 - currentTex.getY();
		Vector3f currentNorm = normals.get(Integer.parseInt(vertexData[2]) - 1);
		normalsArray[current * 3] = currentNorm.getX();
		normalsArray[current * 3 + 1] = currentNorm.getY();
		normalsArray[current * 3 + 2] = currentNorm.getZ();
	}
}
