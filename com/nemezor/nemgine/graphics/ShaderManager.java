package com.nemezor.nemgine.graphics;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Iterator;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix2f;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.nemezor.nemgine.exceptions.ShaderException;
import com.nemezor.nemgine.graphics.util.Shader;
import com.nemezor.nemgine.main.Nemgine;
import com.nemezor.nemgine.misc.EnumShaderType;
import com.nemezor.nemgine.misc.Logger;
import com.nemezor.nemgine.misc.Registry;
import com.nemezor.nemgine.misc.Side;

public class ShaderManager {

	private static HashMap<Integer, Shader> shaders = new HashMap<Integer, Shader>();
	private static int shaderCounter = 0;
	private static int currentShader = 0;
	private static Shader currentShaderData = null;
	
	private static int colorShader;
	private static int textureShader;
	private static int fontShader;
	
	private ShaderManager() {}

	public static synchronized int generateShaders() {
		if (Nemgine.getSide() == Side.SERVER) {
			return Registry.INVALID;
		}
		shaderCounter++;
		shaders.put(shaderCounter, new Shader());
		if (Loader.loading()) {
			Loader.shaderCounter++;
		}
		return shaderCounter;
	}

	public static void dispose(int id) {
		if (Nemgine.getSide() == Side.SERVER) {
			return;
		}
		if (currentShader == id) {
			GL20.glUseProgram(0);
		}
		Shader shader = shaders.get(id);
		if (shader == null) {
			return;
		}
		GL20.glDetachShader(shader.progID, shader.vertID);
		GL20.glDetachShader(shader.progID, shader.fragID);
		GL20.glDeleteShader(shader.vertID);
		GL20.glDeleteShader(shader.fragID);
		GL20.glDeleteProgram(shader.progID);
		shaders.remove(id);
	}

	public static void disposeAll() {
		if (Nemgine.getSide() == Side.SERVER) {
			return;
		}
		GL20.glUseProgram(0);
		Iterator<Integer> keys = shaders.keySet().iterator();
		while (keys.hasNext()) {
			Shader shader = shaders.get(keys.next());
			GL20.glDetachShader(shader.progID, shader.vertID);
			GL20.glDetachShader(shader.progID, shader.fragID);
			GL20.glDeleteShader(shader.vertID);
			GL20.glDeleteShader(shader.fragID);
			GL20.glDeleteProgram(shader.progID);
		}
		shaders.clear();
	}

	public static void bindShader(int id) {
		if (currentShader == id) {
			return;
		}
		Shader shader = shaders.get(id);
		if (shader == null) {
			return;
		}
		currentShader = id;
		currentShaderData = shader;
		GL20.glUseProgram(shader.progID);
	}

	public static void unbindShader() {
		if (currentShader == 0 || Nemgine.getSide() == Side.SERVER) {
			return;
		}
		currentShader = 0;
		currentShaderData = null;
		GL20.glUseProgram(0);
	}

	public static void loadMatrix4(int id, String name, Matrix4f data) {
		if (id != currentShader || Nemgine.getSide() == Side.SERVER) {
			return;
		}
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		data.store(buffer);
		buffer.flip();
		GL20.glUniformMatrix4fv(currentShaderData.data.get(name), false, buffer);
	}

	public static void loadMatrix3(int id, String name, Matrix3f data) {
		if (id != currentShader || Nemgine.getSide() == Side.SERVER) {
			return;
		}
		FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
		data.store(buffer);
		buffer.flip();
		GL20.glUniformMatrix3fv(currentShaderData.data.get(name), false, buffer);
	}

	public static void loadMatrix2(int id, String name, Matrix2f data) {
		if (id != currentShader || Nemgine.getSide() == Side.SERVER) {
			return;
		}
		FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
		data.store(buffer);
		buffer.flip();
		GL20.glUniformMatrix2fv(currentShaderData.data.get(name), false, buffer);
	}

	public static void loadVector4(int id, String name, Vector4f data) {
		if (id != currentShader || Nemgine.getSide() == Side.SERVER) {
			return;
		}
		GL20.glUniform4f(currentShaderData.data.get(name), data.getX(), data.getY(), data.getZ(), data.getW());
	}

	public static void loadVector3(int id, String name, Vector3f data) {
		if (id != currentShader || Nemgine.getSide() == Side.SERVER) {
			return;
		}
		GL20.glUniform3f(currentShaderData.data.get(name), data.getX(), data.getY(), data.getZ());
	}

