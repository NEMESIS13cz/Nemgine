package com.nemezor.nemgine.main;

public interface IAuxLoop {

	public abstract void update();
	
	public abstract void setUp();
	
	public abstract void cleanUp();
	
	public abstract long getSleepInterval();
	
	public default String getStacktraceAuxName() {
		return "Auxiliary loop";
	}
}
