package com.minis.aop;

public interface Pointcut {
	//ClassFilter getClassFilter();

	/**
	 * 返回一条匹配规则
	 * @return
	 */
	MethodMatcher getMethodMatcher();

}
