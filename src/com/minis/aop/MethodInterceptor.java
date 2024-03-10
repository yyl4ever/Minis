package com.minis.aop;

/**
 * 方法拦截器
 */
public interface MethodInterceptor extends Interceptor{
	Object invoke(MethodInvocation invocation) throws Throwable;
}
