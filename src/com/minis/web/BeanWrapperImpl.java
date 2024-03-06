package com.minis.web;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.minis.beans.AbstractPropertyAccessor;
import com.minis.beans.PropertyEditor;
import com.minis.beans.PropertyEditorRegistrySupport;
import com.minis.beans.PropertyValue;
import com.minis.beans.PropertyValues;

public class BeanWrapperImpl extends AbstractPropertyAccessor {
    /**
     * 目标对象
     */
    Object wrappedObject;
    /**
     * 目标对象的 clazz
     */
    Class<?> clz;

    public BeanWrapperImpl(Object object) {
        super();
        this.wrappedObject = object;
        this.clz = object.getClass();
    }

    /**
     * 绑定参数值
     *
     * @param pv
     */
    @Override
    public void setPropertyValue(PropertyValue pv) {
        // 拿到参数处理器
        BeanPropertyHandler propertyHandler = new BeanPropertyHandler(pv.getName());
        // 找到该参数类型的 editor，优先获取自定义的 editor
        PropertyEditor pe = this.getCustomEditor(propertyHandler.getPropertyClz());
        if (pe == null) {
            pe = this.getDefaultEditor(propertyHandler.getPropertyClz());

        }
        if (pe != null) {
            // 设置参数值 string-->具体类型
            pe.setAsText((String) pv.getValue());
            // pe.getValue() 的值由上一步已经赋值，参数绑定成功
            propertyHandler.setValue(pe.getValue());
        } else {
            propertyHandler.setValue(pv.getValue());
        }

    }

    /**
     * 内部类，用于处理参数
     */
    class BeanPropertyHandler {
        Method writeMethod = null;
        Method readMethod = null;
        Class<?> propertyClz = null;

        public Class<?> getPropertyClz() {
            return propertyClz;
        }

        public BeanPropertyHandler(String propertyName) {
            try {
                // clz 是在初始化 webBinder 时产生的
                // 获取参数对应的属性及类型，比如 Person.class 有个 name 属性 为 String 类型
                Field field = clz.getDeclaredField(propertyName);
                propertyClz = field.getType();
                // 获取设置属性的方法
                this.writeMethod = clz.getDeclaredMethod("set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1), propertyClz);
                // 获取读取属性的方法
                this.readMethod = clz.getDeclaredMethod("get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        /**
         * 调用 getter 方法读取属性值
         *
         * @return
         */
        public Object getValue() {
            Object result = null;
            // 加上这句是习惯
            readMethod.setAccessible(true);

            try {
                result = readMethod.invoke(wrappedObject);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return result;

        }

        public void setValue(Object value) {
            writeMethod.setAccessible(true);
            try {
                writeMethod.invoke(wrappedObject, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

    }

}
