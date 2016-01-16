package com.nemezor.nemgine.misc;

import java.awt.Font;

public final class Registry {
	
	// Main
	public static final int ERROR_SCREEN_WIDTH = 600;
	public static final int ERROR_SCREEN_HEIGHT = 400;
	public static final int OPENGL_OFFICIAL_SUPPORTED_VERSION = 32;
	
	// Misc
	public static final int ONE_SECOND_IN_MILLIS = 1000;
	public static final int INVALID = -1;
	public static final int COLOR_NORMALIZER_VALUE = 255;
	
	// Threads
	public static final int DEFAULT_FRAMESKIP_TRESHOLD = 5;
	public static final int DEFAULT_TICKSKIP_TRESHOLD = 5;
	public static final int MAX_TIME_BEHIND_OFFSET = 2000;
	
	// Shaders
	public static final int SHADER_ERROR_LOG_SIZE = 4096;
	
	// Loading
	public static final int LOADING_SCREEN_WIDTH = 800;
	public static final int LOADING_SCREEN_HEIGHT = 450;
	public static final int LOADING_SCREEN_FONT_SIZE = 16;
	public static final int LOADING_SCREEN_FONT_X = 400;
	public static final int LOADING_SCREEN_FONT_Y = 340;
	public static final int LOADING_SCREEN_FONT_PROPERTIES = Font.BOLD;
	public static final int LOADING_SCREEN_FONT_COLOR = 0x6a9df9;
	public static final int LOADING_SCREEN_REFRESHRATE = 60;
	
	// Network
	public static final int IP_ADDRESS_MAXIMUM_PORT_VALUE = 65535;
	public static final int IP_ADDRESS_MINIMUM_PORT_VALUE = 0;
	public static final int SOCKET_DEFAULT_TIMEOUT = 5000;
	
	////////////////////////////////////////////////////////////////////////////////////////

	// Main
	public static final boolean OPENGL_FORWARD_COMPATIBLE = true;
	
	////////////////////////////////////////////////////////////////////////////////////////
	
	// Main
	public static final String NEMGINE_NAME = "Nemgine";
	public static final String NEMGINE_SHUTDOWN_DISPOSE = "Disposing resources";
	public static final String NEMGINE_SHUTDOWN_EXIT = "Shutting down with exit code ";
	public static final String NEMGINE_RESOLVE_NONE = "No entry point specified!";
	public static final String NEMGINE_RESOLVE_MULTIPLE = "Multiple entry points specified!";
	public static final String NEMGINE_EXECUTION_FAIL = "Application execution failed!";
	public static final String NEMGINE_EXCEPTION_SHUTDOWN = "The application has encountered an unexpected exception.";
	public static final String NEMGINE_EXCEPTION_SHUTDOWN_MORE = "More information can be found in the logs.";
	public static final String NEMGINE_OPENGL_VERSION_OUT_OF_DATE = "Your OpenGL version is not sufficient to run Nemgine.";
	public static final String NEMGINE_FAILED_TO_INITIALIZE_GLFW = "Failed to initialize GLFW.";
	public static final String NEMGINE_FAILED_TO_INSTANTIATE_APPLICATION = "Failed to create an instance of application class.";
	
	// Window
	public static final String WINDOW_EXCEPTION_NO_ACCESSOR = "Anonymous Window Accessor";
	public static final String WINDOW_EXCEPTION_FAILED_TO_CREATE = "Failed to create GLFW Window.";
	public static final String WINDOW_EXCEPTION_PLATFORM_DATA_EXTRACTION = "Failed to create Platform window.";
	public static final String WINDOW_EXCEPTION_LOADING_SCREEN_INITIALIZATION_FAILED = "Failed to create Loading Screen window.";
	
	// Misc
	public static final String LOGO_IMAGE_PATH = "com/nemezor/nemgine/resources/logo.png";
	public static final String LOGO_IMAGE_LOAD_ERROR = "Failed to load logo image!";
	public static final String GUI_COLORED_SHADER_FRAGMENT = "com/nemezor/nemgine/resources/gui_colored.fragment";
	public static final String GUI_COLORED_SHADER_VERTEX = "com/nemezor/nemgine/resources/gui_colored.vertex";
	public static final String GUI_TEXTURED_SHADER_FRAGMENT = "com/nemezor/nemgine/resources/gui_textured.fragment";
	public static final String GUI_TEXTURED_SHADER_VERTEX = "com/nemezor/nemgine/resources/gui_textured.vertex";
	
