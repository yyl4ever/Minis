package com.minis.beans.factory.config;

import com.minis.beans.BeansException;

/**
 * post的字面含义是“什么什么之后”，这里是指create bean之后，用postProcessor进行修饰。
 */
public interface BeanPostProcessor {
	Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException;

	Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException;

}
