package com.minis.beans;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public abstract class AbstractPropertyAccessor extends PropertyEditorRegistrySupport{
	/**
	 * 参数值
	 */
	PropertyValues pvs;
	
	public AbstractPropertyAccessor() {
		super();

	}

	/**
	 * 绑定参数值
	 * @param pvs
	 */
	public void setPropertyValues(PropertyValues pvs) {
		this.pvs = pvs;
		// 绑定某个具体的参数
		for (PropertyValue pv : this.pvs.getPropertyValues()) {
			setPropertyValue(pv);
		}
	}
	
	public abstract void setPropertyValue(PropertyValue pv) ;

}
