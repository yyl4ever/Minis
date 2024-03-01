package com.minis.beans.factory.annotation;

import java.lang.reflect.Field;

import com.minis.beans.BeansException;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.BeanFactoryAware;
import com.minis.beans.factory.config.BeanPostProcessor;

public class AutowiredAnnotationBeanPostProcessor implements BeanPostProcessor,BeanFactoryAware {
	private BeanFactory beanFactory;

	/**
	 * bean 初始化前
	 * @param bean
	 * @param beanName
	 * @return
	 * @throws BeansException
	 */
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		Object result = bean;
		
		Class<?> clazz = bean.getClass();
		Field[] fields = clazz.getDeclaredFields();
		if(fields!=null){
			// 遍历每一个属性
			for(Field field : fields){
				boolean isAutowired = field.isAnnotationPresent(Autowired.class);
				// 带有 @Autowired 注解
				if(isAutowired){
					String fieldName = field.getName();
					// 根据属性名查找同名的 bean
					Object autowiredObj = this.getBeanFactory().getBean(fieldName);
					try {
						field.setAccessible(true);
						// 注入这个 bean
						field.set(bean, autowiredObj);
						System.out.println("autowire " + fieldName + " for bean " + beanName);
						System.out.println("autowire " + fieldName + " for bean " + beanName + " : " + autowiredObj + " class : "+autowiredObj.getClass());
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}

				}
			}
		}
		
		return result;
	}

	/**
	 * bean 初始化后
	 * @param bean
	 * @param beanName
	 * @return
	 * @throws BeansException
	 */
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		// TODO Auto-generated method stub
		return bean;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}


}
