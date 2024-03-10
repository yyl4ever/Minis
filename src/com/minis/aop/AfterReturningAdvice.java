package com.minis.aop;

import java.lang.reflect.Method;

public interface AfterReturningAdvice extends AfterAdvice {
	// 目标方法后调用，所以可以获取到返回值
	void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable;
}
