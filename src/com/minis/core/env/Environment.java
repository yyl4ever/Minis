package com.minis.core.env;

/**
 * 环境因素使一些容器整体所需要的属性有个地方存储访问
 */
public interface Environment extends PropertyResolver {
	String[] getActiveProfiles();

	String[] getDefaultProfiles();

	boolean acceptsProfiles(String... profiles);

}

