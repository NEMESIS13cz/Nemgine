package com.nemezor.nemgine.main;

public interface IMainRenderLoop {
	
	public abstract void render();
	
	public abstract void setUpRender();
	
	public abstract void cleanUpRender();
	
	public abstract void generateResources();
	
	public abstract void loadResources();
	
	public abstract void updateRenderSecond(int frames, long averageInterval);
	
	public abstract long getRenderSleepInterval();
	
	public abstract int getRenderFrameskipTreshold();
	
	public default String getStacktraceRenderName() {
		return "Main render loop";
	}
}
