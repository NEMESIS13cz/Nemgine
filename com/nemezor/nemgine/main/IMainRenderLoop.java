package com.nemezor.nemgine.main;

import com.nemezor.nemgine.misc.Registry;

public interface IMainRenderLoop {
	
	public void render();
	
	public void setUpRender();
	
	public void cleanUpRender();
	
	public void updateRenderSecond(int frames, long averageInterval);
	
	public long getRenderSleepInterval();
	
	public int getRenderFrameskipTreshold();
	
	public default String getStacktraceRenderName() {
		return Registry.THREAD_RENDER_LOOP_NAME;
	}
}
