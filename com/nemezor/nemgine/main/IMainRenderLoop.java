package com.nemezor.nemgine.main;

public interface IMainRenderLoop {
	
	public void render();
	
	public void setUpRender();
	
	public void cleanUpRender();
	
	public void generateResources();
	
	public void loadResources();
	
	public void updateRenderSecond(int frames, long averageInterval);
	
	public long getRenderSleepInterval();
	
	public int getRenderFrameskipTreshold();
	
	public default String getStacktraceRenderName() {
		return "Main render loop";
	}
}
