package com.minis.beans;

/**
 * 管理单例 bean
 */
public interface SingletonBeanRegistry {
    void registerSingleton(String beanName, Object singletonObject);

    Object getSingleton(String beanName);

    boolean containsSingleton(String beanName);

    /**
     * 获取所有单例 bean
     * @return
     */
    String[] getSingletonNames();

}
