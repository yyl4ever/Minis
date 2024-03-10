package com.minis.aop;

import com.minis.beans.BeansException;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.BeanFactoryAware;
import com.minis.beans.factory.FactoryBean;
import com.minis.util.ClassUtils;

public class ProxyFactoryBean  implements FactoryBean<Object>,BeanFactoryAware {
	private BeanFactory beanFactory;
	private AopProxyFactory aopProxyFactory;
	/**
	 * 拦截器名称，用于 ioc 容器注入
	 */
	private String interceptorName;
	private String targetName;
	private Object target;
	private ClassLoader proxyClassLoader = ClassUtils.getDefaultClassLoader();
	private Object singletonInstance;
	/**
	 * 内部设置拦截器，advisor 里面设置 advice
	 */
	private Advisor advisor;

	public ProxyFactoryBean() {
		this.aopProxyFactory = new DefaultAopProxyFactory();
	}
	
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public void setAopProxyFactory(AopProxyFactory aopProxyFactory) {
		this.aopProxyFactory = aopProxyFactory;
	}
	public AopProxyFactory getAopProxyFactory() {
		return this.aopProxyFactory;
	}

	public void setInterceptorName(String interceptorName) {
		this.interceptorName = interceptorName;
	}
	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}
	public Object getTarget() {
		return target;
	}
	public void setTarget(Object target) {
		this.target = target;
	}

	@Override
	public Object getObject() throws Exception {
		initializeAdvisor();
		return getSingletonInstance();
	}

	/**
	 * 设置拦截器
	 */
	private synchronized void initializeAdvisor() {
		Object advice = null;
		MethodInterceptor mi = null;
		try {
			advice = this.beanFactory.getBean(this.interceptorName);
		} catch (BeansException e) {
			e.printStackTrace();
		}
		if (advice instanceof BeforeAdvice) {
			mi = new MethodBeforeAdviceInterceptor((MethodBeforeAdvice)advice);
		}
		else if (advice instanceof AfterAdvice){
			mi = new AfterReturningAdviceInterceptor((AfterReturningAdvice)advice);
		}
		else if (advice instanceof MethodInterceptor) {
			mi = (MethodInterceptor)advice;
		}
		
		advisor = new DefaultAdvisor();
		advisor.setMethodInterceptor(mi);

	}

	private synchronized Object getSingletonInstance() {
		if (this.singletonInstance == null) {
			this.singletonInstance = getProxy(createAopProxy());
		}
		return this.singletonInstance;
	}
	protected AopProxy createAopProxy() {
		// 传入增强器 advisor
		return getAopProxyFactory().createAopProxy(target,this.advisor);
	}
	protected Object getProxy(AopProxy aopProxy) {
		return aopProxy.getProxy();
	}
	
	@Override
	public Class<?> getObjectType() {
		return null;
	}

}
