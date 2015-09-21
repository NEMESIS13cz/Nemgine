package com.nemezor.nemgine.misc;

import java.awt.Font;

public final class Registry {
	
	// Main
	
	// Misc
	public static final int ONE_SECOND_IN_MILLIS = 1000;
	public static final int INVALID = -1;
	
	// Threads
	public static final int DEFAULT_FRAMESKIP_TRESHOLD = 5;
	public static final int DEFAULT_TICKSKIP_TRESHOLD = 5;
	public static final int MAX_TIME_BEHIND_OFFSET = 2000;
	
	// Textures
	
	// Shaders
	public static final int SHADER_ERROR_LOG_SIZE = 4096;
	
	// Models
	
	// Loading
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
	
	////////////////////////////////////////////////////////////////////////////////////////
	
	// Main
	public static final String NEMGINE_NAME = "Nemgine";
	public static final String NEMGINE_SHUTDOWN_DISPOSE = "Disposing resources";
	public static final String NEMGINE_SHUTDOWN_EXIT = "Shutting down with exit code ";
	public static final String NEMGINE_RESOLVE_NONE = "No entry point specified!";
	public static final String NEMGINE_RESOLVE_MULTIPLE = "Multiple entry points specified!";
	public static final String NEMGINE_EXECUTION_FAIL = "Application execution failed!";
	
	// Misc
	public static final String LOGO_IMAGE_PATH = "com/nemezor/nemgine/graphics/logo.png";
	public static final String LOGO_IMAGE_LOAD_ERROR = "Failed to load logo image!";
	public static final String LOG_FILE_PATH = "/logs";
	public static final String LOG_FILE_DATE_FORMAT = "yyyy-M-dd";
	public static final String LOG_MESSAGE_DATE_FORMAT = "HH:mm:ss";
	public static final String LOG_FILE_FORMAT = ".log";
	
	// Threads
	public static final String THREAD_BIND_EXCEPTION_NO_THROWER = "Anonymous Thread";
	public static final String THREAD_TICK_KEEPUP_1 = "Can't keep up at ticking! skipping ";
	public static final String THREAD_TICK_KEEPUP_2 = " ticks.";
	public static final String THREAD_RENDER_KEEPUP_1 = "Can't keep up at rendering! skipping ";
	public static final String THREAD_RENDER_KEEPUP_2 = " frames.";
	public static final String THREAD_TIME_KEEPUP_1 = "Can't keep up at timing! skipping ";
	public static final String THREAD_TIME_KEEPUP_2 = " seconds.";
	public static final String THREAD_INTERRUPT = "Thread interrupted! This should NEVER happen!";
	
	// Textures
	public static final String TEXTURE_EXCEPTION_NO_ACCESSOR = "Anonymous Texture Accessor";
	public static final String TEXTURE_LOADER_NOT_FOUND = "Texture failed to load!";
	public static final String TEXTURE_LOADER_NAME = "Nemgine Texture Loader";
	public static final String TEXTURE_MANAGER_NAME = "Nemgine Texture Manager";
	public static final String TEXTURE_MANAGER_LOADER_GLOBAL_ERROR = "An error occured while loading textures, aborting";
	public static final String TEXTURE_MISSING_PATH = "com/nemezor/nemgine/graphics/missing.png";
	public static final String TEXTURE_LOADER_MISSING_ERROR = "Failed to load 'invalid' texture!";
	
	// Shaders
	public static final String SHADER_EXCEPTION_NO_ACCESSOR = "Anonymous Shader Accessor";
	public static final String SHADER_MANAGER_NAME = "Nemgine Shader Manager";
	public static final String SHADER_MANAGER_LOADER_GLOBAL_ERROR = "An error occured while loading shaders, aborting";
	public static final String SHADER_MANAGER_NO_SHADER_BOUND = "No shader currently bound";
	public static final String SHADER_MANAGER_SHADER_DOESNT_EXIST = "Shader does not exist";
	public static final String SHADER_LOADER_LOAD_ERROR = "Failed to load shader file!";
	public static final String SHADER_LOADER_COMPILE_ERROR = "Failed to compile shader program!";
	public static final String SHADER_LOADER_NAME = "Nemgine Shader Loader";
	
	// Models
	public static final String MODEL_LOADER_NOT_FOUND = "Model failed to load!";
	public static final String MODEL_LOADER_NAME = "Nemgine Model Loader";
	public static final String MODEL_MANAGER_NAME = "Nemgine Model Manager";
	public static final String MODEL_MANAGER_LOADER_GLOBAL_ERROR = "An error occured while loading models, aborting";
	
	// Loading
	public static final String LOADING_SCREEN_FONT = "Courier New";
	public static final String PARAMS_ARG_PREFIX_1 = "-";
	public static final String PARAMS_ARG_PREFIX_2 = "--";
	public static final String PARAMS_SEPARATOR = "=";
}
