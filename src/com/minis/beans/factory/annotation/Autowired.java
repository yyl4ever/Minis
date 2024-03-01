package com.minis.beans.factory.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

@Target(ElementType.FIELD) // 修饰成员变量
@Retention(RetentionPolicy.RUNTIME) // 运行时生效
public @interface Autowired {

}
