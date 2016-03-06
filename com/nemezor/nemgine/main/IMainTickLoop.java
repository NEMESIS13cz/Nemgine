package com.nemezor.nemgine.main;

import com.nemezor.nemgine.misc.Registry;

public interface IMainTickLoop {

	public void tick();
	
	public void setUpTick();
	
	public void cleanUpTick();
	
	public void updateTickSecond(int ticks, long averageInterval);
	
	public long getTickSleepInterval();
	
	public int getTickTickskipTreshold();
	
	public default String getStacktraceTickName() {
		return Registry.THREAD_TICK_LOOP_NAME;
	}
}
