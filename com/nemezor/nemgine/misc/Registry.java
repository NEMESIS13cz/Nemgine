package com.nemezor.nemgine.misc;

import java.awt.Font;

public class Registry {

	public static final int DEFAULT_FRAMESKIP_TRESHOLD = 5;
	public static final int DEFAULT_TICKSKIP_TRESHOLD = 5;
	public static final int MAX_TIME_BEHIND_OFFSET = 2000;
	public static final int ONE_SECOND_IN_MILLIS = 1000;
	public static final int INVALID = -1;
	public static final int SHADER_ERROR_LOG_SIZE = 4096;
	public static final int LOADING_SCREEN_REFRESH_RATE = 10;
	public static final int LOADING_SCREEN_INDICATOR_REFRESH = 1;
	public static final int LOADING_SCREEN_WIDTH = 800;
	public static final int LOADING_SCREEN_HEIGHT = 450;
	public static final int LOADING_SCREEN_FONT_SIZE = 16;
	public static final int LOADING_SCREEN_FONT_X = 400;
	public static final int LOADING_SCREEN_FONT_Y = 340;
	public static final int LOADING_SCREEN_FONT_PROPERTIES = Font.BOLD;
	public static final int LOADING_SCREEN_FONT_COLOR = 0x6a9df9;
	public static final int LOADING_SCREEN_INDICATOR_SIZE = 10;
	public static final int LOADING_SCREEN_INDICATOR_PADDING = 3;
	public static final int LOADING_SCREEN_INDICATOR_X = 400;
	public static final int LOADING_SCREEN_INDICATOR_Y = 380;
	public static final int LOADING_SCREEN_INDICATOR_LENGTH = 3;

	public static final String NEMGINE_NAME = "Nemgine";
	public static final String LOADING_SCREEN_FONT = "Courier New";
	public static final String LOGO_IMAGE_PATH = "com/nemezor/nemgine/graphics/logo.png";
	public static final String TEXTURE_EXCEPTION_NO_ACCESSOR = "Anonymous Texture Accessor";
	public static final String THREAD_BIND_EXCEPTION_NO_THROWER = "Anonymous Thread";
	public static final String SHADER_EXCEPTION_NO_ACCESSOR = "Anonymous Shader Accessor";
	public static final String SHADER_MANAGER_NAME = "Nemgine Shader Manager";
	public static final String SHADER_MANAGER_LOADER_GLOBAL_ERROR = "An error occured while loading shaders, aborting";
	public static final String SHADER_MANAGER_NO_SHADER_BOUND = "No shader currently bound";
	public static final String SHADER_MANAGER_SHADER_DOESNT_EXIST = "Shader does not exist";
	public static final String SHADER_LOADER_LOAD_ERROR = "Failed to load shader file!";
	public static final String SHADER_LOADER_COMPILE_ERROR = "Failed to compile shader program!";
	public static final String SHADER_LOADER_NAME = "Nemgine Shader Loader";
	public static final String TEXTURE_LOADER_NOT_FOUND = "Texture failed to load!";
	public static final String TEXTURE_LOADER_NAME = "Nemgine Texture Loader";
	public static final String TEXTURE_MANAGER_NAME = "Nemgine Texture Manager";
	public static final String TEXTURE_MANAGER_LOADER_GLOBAL_ERROR = "An error occured while loading textures, aborting";
	public static final String TEXTURE_MISSING_PATH = "com/nemezor/nemgine/graphics/missing.png";
	public static final String TEXTURE_LOADER_MISSING_ERROR = "Failed to load 'invalid' texture!";
	public static final String MODEL_LOADER_NOT_FOUND = "Model failed to load!";
	public static final String MODEL_LOADER_NAME = "Nemgine Model Loader";
	public static final String MODEL_MANAGER_NAME = "Nemgine Model Manager";
	public static final String MODEL_MANAGER_LOADER_GLOBAL_ERROR = "An error occured while loading models, aborting";
}
