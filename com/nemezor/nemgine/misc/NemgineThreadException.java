package com.nemezor.nemgine.misc;

import com.nemezor.nemgine.main.IAuxLoop;
import com.nemezor.nemgine.main.IMainRenderLoop;
import com.nemezor.nemgine.main.IMainTickLoop;

public class NemgineThreadException extends Exception {

	private static final long serialVersionUID = 1L;

	private IMainRenderLoop render;
	private IMainTickLoop tick;
	private IAuxLoop aux;
	private Thread thrower;
	
	public NemgineThreadException(String message) {
		super(message);
	}
	
	public NemgineThreadException(String message, Thread thrower) {
		super(message);
		this.thrower = thrower;
	}
	
	public void setThrower(Thread thread) {
		thrower = thread;
	}
	
	public void setBindCause(IMainRenderLoop loop) {
		render = loop;
	}
	
	public void setBindCause(IMainTickLoop loop) {
		tick = loop;
	}
	
	public void setBindCause(IAuxLoop loop) {
		aux = loop;
	}
	
	public Thread getThrower() {
		return thrower;
	}
	
	public IMainRenderLoop getBindCauseRender() {
		return render;
	}
	
	public IMainTickLoop getBindCauseTick() {
		return tick;
	}
	
	public IAuxLoop getBindCauseAux() {
		return aux;
	}
	
	public void printStackTrace() {
		System.err.println("[" + (thrower != null ? thrower.getName() : Registry.THREAD_BIND_EXCEPTION_NO_THROWER) + "]: " + getLocalizedMessage());
		String msg = null;
		if (getBindCauseRender() != null) {
			msg = getBindCauseRender().getStacktraceRenderName();
		}else if (getBindCauseTick() != null) {
			msg = getBindCauseTick().getStacktraceTickName();
		}else if (getBindCauseAux() != null) {
			msg = getBindCauseAux().getStacktraceAuxName();
		}
		if (msg != null) {
			System.err.println(" >>> " + msg);
		}
	}
}
