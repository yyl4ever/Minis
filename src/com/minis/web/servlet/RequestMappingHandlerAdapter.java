package com.minis.web.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.minis.web.WebApplicationContext;

public class RequestMappingHandlerAdapter implements HandlerAdapter {
	WebApplicationContext wac;

	public RequestMappingHandlerAdapter(WebApplicationContext wac) {
		this.wac = wac;
	}

	/**
	 * 接受前端请求，通过反射调用方法
	 * @param request
	 * @param response
	 * @param handler
	 * @throws Exception
	 */
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		handleInternal(request, response, (HandlerMethod) handler);
	}

	/**
	 * @param request
	 * @param response
	 * @param handler 封装了当前请求的 url，和对应请求指向的 bean
	 */
	private void handleInternal(HttpServletRequest request, HttpServletResponse response,
			HandlerMethod handler) {

			Method method = handler.getMethod();
			Object obj = handler.getBean();
			Object objResult = null;
			try {
				objResult = method.invoke(obj);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		
		try {
			response.getWriter().append(objResult.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
