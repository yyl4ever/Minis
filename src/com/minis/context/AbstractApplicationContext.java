package com.minis.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.minis.beans.BeansException;
import com.minis.beans.factory.config.BeanFactoryPostProcessor;
import com.minis.beans.factory.config.BeanPostProcessor;
import com.minis.beans.factory.config.ConfigurableListableBeanFactory;
import com.minis.core.env.Environment;

/**
 * interface-abstract-class 模式
 */
public abstract class AbstractApplicationContext implements ApplicationContext{
	private Environment environment;

	private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();
	private long startupDate;
	private final AtomicBoolean active = new AtomicBoolean();
	private final AtomicBoolean closed = new AtomicBoolean();
	private ApplicationEventPublisher applicationEventPublisher;
	


	@Override
	public Object getBean(String beanName) throws BeansException {
		return getBeanFactory().getBean(beanName);
	}

	@Override
	public boolean containsBean(String name) {
		return getBeanFactory().containsBean(name);
	}

//	public void registerBean(String beanName, Object obj) {
//		getBeanFactory().registerBean(beanName, obj);		
//	}

	@Override
	public boolean isSingleton(String name) {
		return getBeanFactory().isSingleton(name);
	}

	@Override
	public boolean isPrototype(String name) {
		return getBeanFactory().isPrototype(name);
	}

	@Override
	public Class<?> getType(String name) {
		return getBeanFactory().getType(name);
	}
	
	public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
		return this.beanFactoryPostProcessors;
	}
	
	
	@Override
	public void refresh() throws BeansException, IllegalStateException {
		// 先有容器的启动，然后才是加载各个 bean，这里可以让开发者在容器启动后做一些处理
		postProcessBeanFactory(getBeanFactory());
		// 可以增加对 bean 的额外修饰代码进行后期处理
		registerBeanPostProcessors(getBeanFactory());

		// 初始化事件发布者
		initApplicationEventPublisher();

		onRefresh();

		// 注册监听者
		registerListeners();
		
		finishRefresh();
	}

	// 抽象方法，提高扩展性
	abstract void registerListeners();
	abstract void initApplicationEventPublisher();
	abstract void postProcessBeanFactory(ConfigurableListableBeanFactory bf);
	abstract void registerBeanPostProcessors(ConfigurableListableBeanFactory bf);
	abstract void onRefresh();
	abstract void finishRefresh();

	@Override
	public void registerSingleton(String beanName, Object singletonObject) {
		getBeanFactory().registerSingleton(beanName, singletonObject);
	}

	@Override
	public Object getSingleton(String beanName) {
		return getBeanFactory().getSingleton(beanName);
	}

	@Override
	public boolean containsSingleton(String beanName) {
		return getBeanFactory().containsSingleton(beanName);
	}

	@Override
	public String[] getSingletonNames() {
		return getBeanFactory().getSingletonNames();
	}

	@Override
	public boolean containsBeanDefinition(String beanName) {
		return getBeanFactory().containsBeanDefinition(beanName);
	}

	@Override
	public int getBeanDefinitionCount() {
		return getBeanFactory().getBeanDefinitionCount();
	}

	@Override
	public String[] getBeanDefinitionNames() {
		return getBeanFactory().getBeanDefinitionNames();
	}

	@Override
	public String[] getBeanNamesForType(Class<?> type) {
		return getBeanFactory().getBeanNamesForType(type);
	}

	@Override
	public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
		return getBeanFactory().getBeansOfType(type);
	}

	@Override
	public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
		getBeanFactory().addBeanPostProcessor(beanPostProcessor);
		
	}

	@Override
	public int getBeanPostProcessorCount() {
		return getBeanFactory().getBeanPostProcessorCount();
	}

	@Override
	public void registerDependentBean(String beanName, String dependentBeanName) {
		getBeanFactory().registerDependentBean(beanName, dependentBeanName);
	}

	@Override
	public String[] getDependentBeans(String beanName) {
		return getBeanFactory().getDependentBeans(beanName);
	}

	@Override
	public String[] getDependenciesForBean(String beanName) {
		return getBeanFactory().getDependenciesForBean(beanName);
	}

	
	@Override
	public String getApplicationName() {
		return "";
	}
	@Override
	public long getStartupDate() {
		return this.startupDate;
	}
	@Override
	public abstract ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;
	
	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
	
	@Override
	public Environment getEnvironment() {
		return this.environment;
	}
	
	@Override
	public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
		this.beanFactoryPostProcessors.add(postProcessor);
	}
	

	@Override
	public void close() {
	}
	
	@Override
	public boolean isActive() {
		return true;
	}

	public ApplicationEventPublisher getApplicationEventPublisher() {
		return applicationEventPublisher;
	}

	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}
}
