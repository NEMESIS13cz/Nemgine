package com.nemezor.nemgine.misc;

import java.awt.Font;

public final class Registry {
	
	private Registry() {}
	
	// Main
	public static final int ERROR_SCREEN_WIDTH = 600;
	public static final int ERROR_SCREEN_HEIGHT = 400;
	public static final GLVersion SUPPORTED_OPENGL_VERSION = new GLVersion(3, 3);
	
	// Misc
	public static final int ONE_SECOND_IN_MILLIS = 1000;
	public static final int INVALID = -1;
	public static final int COLOR_NORMALIZER_VALUE = 255;
	public static final Color DEBUG_TEXT_OUTLINE_COLOR = new Color(0xFF0000FF);
	public static final int PLATFORM_MEMORY_POLL_REFRESH = 10;
	
	// Threads
	public static final int DEFAULT_FRAMESKIP_TRESHOLD = 5;
	public static final int DEFAULT_TICKSKIP_TRESHOLD = 5;
	public static final int MAX_TIME_BEHIND_OFFSET = 2000;
	
	// Shaders
	public static final int SHADER_ERROR_LOG_SIZE = 4096;
	
	// Loading
	public static final int LOADING_SCREEN_WIDTH = 800;
	public static final int LOADING_SCREEN_HEIGHT = 450;
	public static final Color LOADING_SCREEN_FONT_COLOR = new Color(0xFFFFFFFF);
	public static final int LOADING_SCREEN_REFRESHRATE = 60;
	
	// Network
	public static final int IP_ADDRESS_MAXIMUM_PORT_VALUE = 65535;
	public static final int IP_ADDRESS_MINIMUM_PORT_VALUE = 0;
	public static final int SOCKET_DEFAULT_TIMEOUT = 5000;
	public static final int RSA_ENCRYPTION_KEY_LENGTH = 4096;
	public static final int AES_ENCRYPTION_KEY_LENGTH = 256;
	
	// Fonts
	public static final Color FONT_DEFAULT_COLOR = new Color(0xFFFFFFFF);
	public static final int FONT_DEFAULT_FONT_SIZE = 20;
	public static final int FONT_DEFAULT_FONT_STYLE = Font.PLAIN;
	public static final int FONT_TAB_WIDTH_IN_CHARS = 4;
	
	// GUI
	public static final int GUI_DEFAULT_RASTER_WIDTH = 1600;
	public static final int GUI_DEFAULT_RASTER_HEIGHT = 900;
	public static final Color GUI_DEFAULT_PRIMARY_COLOR = new Color(0xFFFFFFFF);
	public static final Color GUI_DEFAULT_SECONDARY_COLOR = new Color(0xF0F0F0FF);
	public static final Color GUI_DEFAULT_TERTIARY_COLOR = new Color(0xE1E1E1FF);
	public static final Color GUI_DEFAULT_QUATERNARY_COLOR = new Color(0xADADADFF);
	public static final Color GUI_DEFAULT_PRIMARY_ACCENT_COLOR = new Color(0xE5F1FBFF);
	public static final Color GUI_DEFAULT_SECONDARY_ACCENT_COLOR = new Color(0x0078D7FF);
	public static final Color GUI_DEFAULT_FONT_COLOR = new Color(0x000000FF);
	
	////////////////////////////////////////////////////////////////////////////////////////

	// Main
	public static final boolean OPENGL_FORWARD_COMPATIBLE = false;
	
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
	public static final String NEMGINE_EXIT_THREAD_NAME = "Exit Thread";
	
	// Window
	public static final String WINDOW_EXCEPTION_NO_ACCESSOR = "Anonymous Window Accessor";
	public static final String WINDOW_EXCEPTION_FAILED_TO_CREATE = "Failed to create GLFW Window.";
	public static final String WINDOW_EXCEPTION_PLATFORM_DATA_EXTRACTION = "Failed to create Platform window.";
	public static final String WINDOW_EXCEPTION_LOADING_SCREEN_INITIALIZATION_FAILED = "Failed to create Loading Screen window.";
	