	public static void loadVector2(int id, String name, Vector2f data) {
		if (id != currentShader || Nemgine.getSide() == Side.SERVER) {
			return;
		}
		GL20.glUniform2f(currentShaderData.data.get(name), data.getX(), data.getY());
	}

	public static void loadFloat(int id, String name, float data) {
		if (id != currentShader || Nemgine.getSide() == Side.SERVER) {
			return;
		}
		GL20.glUniform1f(currentShaderData.data.get(name), data);
	}

	public static void loadBoolean(int id, String name, boolean data) {
		if (id != currentShader || Nemgine.getSide() == Side.SERVER) {
			return;
		}
		GL20.glUniform1f(currentShaderData.data.get(name), data ? 1.0f : 0.0f);
	}
	
	public static void loadVector4Array(int id, String name, Vector4f[] array) {
		if (id != currentShader || Nemgine.getSide() == Side.SERVER) {
			return;
		}
		FloatBuffer buffer = BufferUtils.createFloatBuffer(array.length * 4);
		float[] data = new float[array.length * 4];
		for (int i = 0; i < array.length; i++) {
			data[i * 4] = array[i].getX();
			data[i * 4 + 1] = array[i].getY();
			data[i * 4 + 2] = array[i].getZ();
			data[i * 4 + 3] = array[i].getW();
		}
		buffer.put(data);
		buffer.flip();
		ARBShaderObjects.glUniform1fvARB(currentShaderData.data.get(name), buffer);
	}
	
	public static void loadVector3Array(int id, String name, Vector3f[] array) {
		if (id != currentShader || Nemgine.getSide() == Side.SERVER) {
			return;
		}
		FloatBuffer buffer = BufferUtils.createFloatBuffer(array.length * 3);
		float[] data = new float[array.length * 3];
		for (int i = 0; i < array.length; i++) {
			data[i * 3] = array[i].getX();
			data[i * 3 + 1] = array[i].getY();
			data[i * 3 + 2] = array[i].getZ();
		}
		buffer.put(data);
		buffer.flip();
		ARBShaderObjects.glUniform1fvARB(currentShaderData.data.get(name), buffer);
	}
	
	public static void loadVector2Array(int id, String name, Vector2f[] array) {
		if (id != currentShader || Nemgine.getSide() == Side.SERVER) {
			return;
		}
		FloatBuffer buffer = BufferUtils.createFloatBuffer(array.length * 2);
		float[] data = new float[array.length * 2];
		for (int i = 0; i < array.length; i++) {
			data[i * 2] = array[i].getX();
			data[i * 2 + 1] = array[i].getY();
		}
		buffer.put(data);
		buffer.flip();
		ARBShaderObjects.glUniform1fvARB(currentShaderData.data.get(name), buffer);
	}
	
	public static void loadFloatArray(int id, String name, float[] array) {
		if (id != currentShader || Nemgine.getSide() == Side.SERVER) {
			return;
		}
		FloatBuffer buffer = BufferUtils.createFloatBuffer(array.length * 2);
		buffer.put(array);
		buffer.flip();
		ARBShaderObjects.glUniform1fvARB(currentShaderData.data.get(name), buffer);
	}

