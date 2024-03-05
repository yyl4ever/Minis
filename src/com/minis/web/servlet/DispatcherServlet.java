package com.minis.web.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.minis.beans.BeansException;
import com.minis.beans.factory.annotation.Autowired;
import com.minis.web.AnnotationConfigWebApplicationContext;
import com.minis.web.RequestMapping;
import com.minis.web.WebApplicationContext;
import com.minis.web.XmlScanComponentHelper;
import com.test.HelloWorldBean;

/**
 * Servlet implementation class DispatcherServlet
 * 整个系统用唯一一个 DispatcherServlet 来处理请求。请求和 bean 管理解耦
 * TODO：父上下文 refresh 的时候不会将 controller 实例初始化吗？
 */
public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String WEB_APPLICATION_CONTEXT_ATTRIBUTE = DispatcherServlet.class.getName() + ".CONTEXT";
	/**
	 * web 应用上下文，由 dispatcher 加载产生的
	 */
	private WebApplicationContext webApplicationContext;
	/**
	 * 父上下文：ioc 容器
	 */
	private WebApplicationContext parentApplicationContext;
	
    private String sContextConfigLocation;
    private List<String> packageNames = new ArrayList<>();
    private Map<String,Object> controllerObjs = new HashMap<>();
    private List<String> controllerNames = new ArrayList<>();
    private Map<String,Class<?>> controllerClasses = new HashMap<>();    
    
	private HandlerMapping handlerMapping;
	private HandlerAdapter handlerAdapter;

    public DispatcherServlet() {
        super();
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException {
    	super.init(config);
    	
    	
    	this.parentApplicationContext = 
    			(WebApplicationContext) this.getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
    	
        sContextConfigLocation = config.getInitParameter("contextConfigLocation");
        
        URL xmlPath = null;
		try {
			xmlPath = this.getServletContext().getResource(sContextConfigLocation);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
        
        this.packageNames = XmlScanComponentHelper.getNodeValue(xmlPath);
        // 传参：servlet 路径、父上下文(ioc容器)
    	this.webApplicationContext = new AnnotationConfigWebApplicationContext(sContextConfigLocation,this.parentApplicationContext);


        Refresh();
        
    }
    
    protected void Refresh() {
    	initController();
    	// 扫描 servlet 中配置的 controller 路径
		initHandlerMappings(this.webApplicationContext);
		initHandlerAdapters(this.webApplicationContext);
		initViewResolvers(this.webApplicationContext);
    }
    
    protected void initHandlerMappings(WebApplicationContext wac) {
    	this.handlerMapping = new RequestMappingHandlerMapping(wac);
    	
    }
    protected void initHandlerAdapters(WebApplicationContext wac) {
    	this.handlerAdapter = new RequestMappingHandlerAdapter(wac);
    	
    }
    protected void initViewResolvers(WebApplicationContext wac) {
    	
    }
    
    protected void initController() {
    	this.controllerNames = Arrays.asList(this.webApplicationContext.getBeanDefinitionNames());
    	for (String controllerName : this.controllerNames) {
			try {
				this.controllerClasses.put(controllerName,Class.forName(controllerName));
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
			try {
				this.controllerObjs.put(controllerName,this.webApplicationContext.getBean(controllerName));
		    	System.out.println("controller : "+controllerName);
			} catch (BeansException e) {
				e.printStackTrace();
			}
    	}

    }
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.webApplicationContext);

		try {
			doDispatch(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
		}
	}
	
	protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpServletRequest processedRequest = request;
		HandlerMethod handlerMethod = null;
		
		handlerMethod = this.handlerMapping.getHandler(processedRequest);
		if (handlerMethod == null) {
			return;
		}
		// 包装 url 映射后的处理过程
		HandlerAdapter ha = this.handlerAdapter;

		ha.handle(processedRequest, response, handlerMethod);
	}
	

}
