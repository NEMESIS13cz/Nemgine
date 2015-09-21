package com.nemezor.nemgine.main;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Application {
	
	boolean contained();
	int width();
	int height();
	String name();
	String path();
}