	// Logging
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
	public static final String THREAD_ALREADY_RUNNING = "Thread is already running!";
	public static final String THREAD_ALREADY_AUXILIARY = "Thread is already auxiliary thread!";
	public static final String THREAD_ALREADY_MAIN = "Thread is already a main render or main tick thread!";
	
	// Textures
	public static final String TEXTURE_EXCEPTION_NO_ACCESSOR = "Anonymous Texture Accessor";
	public static final String TEXTURE_LOADER_NOT_FOUND = "Texture failed to load!";
	public static final String TEXTURE_LOADER_NAME = "Nemgine Texture Loader";
	public static final String TEXTURE_MANAGER_NAME = "Nemgine Texture Manager";
	public static final String TEXTURE_MANAGER_LOADER_GLOBAL_ERROR = "An error occured while loading textures, aborting";
	public static final String TEXTURE_MISSING_PATH = "com/nemezor/nemgine/resources/missing.png";
	public static final String TEXTURE_LOGO_PATH = "com/nemezor/nemgine/resources/logo.png";
	public static final String TEXTURE_LOADER_MISSING_ERROR = "Failed to load 'invalid' texture!";
	public static final String TEXTURE_LOADER_LOGO_ERROR = "Failed to load 'logo' texture!";
	
	// Shaders
	public static final String SHADER_EXCEPTION_NO_ACCESSOR = "Anonymous Shader Accessor";
	public static final String SHADER_MANAGER_NAME = "Nemgine Shader Manager";
	public static final String SHADER_MANAGER_LOADER_GLOBAL_ERROR = "An error occured while loading shaders, aborting";
	public static final String SHADER_MANAGER_NO_SHADER_BOUND = "No shader currently bound";
	public static final String SHADER_MANAGER_SHADER_DOESNT_EXIST = "Shader does not exist";
	public static final String SHADER_LOADER_LOAD_ERROR = "Failed to load shader file!";
	public static final String SHADER_LOADER_COMPILE_ERROR = "Failed to compile shader program!";
	public static final String SHADER_LOADER_NAME = "Nemgine Shader Loader";
	public static final String SHADER_LOGO_VERTEX = "com/nemezor/nemgine/resources/logo.vertex";
	public static final String SHADER_LOGO_FRAGMENT = "com/nemezor/nemgine/resources/logo.fragment";
	public static final String SHADER_PROGRESS_BAR_VERTEX = "com/nemezor/nemgine/resources/loader_bar.vertex";
	public static final String SHADER_PROGRESS_BAR_FRAGMENT = "com/nemezor/nemgine/resources/loader_bar.fragment";
	
	// Models
	public static final String MODEL_EXCEPTION_NO_ACCESSOR = "Anonymous Model Accessor";
	public static final String MODEL_LOADER_NOT_FOUND = "Failed to load model!";
	public static final String MODEL_LOADER_NAME = "Nemgine Model Loader";
	public static final String MODEL_MANAGER_NAME = "Nemgine Model Manager";
	public static final String MODEL_MANAGER_LOADER_GLOBAL_ERROR = "An error occured while loading models, aborting";
	public static final String MODEL_SQUARE = "com/nemezor/nemgine/resources/square.obj";
	
	// Loading
	public static final String LOADING_SCREEN_FONT = "Courier New";
	public static final String PARAMS_ARG_PREFIX_1 = "-";
	public static final String PARAMS_ARG_PREFIX_2 = "--";
	public static final String PARAMS_SEPARATOR = "=";
	public static final String LOADING_SCREEN_ERROR = "Failed to load resources.";
	public static final String LOADING_RESOURCES_GENERATE_IDS_FAILED = "Failed to generate OpenGL resource IDs.";
	public static final String LOADING_RESOURCES_LOAD_FAILED = "Failed to load OpenGL resources.";
	
	// Network
	public static final String NETWORK_MANAGER_NAME = "Nemgine Network Manager";
	public static final String NETWORK_MANAGER_UNKNOWN_HOST_ERROR = "Failed to resolve host name!";
	public static final String NETWORK_MANAGER_IO_ERROR = "I/O error has occured!";
	public static final String IP_ADDRESS_INVALID_PORT = "Port out of range!";
	public static final String IP_ADDRESS_NAME = "IP Address Checker";
	public static final String NETWORK_EXCEPTION_NO_ACCESSOR = "Anonymous Network Accessor";
	
	// Parameters
	public static final String PARAM_CONTAINED = "NGcontained";
	public static final String PARAM_SERVER = "NGserver";
	public static final String PARAM_COMPAT = "NGcompat";
}
