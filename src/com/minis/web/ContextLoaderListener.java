package com.minis.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 当 Sevlet 服务器启动时，Listener 会优先启动，读配置文件 路径，启动过程中初始化上下文，
 * 然后启动 IoC 容器，这个容器通过 refresh() 方法加载所管 理的 Bean 对象。这样就实现了 Tomcat 启动的时候同时启动 IoC 容器。
 *
 *
 */
public class ContextLoaderListener implements ServletContextListener {
    /**
     * ioc 容器的配置文件
     */
    public static final String CONFIG_LOCATION_PARAM = "contextConfigLocation";
    private WebApplicationContext context;

    public ContextLoaderListener() {
    }

    public ContextLoaderListener(WebApplicationContext context) {
        this.context = context;
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        initWebApplicationContext(event.getServletContext());
    }

    private void initWebApplicationContext(ServletContext servletContext) {
        String sContextLocation = servletContext.getInitParameter(CONFIG_LOCATION_PARAM);
        System.out.println("sContextLocation-----------" + sContextLocation);
        // wac 主体上就是我们理解的 ioc 容器
        WebApplicationContext wac = new AnnotationConfigWebApplicationContext(sContextLocation);
        wac.setServletContext(servletContext);
        this.context = wac;
        // wac 和 servletContext 形成一个相互引用的关系
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.context);
    }


}
