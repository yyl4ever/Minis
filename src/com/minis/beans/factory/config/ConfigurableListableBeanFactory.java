package com.minis.beans.factory.config;

import com.minis.beans.factory.ListableBeanFactory;

/**
 * 用一个接口将几种特性进行整合
 * 接口的能力各自独立互不干扰，符合接口隔离原则
 */
public interface ConfigurableListableBeanFactory 
		extends ListableBeanFactory, AutowireCapableBeanFactory, ConfigurableBeanFactory {

}
