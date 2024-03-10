package com.minis.aop;

import java.lang.reflect.Method;

/**
 * 方法匹配算法
 */
public interface MethodMatcher {
	/**
	 * 看名称是否符合某个模式
	 * @param method
	 * @param targetClass
	 * @return
	 */
	boolean matches(Method method, Class<?> targetClass);
}
