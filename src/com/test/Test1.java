package com.test;

import com.minis.beans.BeansException;
import com.minis.context.ClassPathXmlApplicationContext;
import com.test.service.AService;

/**
 * @author qiuyun
 * @version 1.0
 * @since 2024-02-29 00:49
 */
public class Test1 {

    public static void main(String[] args) throws BeansException {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
        AService aservice = (AService) ctx.getBean("applicationContext.xml");
        aservice.sayHello();
    }
}