	// Misc
	public static final String LOGO_IMAGE_PATH = "com/nemezor/nemgine/resources/logo.png";
	public static final String LOGO_IMAGE_LOAD_ERROR = "Failed to load logo image!";
	public static final String PLATFORM_NAME = "Platform";
	
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
	public static final String THREAD_AUX_LOOP_NAME = "Auxiliary loop";
	public static final String THREAD_TICK_LOOP_NAME = "Main tick loop";
	public static final String THREAD_RENDER_LOOP_NAME = "Main render loop";
	
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
	public static final String SHADER_PROGRESS_BAR_VERTEX = "com/nemezor/nemgine/resources/loader_bar.vertex";
	public static final String SHADER_PROGRESS_BAR_FRAGMENT = "com/nemezor/nemgine/resources/loader_bar.fragment";
	public static final String COLOR_SHADER_FRAGMENT = "com/nemezor/nemgine/resources/color.frag";
	public static final String COLOR_SHADER_VERTEX = "com/nemezor/nemgine/resources/color.vert";
	public static final String TEXTURE_SHADER_FRAGMENT = "com/nemezor/nemgine/resources/texture.frag";
	public static final String TEXTURE_SHADER_VERTEX = "com/nemezor/nemgine/resources/texture.vert";
	public static final String FONT_SHADER_FRAGMENT = "com/nemezor/nemgine/resources/font.frag";
	
	public static final String FONT_SHADER_COLOR_ATTRIBUTE = "color";
	public static final String FONT_SHADER_PROJECTION_ATTRIBUTE = "projection";
	public static final String FONT_SHADER_TRANSFORMATION_ATTRIBUTE = "transformation";
	
	// Models
	public static final String MODEL_EXCEPTION_NO_ACCESSOR = "Anonymous Model Accessor";
	public static final String MODEL_LOADER_NOT_FOUND = "Failed to load model!";
	public static final String MODEL_LOADER_NAME = "Nemgine Model Loader";
	public static final String MODEL_MANAGER_NAME = "Nemgine Model Manager";
	public static final String MODEL_MANAGER_LOADER_GLOBAL_ERROR = "An error occured while loading models, aborting";
	public static final String MODEL_SQUARE = "com/nemezor/nemgine/resources/square.obj";
	
	// Tessellator
	public static final String TESSELLATOR_ALREADY_RUNNING = "Already tessellating!";
	public static final String TESSELLATOR_NOT_RUNNING = "Not tessellating!";
	public static final String TESSELLATOR_SWITCHED_CONTEXTS = "Windows switched during tessellation!";
	public static final String TESSELLATOR_INVALID_DATA = "Invalid data!";
	
	// Loading
	public static final String PARAMS_ARG_PREFIX_1 = "-";
	public static final String PARAMS_ARG_PREFIX_2 = "--";
	public static final String PARAMS_SEPARATOR = "=";
	public static final String LOADING_SCREEN_ERROR = "Failed to load resources.";
	public static final String LOADING_RESOURCES_GENERATE_IDS_FAILED = "Failed to generate OpenGL resource IDs.";
	public static final String LOADING_RESOURCES_LOAD_FAILED = "Failed to load OpenGL resources.";
	public static final String LOADING_PROGRESS_GFX_RESOURCES = "Loading Graphical Resources";
	public static final String LOADING_PROGRESS_GFX_TEXTURES = "Loading Textures";
	public static final String LOADING_PROGRESS_GFX_MODELS = "Loading Models";
	public static final String LOADING_PROGRESS_GFX_SHADERS = "Loading Shaders";
	public static final String LOADING_PROGRESS_GFX_FONTS = "Loading Fonts";
	public static final String LOADING_SCREEN_NAME = "Resource Loader";
	
	// Network
	public static final String NETWORK_MANAGER_NAME = "Nemgine Network Manager";
	public static final String NETWORK_MANAGER_UNKNOWN_HOST_ERROR = "Failed to resolve host name!";
	public static final String NETWORK_MANAGER_IO_ERROR = "I/O error has occured!";
	public static final String IP_ADDRESS_INVALID_PORT = "Port out of range!";
	public static final String IP_ADDRESS_NAME = "IP Address Checker";
	public static final String NETWORK_EXCEPTION_NO_ACCESSOR = "Anonymous Network Accessor";
	public static final String KEY_EXCHANGE_ENCRYPTION_ALGORITHM = "RSA";
	public static final String CONNECTION_ENCRYPTION_ALGORITHM = "AES";
	
	// Parameters
	public static final String PARAM_CONTAINED = "NGcontained";
	public static final String PARAM_SERVER = "NGserver";
	public static final String PARAM_DEBUG = "NGdebug";
	public static final String PARAM_KEEPUP = "NGkeepUpWarnings";
	
	// Fonts
	public static final String FONT_MANAGER_NAME = "Nemgine Font Manager";
	public static final String FONT_FALLBACK_FONT = "Monospaced";
	public static final String FONT_NOT_FOUND_MESSAGE_1 = "Could not find font '";
	public static final String FONT_NOT_FOUND_MESSAGE_2 = "', defaulting to '";
	public static final String FONT_NOT_FOUND_MESSAGE_3 = "'";
}
