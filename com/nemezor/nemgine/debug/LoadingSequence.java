package com.nemezor.nemgine.debug;

import com.nemezor.nemgine.main.NemgineLoader;
import com.nemezor.nemgine.misc.DefaultLoaderMessages;

public class LoadingSequence {
	
	public static void run(String[] additional, boolean skipBuiltIn) {
		try{
			if (!skipBuiltIn) {
				NemgineLoader.updateState(DefaultLoaderMessages.LOADING_MODELS);
				Thread.sleep(5000);
				NemgineLoader.updateState(DefaultLoaderMessages.LOADING_SHADERS);
				Thread.sleep(5000);
				NemgineLoader.updateState(DefaultLoaderMessages.LOADING_FONTS);
				Thread.sleep(5000);
				NemgineLoader.updateState(DefaultLoaderMessages.LOADING_TEXTURES);
				Thread.sleep(5000);
			}
			for (String s : additional){
				NemgineLoader.updateState(s);
				Thread.sleep(5000);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
}
