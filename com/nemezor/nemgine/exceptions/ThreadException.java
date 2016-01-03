package com.nemezor.nemgine.exceptions;

import com.nemezor.nemgine.main.IAuxLoop;
import com.nemezor.nemgine.main.IMainRenderLoop;
import com.nemezor.nemgine.main.IMainTickLoop;
import com.nemezor.nemgine.misc.Logger;
import com.nemezor.nemgine.misc.Registry;

public class ThreadException extends Exception {

	private static final long serialVersionUID = 1L;

	private IMainRenderLoop render;
	private IMainTickLoop tick;
	private IAuxLoop aux;
	private Thread thrower;
	
	public ThreadException(String message) {
		super(message);
	}
	
	public ThreadException(String message, Thread thrower) {
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
		Logger.log(thrower != null ? thrower.getName() : Registry.THREAD_BIND_EXCEPTION_NO_THROWER, getLocalizedMessage(), false);
		String msg = null;
		if (getBindCauseRender() != null) {
			msg = getBindCauseRender().getStacktraceRenderName();
		}else if (getBindCauseTick() != null) {
			msg = getBindCauseTick().getStacktraceTickName();
		}else if (getBindCauseAux() != null) {
			msg = getBindCauseAux().getStacktraceAuxName();
		}
		if (msg != null) {
			Logger.log(null, msg, false);
		}
	}
}
