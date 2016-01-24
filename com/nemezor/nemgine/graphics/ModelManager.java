package com.nemezor.nemgine.graphics;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.IntBuffer;
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

import com.nemezor.nemgine.exceptions.ModelException;
import com.nemezor.nemgine.graphics.util.Model;
import com.nemezor.nemgine.graphics.util.ModelData;
import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.misc.Color;
import com.nemezor.nemgine.misc.Registry;
import com.nemezor.nemgine.misc.Side;

public class ModelManager {

	private static HashMap<Integer, Model> models = new HashMap<Integer, Model>();
	private static int modelCounter = 0;
	private static int squareModel = 0;
	
	private ModelManager() {}
	
	public static synchronized int generateModels() {
		if (Nemgine.getSide() == Side.SERVER) {
			return Registry.INVALID;
		}
		modelCounter++;
		models.put(modelCounter, new Model(false));
		if (Loader.loading()) {
			Loader.modelCounter++;
		}
		return modelCounter;
	}
	
	public static void dispose(int id) {
		Model model = models.get(id);
		if (model == null) {
			return;
		}
		if (model.init) {
			model.dispose();
		}
		models.remove(id);
	}
	
	public static void disposeAll() {
		if (Nemgine.getSide() == Side.SERVER) {
			return;
		}
		Iterator<Integer> keys = models.keySet().iterator();
		
		for (int id : DisplayManager.getAllIds()) {
			DisplayManager.switchDisplay(id);
			while (keys.hasNext()) {
				Model model = models.get(keys.next());
				if (model == null) {
					continue;
				}
				if (model.init) {
					model.dispose();
				}
			}
		}
		models.clear();
	}
	
	public static void renderModelWithColor(int id, int textureID, int shaderID, Matrix4f transformation, Matrix4f projection, Color color, String transformationAttribName, String projectionAttribName, String colorAttribName) {
		if (Nemgine.isInCompatibilityMode() && FrameBufferManager.inFrameBuffer) {
			return;
		}
		if (color.getAlpha() != 1.0f) {
			GLHelper.enableBlending();
		}
		ShaderManager.bindShader(shaderID);
		ShaderManager.loadVector4(shaderID, colorAttribName, color.getColorAsVector());
		renderModel(id, textureID, shaderID, transformation, projection, transformationAttribName, projectionAttribName);
		if (color.getAlpha() != 1.0f) {
			GLHelper.disableBlending();
		}
	}
	
