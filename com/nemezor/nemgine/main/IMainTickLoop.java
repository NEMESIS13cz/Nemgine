package com.nemezor.nemgine.main;

public interface IMainTickLoop {

	public abstract void tick();
	
	public abstract void setUpTick();
	
	public abstract void cleanUpTick();
	
	public abstract void updateTickSecond(int ticks, long averageInterval);
	
	public abstract long getTickSleepInterval();
	
	public abstract int getTickTickskipTreshold();
	
	public default String getStacktraceTickName() {
		return "Main tick loop";
	}
}
