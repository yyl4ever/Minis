package com.minis.beans.factory;

import java.util.Map;

import com.minis.beans.BeansException;

/**
 * 将 bf 内部管理的 bean 作为一个集合对待
 */
public interface ListableBeanFactory extends BeanFactory {

	boolean containsBeanDefinition(String beanName);

	int getBeanDefinitionCount();

	String[] getBeanDefinitionNames();

	/**
	 * 根据某个类型获取 bean 列表
	 * @param type
	 * @return
	 */
	String[] getBeanNamesForType(Class<?> type);

	<T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException;

}

