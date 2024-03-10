package com.minis.aop;
// 支持符合某个规则的 pointCut 的增强器
public interface PointcutAdvisor extends Advisor {

	Pointcut getPointcut();
}