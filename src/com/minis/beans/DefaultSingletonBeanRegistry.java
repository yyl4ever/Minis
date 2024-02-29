package com.minis.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SingletonBeanRegistry 的默认实现类
 */
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {
	/**
	 * 容器中存放所有 bean 实例别名的列表
	 */
    protected List<String> beanNames=new ArrayList<>();
	/**
	 * 容器中存放所有 bean 实例的 map
	 */
    protected Map<String, Object> singletonObjects =new ConcurrentHashMap<>(256);
    protected Map<String,Set<String>> dependentBeanMap = new ConcurrentHashMap<>(64);
    protected Map<String,Set<String>> dependenciesForBeanMap = new ConcurrentHashMap<>(64);

	/**
	 * 初始化的时候没有并发问题，考虑到 refresh，运行时刷新，可能是多线程的，需要考虑并发
	 * @param beanName
	 * @param singletonObject
	 */
	@Override
	public void registerSingleton(String beanName, Object singletonObject) {
		/**
		 * 	确保在多线程并发的情况下，安全地对 bean 管理，确保 bean 单例唯一
		 */
		synchronized(this.singletonObjects) {
			// 保证业务的逻辑一致性
	    	this.singletonObjects.put(beanName, singletonObject);
	    	this.beanNames.add(beanName);
		}
	}

	@Override
	public Object getSingleton(String beanName) {
		return this.singletonObjects.get(beanName);
	}

	@Override
	public boolean containsSingleton(String beanName) {
		return this.singletonObjects.containsKey(beanName);
	}

	@Override
	public String[] getSingletonNames() {
		return (String[]) this.beanNames.toArray();
	}
	
	protected void removeSingleton(String beanName) {
	    synchronized (this.singletonObjects) {
		    this.singletonObjects.remove(beanName);
		    this.beanNames.remove(beanName);
	    }
	}
	
	protected void registerDependentBean(String beanName, String dependentBeanName) {
		
	}
	
	protected boolean hasDependentBean(String beanName) {
		return false;
	}
	protected String[] getDependentBeans(String beanName) {
		return null;
	}
	protected String[] getDependenciesForBean(String beanName) {
		return null;
	}}
