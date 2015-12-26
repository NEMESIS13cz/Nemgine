package com.nemezor.nemgine.main;

public interface IMainTickLoop {

	public void tick();
	
	public void setUpTick();
	
	public void cleanUpTick();
	
	public void updateTickSecond(int ticks, long averageInterval);
	
	public long getTickSleepInterval();
	
	public int getTickTickskipTreshold();
	
	public default String getStacktraceTickName() {
		return "Main tick loop";
	}
}
