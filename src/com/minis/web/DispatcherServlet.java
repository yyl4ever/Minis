package com.minis.web;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.minis.test.HelloWorldBean;

/**
 * Servlet implementation class DispatcherServlet
 */
public class DispatcherServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private String sContextConfigLocation;
    /**
     * 存储扫描的 package
     */
    private List<String> packageNames = new ArrayList<>();
    /**
     * {controll全限定名称, 对应对象}
     */
    private Map<String, Object> controllerObjs = new HashMap<>();
    /**
     * controll全限定名称列表
     */
    private List<String> controllerNames = new ArrayList<>();
    /**
     * {controll全名称, 对应类}
     */
    private Map<String, Class<?>> controllerClasses = new HashMap<>();
    /**
     * 保存 url 列表
     */
    private List<String> urlMappingNames = new ArrayList<>();
    /**
     * {url, 对象}
     */
    private Map<String, Object> mappingObjs = new HashMap<>();
    /**
     * {url, 方法}
     */
    private Map<String, Method> mappingMethods = new HashMap<>();

    public DispatcherServlet() {
        super();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        sContextConfigLocation = config.getInitParameter("contextConfigLocation");

        URL xmlPath = null;
        try {
            xmlPath = this.getServletContext().getResource(sContextConfigLocation);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // servlet.xml 中扫描的 package 存入列表
        this.packageNames = XmlScanComponentHelper.getNodeValue(xmlPath);

        Refresh();

    }

    protected void Refresh() {
        //  初始化 controller
        initController();
        // 初始化 url 映射
        initMapping();
    }

    protected void initController() {
        // 扫描包，获取所有的 controller 名称
        this.controllerNames = scanPackages(this.packageNames);

        for (String controllerName : this.controllerNames) {
            Object obj = null;
            Class<?> clz = null;

            try {
                // 加载类
                clz = Class.forName(controllerName);
                // 缓存
                this.controllerClasses.put(controllerName, clz);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                // 实例化 bean
                obj = clz.newInstance();
                this.controllerObjs.put(controllerName, obj);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private List<String> scanPackages(List<String> packages) {
        List<String> tempControllerNames = new ArrayList<>();
        for (String packageName : packages) {
            tempControllerNames.addAll(scanPackage(packageName));
        }
        return tempControllerNames;
    }

    private List<String> scanPackage(String packageName) {
        List<String> tempControllerNames = new ArrayList<>();
        // 将以「.」分隔的包名换成以「/」分隔的 url
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
        // 处理对应的文件目录
        File dir = new File(url.getFile());
        // 目录下的文件或者子目录
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                // 递归处理文件目录
                scanPackage(packageName + "." + file.getName());
            } else {
                // 类名
                String controllerName = packageName + "." + file.getName().replace(".class", "");
                tempControllerNames.add(controllerName);
            }
        }
        return tempControllerNames;
    }

    protected void initMapping() {
        for (String controllerName : this.controllerNames) {
            Class<?> clazz = this.controllerClasses.get(controllerName);
            Object obj = this.controllerObjs.get(controllerName);
            Method[] methods = clazz.getDeclaredMethods();
            if (methods != null) {
                for (Method method : methods) {
                    // 找到使用了 @RequestMapping 的方法
                    boolean isRequestMapping = method.isAnnotationPresent(RequestMapping.class);
                    if (isRequestMapping) {
                        String methodName = method.getName();
                        String urlmapping = method.getAnnotation(RequestMapping.class).value();
                        // 存储 url
                        this.urlMappingNames.add(urlmapping);
                        // url 和映射的对象
                        this.mappingObjs.put(urlmapping, obj);
                        // url 和映射的方法
                        this.mappingMethods.put(urlmapping, method);
                    }
                }
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取请求的 path
        String sPath = request.getServletPath();
        System.out.println(sPath);
        if (!this.urlMappingNames.contains(sPath)) {
            return;
        }

        Object obj = null;
        Object objResult = null;
        try {
            // 获取调用方法名
            Method method = this.mappingMethods.get(sPath);
            // 获取 bean 实例
            obj = this.mappingObjs.get(sPath);
            // 方法调用
            objResult = method.invoke(obj);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        // 将方法返回值写入 response
        response.getWriter().append(objResult.toString());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
