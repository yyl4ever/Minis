package com.minis.web;


import javax.servlet.ServletContext;

import com.minis.context.ClassPathXmlApplicationContext;

/**
 * 在 ClassPathXmlApplicationContext 基础上，增加了 servletContext 属性，形成一个适用于 web 场景的上下文
 */
public class AnnotationConfigWebApplicationContext 
					extends ClassPathXmlApplicationContext implements WebApplicationContext{
	private ServletContext servletContext;
	
	public AnnotationConfigWebApplicationContext(String fileName) {
		super(fileName);
	}

	@Override
	public ServletContext getServletContext() {
		return this.servletContext;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

}
