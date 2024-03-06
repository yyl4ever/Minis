package com.minis.web.servlet;

public interface ViewResolver {
	/**
	 * 根据视图名称找到实际的视图位置
	 * @param viewName
	 * @return
	 * @throws Exception
	 */
	View resolveViewName(String viewName) throws Exception;
}
