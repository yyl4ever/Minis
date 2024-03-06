package com.minis.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.minis.beans.AbstractPropertyAccessor;
import com.minis.beans.PropertyEditor;
import com.minis.beans.PropertyValues;
import com.minis.util.WebUtils;

public class WebDataBinder {
	private Object target;
	private Class<?> clz;

	private String objectName;
	AbstractPropertyAccessor propertyAccessor;
	
	public WebDataBinder(Object target) {
		this(target,"");
	}

	/**
	 *
	 * @param target 比如 Person.class
	 * @param targetName 比如 person
	 */
	public WebDataBinder(Object target, String targetName) {
		this.target = target;
		this.objectName = targetName;
		this.clz = this.target.getClass();
		this.propertyAccessor = new BeanWrapperImpl(this.target);
	}

	/**
	 * 核心绑定方法，将request里面的参数值绑定到目标对象的属性上
	 * @param request
	 */
	public void bind(HttpServletRequest request) {
		PropertyValues mpvs = assignParameters(request);
		addBindValues(mpvs, request);
		doBind(mpvs);
	}
	
	private void doBind(PropertyValues mpvs) {
		applyPropertyValues(mpvs);
		
	}

	/**
	 * 将参数值与对象属性进行绑定
	 * @param mpvs
	 */
	protected void applyPropertyValues(PropertyValues mpvs) {
		getPropertyAccessor().setPropertyValues(mpvs);
	}

	/**
	 * 设定属性值的工具
	 * @return
	 */
	protected AbstractPropertyAccessor getPropertyAccessor() {
		return this.propertyAccessor;
	}

	/**
	 * 将 request 解析为 PropertyValues
	 * @param request
	 * @return
	 */
	private PropertyValues assignParameters(HttpServletRequest request) {
		Map<String,Object> map = WebUtils.getParametersStartingWith(request, "");
		
		return new PropertyValues(map);
	}

	/**
	 * 注册自定义的入参转换器
	 * @param requiredType
	 * @param propertyEditor
	 */
	public void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor) {
		getPropertyAccessor().registerCustomEditor(requiredType, propertyEditor);
	}
	
	protected void addBindValues(PropertyValues mpvs, HttpServletRequest request) {
	}

}
