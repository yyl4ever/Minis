package com.minis.beans;

/**
 * 字符串与 Object 进行转换
 */
public interface PropertyEditor {
	void setAsText(String text);
	void setValue(Object value);
	Object getValue();
	String getAsText();
}