	public static boolean initializeShader(int id, String vertexFile, String fragmentFile, String[] uniforms, String[] attribNames, int[] attribBinds) {
		if (attribBinds.length != attribNames.length) {
			return false;
		}
		if (currentShader == id) {
			return false;
		}
		Shader shader = shaders.get(id);
		if (shader == null) {
			return false;
		}
		if (Loader.loading()) {
			Loader.loadingShader(new File(vertexFile).getName());
		}
		shader.vertID = loadShader(vertexFile, EnumShaderType.VERTEX);
		if (shader.vertID == Registry.INVALID) {
			ShaderException ex = new ShaderException(Registry.SHADER_MANAGER_LOADER_GLOBAL_ERROR);
			ex.setShaderInfo(vertexFile, EnumShaderType.VERTEX);
			ex.setThrower(Registry.SHADER_MANAGER_NAME);
			ex.printStackTrace();
			if (Loader.loading()) { 
				Loader.failedToLoadResource(Registry.LOADING_SCREEN_ERROR);
			}
			return false;
		}
		if (Loader.loading()) {
			Loader.stepLoader();
		}
		shader.fragID = loadShader(fragmentFile, EnumShaderType.FRAGMENT);
		if (shader.fragID == Registry.INVALID) {
			ShaderException ex = new ShaderException(Registry.SHADER_MANAGER_LOADER_GLOBAL_ERROR);
			ex.setShaderInfo(fragmentFile, EnumShaderType.FRAGMENT);
			ex.setThrower(Registry.SHADER_MANAGER_NAME);
			ex.printStackTrace();
			if (Loader.loading()) { 
				Loader.failedToLoadResource(Registry.LOADING_SCREEN_ERROR);
			}
			return false;
		}
		if (Loader.loading()) {
			Loader.stepLoader();
		}
		shader.progID = GL20.glCreateProgram();
		GL20.glAttachShader(shader.progID, shader.vertID);
		GL20.glAttachShader(shader.progID, shader.fragID);
		for (int i = 0; i < attribNames.length; i++) {
			GL20.glBindAttribLocation(shader.progID, attribBinds[i], attribNames[i]);
		}
		GL20.glLinkProgram(shader.progID);
		GL20.glValidateProgram(shader.progID);
		for (String uni : uniforms) {
			int uniId = GL20.glGetUniformLocation(shader.progID, uni);
			shader.data.put(uni, uniId);
		}
		if (Loader.loading()) {
			Loader.shaderLoaded();
		}
		return true;
	}

	private static int loadShader(String file, EnumShaderType type) {
		StringBuilder shaderSrc = new StringBuilder();
		try {
			InputStream stream = Nemgine.class.getResourceAsStream("/" + file);
			if (stream == null) {
				return Registry.INVALID;
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSrc.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			ShaderException ex = new ShaderException(Registry.SHADER_LOADER_LOAD_ERROR);
			ex.setShaderInfo(file, type);
			ex.setThrower(Registry.SHADER_LOADER_NAME);
			ex.printStackTrace();
			e.printStackTrace();
			return Registry.INVALID;
		}
		int shaderID = GL20.glCreateShader(type.getGlShaderType());
		GL20.glShaderSource(shaderID, shaderSrc);
		GL20.glCompileShader(shaderID);
		if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			ShaderException ex = new ShaderException(Registry.SHADER_LOADER_COMPILE_ERROR);
			ex.setShaderInfo(file, type);
			ex.setThrower(Registry.SHADER_LOADER_NAME);
			ex.printStackTrace();
			Logger.log(null, GL20.glGetShaderInfoLog(shaderID, Registry.SHADER_ERROR_LOG_SIZE), false);
			return Registry.INVALID;
		}
		return shaderID;
	}
	
	protected static int loadLogoShaders() {
		Loader.silent = true;
		int id = generateShaders();
		initializeShader(id, Registry.TEXTURE_SHADER_VERTEX, Registry.TEXTURE_SHADER_FRAGMENT, new String[] {"projection", "transformation", "color"}, new String[] {"position"}, new int[] {0});
		Loader.silent = false;
		return id;
	}
	
	protected static int loadProgressBarShaders() {
		Loader.silent = true;
		int id = generateShaders();
		fontShader = generateShaders();
		initializeShader(id, Registry.SHADER_PROGRESS_BAR_VERTEX, Registry.SHADER_PROGRESS_BAR_FRAGMENT, new String[] {"projection", "transformation", "progress"}, new String[] {"position"}, new int[] {0});
		initializeShader(fontShader, Registry.TEXTURE_SHADER_VERTEX, Registry.FONT_SHADER_FRAGMENT, new String[] {"projection", "transformation", "color"}, new String[] {"position"}, new int[] {0});
		Loader.silent = false;
		return id;
	}
	
	protected static void generateDefaultShaderIDs() {
		colorShader = generateShaders();
		textureShader = generateShaders();
	}
	
	protected static void loadDefaultShaders() {
		initializeShader(colorShader, Registry.COLOR_SHADER_VERTEX, Registry.COLOR_SHADER_FRAGMENT, new String[] {"projection", "transformation", "color"}, new String[] {"position"}, new int[] {0});
		initializeShader(textureShader, Registry.TEXTURE_SHADER_VERTEX, Registry.TEXTURE_SHADER_FRAGMENT, new String[] {"projection", "transformation", "color"}, new String[] {"position"}, new int[] {0});
	}
	
	public static int getColorShaderID() {
		return colorShader;
	}
	
	public static int getTextureShaderID() {
		return textureShader;
	}
	
	public static int getFontShaderID() {
		return fontShader;
	}
}
