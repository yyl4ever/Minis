package com.minis.web.servlet;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.minis.beans.BeansException;
import com.minis.web.WebApplicationContext;
import com.minis.web.WebBindingInitializer;
import com.minis.web.WebDataBinder;
import com.minis.web.WebDataBinderFactory;

public class RequestMappingHandlerAdapter implements HandlerAdapter {
	WebApplicationContext wac = null;
	private WebBindingInitializer webBindingInitializer = null;

	public RequestMappingHandlerAdapter(WebApplicationContext wac) {
		this.wac = wac;
		try {
			// 用于自定义数据转换器的注入
			this.webBindingInitializer = (WebBindingInitializer) this.wac.getBean("webBindingInitializer");
		} catch (BeansException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		return handleInternal(request, response, (HandlerMethod) handler);
	}

	private ModelAndView handleInternal(HttpServletRequest request, HttpServletResponse response,
			HandlerMethod handler) {
		ModelAndView mv = null;
		
		try {
			 invokeHandlerMethod(request, response, handler);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mv;

	}

	/**
	 *
	 * @param request
	 * @param response
	 * @param handlerMethod 某个处理请求的方法，比如 /sayHi?name=yyl 对应的 sayHi(Person person) 方法
	 * @throws Exception
	 */
	protected void invokeHandlerMethod(HttpServletRequest request,
			HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {


			WebDataBinderFactory binderFactory = new WebDataBinderFactory();

			// 存储方法的所有参数
			Parameter[] methodParameters = handlerMethod.getMethod().getParameters();
			Object[] methodParamObjs = new Object[methodParameters.length];
			
			int i = 0;
			// 对调用方法里的每一个参数进行绑定，按照参数出现的次序进行绑定
			for (Parameter methodParameter : methodParameters) {
				// 空对象，需要绑定的操作目标，比如 Person.class 的实例
				Object methodParamObj = methodParameter.getType().newInstance();
				// 给这个参数创建 WebDataBinder
				WebDataBinder wdb = binderFactory.createBinder(request, methodParamObj, methodParameter.getName());
				// 注册自定义的 editor，方便进行自定义参数绑定
				webBindingInitializer.initBinder(wdb);
				// 完成参数的绑定
				wdb.bind(request);
				methodParamObjs[i] = methodParamObj;
				i++;
			}
			
			Method invocableMethod = handlerMethod.getMethod();
			Object returnobj = invocableMethod.invoke(handlerMethod.getBean(), methodParamObjs);
			
			response.getWriter().append(returnobj.toString());
			//ModelFactory modelFactory = getModelFactory(handlerMethod, binderFactory);

//			ServletInvocableHandlerMethod invocableMethod = handlerMethod.getMethod();
//			if (this.argumentResolvers != null) {
//				invocableMethod.setHandlerMethodArgumentResolvers(this.argumentResolvers);
//			}
//			if (this.returnValueHandlers != null) {
//				invocableMethod.setHandlerMethodReturnValueHandlers(this.returnValueHandlers);
//			}
//			invocableMethod.setDataBinderFactory(binderFactory);
//			invocableMethod.setParameterNameDiscoverer(this.parameterNameDiscoverer);
//
//			ModelAndViewContainer mavContainer = new ModelAndViewContainer();
//			mavContainer.addAllAttributes(RequestContextUtils.getInputFlashMap(request));
//			modelFactory.initModel(webRequest, mavContainer, invocableMethod);
//			mavContainer.setIgnoreDefaultModelOnRedirect(this.ignoreDefaultModelOnRedirect);


//			invocableMethod.invokeAndHandle(webRequest, mavContainer);

//			return getModelAndView(mavContainer, modelFactory, webRequest);


	}

	public WebBindingInitializer getWebBindingInitializer() {
		return webBindingInitializer;
	}

	public void setWebBindingInitializer(WebBindingInitializer webBindingInitializer) {
		this.webBindingInitializer = webBindingInitializer;
	}


}
