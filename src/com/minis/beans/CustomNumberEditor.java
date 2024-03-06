package com.minis.beans;

import java.text.NumberFormat;

import com.minis.util.NumberUtils;
import com.minis.util.StringUtils;

public class CustomNumberEditor implements PropertyEditor {
    /**
     * 数据类型
     */
    private Class<? extends Number> numberClass;
    /**
     * 指定格式
     */
    private NumberFormat numberFormat;
    private boolean allowEmpty;
    private Object value;

    public CustomNumberEditor(Class<? extends Number> numberClass,
                              boolean allowEmpty) throws IllegalArgumentException {
        this(numberClass, null, allowEmpty);
    }

    public CustomNumberEditor(Class<? extends Number> numberClass,
                              NumberFormat numberFormat, boolean allowEmpty) throws IllegalArgumentException {
        this.numberClass = numberClass;
        this.numberFormat = numberFormat;
        this.allowEmpty = allowEmpty;
    }

    /**
     * 将字符串转换为 number
     *
     * @param text
     */
    @Override
    public void setAsText(String text) {
        if (this.allowEmpty && !StringUtils.hasText(text)) {
            // Treat empty String as null value.
            setValue(null);
        } else if (this.numberFormat != null) { // 给定格式
            // Use given NumberFormat for parsing text.
            setValue(NumberUtils.parseNumber(text, this.numberClass, this.numberFormat));
        } else {
            // Use default valueOf methods for parsing text.
            setValue(NumberUtils.parseNumber(text, this.numberClass));
        }
    }

    /**
     * 将 Object 作为参数，转换为实际的参数值
     *
     * @param value
     */
    @Override
    public void setValue(Object value) {
        if (value instanceof Number) {
            this.value = (NumberUtils.convertNumberToTargetClass((Number) value, this.numberClass));
        } else {
            this.value = value;
        }
    }

    /**
     * 将 number 转换为字符串
     *
     * @return
     */
    @Override
    public String getAsText() {
        Object value = this.value;
        if (value == null) {
            return "";
        }
        if (this.numberFormat != null) {
            // Use NumberFormat for rendering value.
            return this.numberFormat.format(value);
        } else {
            // Use toString method for rendering value.
            return value.toString();
        }
    }

    @Override
    public Object getValue() {
        return this.value;
    }

}
