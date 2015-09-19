package com.nemezor.nemgine.main;

import java.util.ArrayList;

import com.nemezor.nemgine.graphics.TextureManager;
import com.nemezor.nemgine.misc.NemgineThreadException;
import com.nemezor.nemgine.misc.Registry;

public class BindableThread extends Thread {
	
	protected IMainRenderLoop		render;
	protected IMainTickLoop			tick;
	protected ArrayList<Object[]>	aux	= new ArrayList<Object[]>();
	protected long					renderInterval;
	protected long					tickInterval;
	
	private boolean isRunning = false;
	
	public void run() {
		isRunning = true;
		if (render != null || tick != null) {
			runMain();
		} else {
			runAux();
		}
	}
	
	private void runMain() {
		long SLEEP = 0;
		long RENDER_SLEEP = 0;
		long TICK_SLEEP = 0;
		
		long NEXT_SECOND = System.currentTimeMillis();
		long NEXT_RENDER = System.currentTimeMillis();
		long NEXT_TICK = System.currentTimeMillis();
		
		int FRAME_SKIP = Registry.DEFAULT_FRAMESKIP_TRESHOLD;
		int frames = 0;
		long average_time_frame = 0;
		long frame_begin = 0;
		
		int TICK_SKIP = Registry.DEFAULT_TICKSKIP_TRESHOLD;
		int ticks = 0;
		long average_time_tick = 0;
		long tick_begin = 0;
		
		if (render != null) {
			render.setUpRender();
			TextureManager.loadMissingTexture();
			render.loadResources();
		}
		
		if (tick != null) {
			tick.setUpTick();
		}
		
		NemgineLoader.stop();
		
		while (Nemgine.isRunning() && isRunning()) {
			long begin = System.currentTimeMillis();
			if (render != null && NEXT_RENDER < System.currentTimeMillis()) {
				NEXT_RENDER += RENDER_SLEEP;
				
				render.render();
				
				if (NEXT_RENDER + (RENDER_SLEEP * FRAME_SKIP) < System.currentTimeMillis()) {
					NemgineThreadException e = new NemgineThreadException("Can't keep up at rendering! skipping " + ((System.currentTimeMillis() - NEXT_RENDER) / (RENDER_SLEEP != 0 ? RENDER_SLEEP : 1)) + " frames.");
					e.setBindCause(render);
					e.setThrower(this);
					e.printStackTrace();
					NEXT_RENDER = System.currentTimeMillis() + RENDER_SLEEP;
				}
				
				average_time_frame += System.currentTimeMillis() - frame_begin;
				frames++;
				frame_begin = System.currentTimeMillis();
			}
			if (tick != null && NEXT_TICK < System.currentTimeMillis()) {
				NEXT_TICK += TICK_SLEEP;
				
				tick.tick();
				
				if (NEXT_TICK + (TICK_SLEEP * TICK_SKIP) < System.currentTimeMillis()) {
					NemgineThreadException e = new NemgineThreadException("Can't keep up at ticking! skipping " + ((System.currentTimeMillis() - NEXT_TICK) / (TICK_SLEEP != 0 ? TICK_SLEEP : 1)) + " ticks.");
					e.setBindCause(tick);
					e.setThrower(this);
					e.printStackTrace();
					NEXT_TICK = System.currentTimeMillis() + TICK_SLEEP;
				}
				
				average_time_tick += System.currentTimeMillis() - tick_begin;
				ticks++;
				tick_begin = System.currentTimeMillis();
			}
			try {
				long sleep_amount = SLEEP - (System.currentTimeMillis() - begin);
				if (sleep_amount > 0) {
					sleep(sleep_amount);
				}
			} catch (InterruptedException ex) {
				NemgineThreadException e = new NemgineThreadException("Thread interrupted!");
				e.setThrower(this);
				e.printStackTrace();
				ex.printStackTrace();
			}
			if (NEXT_SECOND < System.currentTimeMillis()) {
				NEXT_SECOND += Registry.ONE_SECOND_IN_MILLIS;
				
				if (NEXT_SECOND + Registry.MAX_TIME_BEHIND_OFFSET < System.currentTimeMillis()) {
					NemgineThreadException e = new NemgineThreadException("Can't keep up at timing! skipping " + ((System.currentTimeMillis() - NEXT_SECOND) / Registry.ONE_SECOND_IN_MILLIS) + " seconds.");
					e.setThrower(this);
					e.printStackTrace();
					NEXT_SECOND = System.currentTimeMillis() + Registry.ONE_SECOND_IN_MILLIS;
				}
				
				long r = Long.MIN_VALUE;
				long t = Long.MIN_VALUE;
				if (render != null) {
					r = render.getRenderSleepInterval();
					render.updateRenderSecond(frames, (average_time_frame / (frames != 0 ? frames : 1)));
					average_time_frame = 0;
					frames = 0;
					FRAME_SKIP = render.getRenderFrameskipTreshold();
				}
				if (tick != null) {
					t = tick.getTickSleepInterval();
					tick.updateTickSecond(ticks, (average_time_tick / (ticks != 0 ? ticks : 1)));
					average_time_tick = 0;
					ticks = 0;
					TICK_SKIP = tick.getTickTickskipTreshold();
				}
				long smaller = Math.min(r, t);
				long larger = Math.max(r, t);
				SLEEP = smaller >= 0 ? smaller : larger;
				RENDER_SLEEP = r;
				TICK_SLEEP = t;
				if (SLEEP < 0) {
					SLEEP = 0;
				}
			}
		}
		
		if (render != null) {
			render.cleanUpRender();
		}
		if (tick != null) {
			tick.cleanUpTick();
		}
	}
	
	private void runAux() {
		long SLEEP = 0;
		
		for (Object[] o : aux) {
			((IAuxLoop)o[0]).setUp();
		}
		for (Object[] o : aux) {
			if (((Long)o[1]) == -1L) {
				o[1] = System.currentTimeMillis();
			}
		}
		
		while (Nemgine.isRunning() && isRunning()) {
			long begin = System.currentTimeMillis();
			for (Object[] o : aux) {
				IAuxLoop l = (IAuxLoop)o[0];
				long NEXT_UPDATE = (Long)o[1];
				if (NEXT_UPDATE < System.currentTimeMillis()) {
					l.update();
					
					o[1] = System.currentTimeMillis() + l.getSleepInterval();
				}
				if (l.getSleepInterval() < SLEEP) {
					SLEEP = l.getSleepInterval();
				}
			}
			try {
				long sleep_amount = SLEEP - (System.currentTimeMillis() - begin);
				if (sleep_amount > 0) {
					sleep(sleep_amount);
				}
			} catch (InterruptedException ex) {
				NemgineThreadException e = new NemgineThreadException("Thread interrupted!");
				e.setThrower(this);
				e.printStackTrace();
				ex.printStackTrace();
			}
		}
		
		for (Object[] o : aux) {
			((IAuxLoop)o[0]).cleanUp();
		}
	}
	
	protected synchronized boolean isRunning() {
		return isRunning;
	}
	
	protected synchronized void stopThread() {
		isRunning = false;
	}
}
