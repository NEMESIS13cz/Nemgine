package com.nemezor.nemgine.main;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.nemezor.nemgine.misc.Side;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Application {
	
	boolean contained() default false;
	boolean compatibilityMode() default false;
	Side side() default Side.CLIENT;
	String name();
	String path();
}