	public static void renderModelWithFrameBufferTexture(int id, int frameBuffer, int texture, int shaderID, Matrix4f transformation, Matrix4f projection, String transformationAttribName, String projectionAttribName) {
		Model model = models.get(id);
		if (model == null || !model.init || Nemgine.isInCompatibilityMode()) {
			return;
		}
		ShaderManager.bindShader(shaderID);
		ShaderManager.loadMatrix4(shaderID, projectionAttribName, projection);
		ShaderManager.loadMatrix4(shaderID, transformationAttribName, transformation);
		GL30.glBindVertexArray(model.id.get(DisplayManager.getCurrentDisplayID()));
		GL20.glEnableVertexAttribArray(0);
		if (model.isTextured()) {
			GL20.glEnableVertexAttribArray(1);
		}
		GL20.glEnableVertexAttribArray(2);
		if (model.isTextured()) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			FrameBufferManager.bindFrameBufferTexture(frameBuffer, texture);
		}
		GL11.glDrawElements(GL11.GL_TRIANGLES, model.data.indices.length, GL11.GL_UNSIGNED_INT, 0);
		GL20.glDisableVertexAttribArray(0);
		if (model.isTextured()) {
			GL20.glDisableVertexAttribArray(1);
			FrameBufferManager.unbindFrameBufferTexture();
		}
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}
	
	public static void renderModel(int id, int textureID, int shaderID, Matrix4f transformation, Matrix4f projection, String transformationAttribName, String projectionAttribName) {
		if (Nemgine.isInCompatibilityMode() && FrameBufferManager.inFrameBuffer) {
			return;
		}
		Model model = models.get(id);
		if (model == null || !model.init) {
			return;
		}
		ShaderManager.bindShader(shaderID);
		ShaderManager.loadMatrix4(shaderID, projectionAttribName, projection);
		ShaderManager.loadMatrix4(shaderID, transformationAttribName, transformation);
		GL30.glBindVertexArray(model.id.get(DisplayManager.getCurrentDisplayID()));
		GL20.glEnableVertexAttribArray(0);
		if (model.isTextured()) {
			GL20.glEnableVertexAttribArray(1);
		}
		GL20.glEnableVertexAttribArray(2);
		if (model.isTextured()) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			TextureManager.bindTexture(textureID);
		}
		GL11.glDrawElements(GL11.GL_TRIANGLES, model.data.indices.length, GL11.GL_UNSIGNED_INT, 0);
		GL20.glDisableVertexAttribArray(0);
		if (model.isTextured()) {
			GL20.glDisableVertexAttribArray(1);
		}
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}
	
	public static void finishRendering() {
		if (Nemgine.getSide() == Side.SERVER || (Nemgine.isInCompatibilityMode() && FrameBufferManager.inFrameBuffer)) {
			return;
		}
		ShaderManager.unbindShader();
		TextureManager.unbindTexture();
		FrameBufferManager.unbindFrameBufferTexture();
	}
	
	protected static void reloadModels() {
		Iterator<Integer> keys = models.keySet().iterator();
		
		while (keys.hasNext()) {
			Model model = models.get(keys.next());
			if (model == null) {
				continue;
			}
			if (model.init) {
				reloadModel(model);
			}
		}
	}
	
	protected static void reloadModel(Model model) {
		ModelData data = model.getData();
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
		
		model.addDisplayResources(VAOid, buffers);
	}
	
	public static boolean initializeModel(int id, String file) {
		return initializeModelInternal(id, file, true);
	}
	
	public static boolean initializeModelForceNoTexture(int id, String file) {
		return initializeModelInternal(id, file, false);
	}
	
	private static boolean initializeModelInternal(int id, String file, boolean textured) {
		Model model = models.get(id);
		if (model == null || model.init) {
			return false;
		}
		if (Loader.loading()) {
			Loader.loadingModel(new File(file).getName());
		}
		ModelData data = loadModel(file, textured);
		if (data == null) {
			ModelException ex = new ModelException(Registry.MODEL_MANAGER_LOADER_GLOBAL_ERROR);
			ex.setThrower(Registry.MODEL_MANAGER_NAME);
			ex.setModelInfo(file);
			ex.printStackTrace();
			if (Loader.loading()) {
				Loader.failedToLoadResource(Registry.LOADING_SCREEN_ERROR);
			}
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
		
		if (Loader.loading()) {
			Loader.modelLoaded();
		}
		return true;
	}
	
	private static ModelData loadModel(String file, boolean textured) {
		try {
			InputStream stream = Nemgine.class.getResourceAsStream("/" + file);
			if (stream == null) {
				return null;
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			
			String buffer = "";
			ArrayList<String> lines = new ArrayList<String>();
			ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
			ArrayList<Vector2f> textures = new ArrayList<Vector2f>();
			ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
			ArrayList<Integer> indices = new ArrayList<Integer>();
			float[] verticesArray = null;
			float[] texturesArray = null;
			float[] normalsArray = null;
			int[] indicesArray = null;
			
			while (buffer != null) {
				buffer = reader.readLine();
				lines.add(buffer);
			}
			reader.close();
			
			for (String buf : lines) {
				if (buf == null) {
					continue;
				}
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
				}
			}
			
			texturesArray = new float[vertices.size() * 2];
			normalsArray = new float[vertices.size() * 3];

			for (String buf : lines) {
				if (buf == null || !buf.startsWith("f ")) {
					continue;
				}
				String[] current = buf.split(" ");
				String[] vertex1 = current[1].split("/");
				String[] vertex2 = current[2].split("/");
				String[] vertex3 = current[3].split("/");
				
				processVertex(vertex1, indices, textures, normals, texturesArray, normalsArray, textured);
				processVertex(vertex2, indices, textures, normals, texturesArray, normalsArray, textured);
				processVertex(vertex3, indices, textures, normals, texturesArray, normalsArray, textured);
			}
			
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
			ModelException ex = new ModelException(Registry.MODEL_LOADER_NOT_FOUND);
			ex.setThrower(Registry.MODEL_LOADER_NAME);
			ex.setModelInfo(file);
			ex.printStackTrace();
			return null;
		}
	}
	
	private static void processVertex(String[] vertexData, ArrayList<Integer> indices, ArrayList<Vector2f> textures, ArrayList<Vector3f> normals, float[] textureArray, float[] normalsArray, boolean textured) {
		int current = vertexData[0].equals("") ? 0 : (Integer.parseInt(vertexData[0]) - 1);
		indices.add(current);
		if (textures.size() > 0 && textured) {
			Vector2f currentTex = textures.get(vertexData[1].equals("") ? 0 : (Integer.parseInt(vertexData[1]) - 1));
			textureArray[current * 2] = currentTex.getX();
			textureArray[current * 2 + 1] = 1 - currentTex.getY();
		}
		if (normals.size() == 0) {
			return;
		}
		Vector3f currentNorm = normals.get((vertexData.length < 3 || vertexData[2].equals("")) ? 0 : (Integer.parseInt(vertexData[2]) - 1));
		normalsArray[current * 3] = currentNorm.getX();
		normalsArray[current * 3 + 1] = currentNorm.getY();
		normalsArray[current * 3 + 2] = currentNorm.getZ();
	}
	
	protected static void initializeSquareModel() {
		squareModel = generateModels();
		if (!initializeModel(squareModel, Registry.MODEL_SQUARE)) {
			Loader.failedToLoadResource(Registry.LOADING_SCREEN_ERROR);
		}
	}
	
	public static int getSquareModelID() {
		return squareModel;
	}
}
