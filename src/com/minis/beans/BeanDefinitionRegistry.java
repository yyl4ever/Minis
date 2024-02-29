package com.minis.beans;

/**
 * 集中存放  beanDefinition
 */
public interface BeanDefinitionRegistry {
	void registerBeanDefinition(String name, BeanDefinition bd);
	void removeBeanDefinition(String name);
	BeanDefinition getBeanDefinition(String name);
	boolean containsBeanDefinition(String name);
}
