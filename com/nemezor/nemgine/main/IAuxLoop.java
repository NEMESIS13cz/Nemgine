package com.nemezor.nemgine.main;

import com.nemezor.nemgine.misc.Registry;

public interface IAuxLoop {
	
	public void update();
	
	public void setUp();
	
	public void cleanUp();
	
	public long getSleepInterval();
	
	public default String getStacktraceAuxName() {
		return Registry.THREAD_AUX_LOOP_NAME;
	}
}
