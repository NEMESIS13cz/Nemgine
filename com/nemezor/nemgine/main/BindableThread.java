package com.nemezor.nemgine.main;

import java.util.ArrayList;

import org.lwjgl.opengl.GL;

import com.nemezor.nemgine.exceptions.ThreadException;
import com.nemezor.nemgine.misc.ErrorScreen;
import com.nemezor.nemgine.misc.Registry;

public class BindableThread extends Thread {
	
	protected IMainRenderLoop render;
	protected IMainTickLoop tick;
	protected ArrayList<Object[]> aux = new ArrayList<Object[]>();
	protected long renderInterval;
	protected long tickInterval;
	
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
		if (render != null) {
			GL.setCapabilities(Nemgine.glCaps);
			try{
				render.setUpRender();
			}catch (Exception e) {
				String stack = "";
				for (StackTraceElement el : e.getStackTrace()) {
					stack += el.toString() + "\n";
				}
				ErrorScreen.show(Registry.LOADING_SCREEN_ERROR + "\n\n" + e.getLocalizedMessage() + "\n" + stack, true);
			}
		}
		if (tick != null) {
			try{
				tick.setUpTick();
			}catch (Exception e) {
				String stack = "";
				for (StackTraceElement el : e.getStackTrace()) {
					stack += el.toString() + "\n";
				}
				ErrorScreen.show(Registry.LOADING_SCREEN_ERROR + "\n\n" + e.getLocalizedMessage() + "\n" + stack, true);
			}
		}
		
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
		long average_time_per_frame = render != null ? render.getRenderSleepInterval() : Long.MAX_VALUE;
		
		int TICK_SKIP = Registry.DEFAULT_TICKSKIP_TRESHOLD;
		int ticks = 0;
		long average_time_tick = 0;
		long tick_begin = 0;
		long average_time_per_tick = tick != null ? tick.getTickSleepInterval() : Long.MAX_VALUE;
		
		while (Nemgine.isRunning() && isRunning()) {
			long begin = System.currentTimeMillis();
			if (render != null && NEXT_RENDER < System.currentTimeMillis()) {
				NEXT_RENDER += RENDER_SLEEP - average_time_per_frame;
				frame_begin = System.currentTimeMillis();
				
				render.render();
				
				if (FRAME_SKIP != Registry.INVALID && NEXT_RENDER + (RENDER_SLEEP * FRAME_SKIP) < System.currentTimeMillis()) {
					ThreadException e = new ThreadException(Registry.THREAD_RENDER_KEEPUP_1 + ((System.currentTimeMillis() - NEXT_RENDER) / (RENDER_SLEEP != 0 ? RENDER_SLEEP : 1)) + Registry.THREAD_RENDER_KEEPUP_2);
					e.setBindCause(render);
					e.setThrower(this);
					e.printStackTrace();
					NEXT_RENDER = System.currentTimeMillis() + (RENDER_SLEEP - average_time_per_frame);
				}
				
				average_time_frame += System.currentTimeMillis() - frame_begin;
				frames++;
			}
			if (tick != null && NEXT_TICK < System.currentTimeMillis()) {
				NEXT_TICK += TICK_SLEEP - average_time_per_tick;
				tick_begin = System.currentTimeMillis();
				
				tick.tick();
				
				if (TICK_SKIP != Registry.INVALID && NEXT_TICK + (TICK_SLEEP * TICK_SKIP) < System.currentTimeMillis()) {
					ThreadException e = new ThreadException(Registry.THREAD_TICK_KEEPUP_1 + ((System.currentTimeMillis() - NEXT_TICK) / (TICK_SLEEP != 0 ? TICK_SLEEP : 1)) + Registry.THREAD_TICK_KEEPUP_2);
					e.setBindCause(tick);
					e.setThrower(this);
					e.printStackTrace();
					NEXT_TICK = System.currentTimeMillis() + (TICK_SLEEP - average_time_per_tick);
				}
				
				average_time_tick += System.currentTimeMillis() - tick_begin;
				ticks++;
			}
			try {
				long sleep_amount = SLEEP - (System.currentTimeMillis() - begin);
				if (sleep_amount > 0) {
					sleep(sleep_amount);
				}
			} catch (InterruptedException ex) {
				ThreadException e = new ThreadException(Registry.THREAD_INTERRUPT);
				e.setThrower(this);
				e.printStackTrace();
				ex.printStackTrace();
			}
			if (NEXT_SECOND < System.currentTimeMillis()) {
				NEXT_SECOND += Registry.ONE_SECOND_IN_MILLIS;
				
				if (NEXT_SECOND + Registry.MAX_TIME_BEHIND_OFFSET < System.currentTimeMillis()) {
					ThreadException e = new ThreadException(Registry.THREAD_TIME_KEEPUP_1 + ((System.currentTimeMillis() - NEXT_SECOND) / Registry.ONE_SECOND_IN_MILLIS) + Registry.THREAD_TIME_KEEPUP_2);
					e.setThrower(this);
					e.printStackTrace();
					NEXT_SECOND = System.currentTimeMillis() + Registry.ONE_SECOND_IN_MILLIS;
				}
				
				long r = Long.MIN_VALUE;
				long t = Long.MIN_VALUE;
				if (render != null) {
					r = render.getRenderSleepInterval();
					average_time_per_frame = (average_time_frame / (frames != 0 ? frames : 1));
					render.updateRenderSecond(frames, average_time_per_frame);
					average_time_frame = 0;
					frames = 0;
					FRAME_SKIP = render.getRenderFrameskipTreshold();
				}
				if (tick != null) {
					t = tick.getTickSleepInterval();
					average_time_per_tick = (average_time_tick / (ticks != 0 ? ticks : 1));
					tick.updateTickSecond(ticks, average_time_per_tick);
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
				ThreadException e = new ThreadException(Registry.THREAD_INTERRUPT);
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
