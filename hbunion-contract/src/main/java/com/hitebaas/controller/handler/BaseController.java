package com.hitebaas.controller.handler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.hitebaas.handler.DealHandler;



public class BaseController extends DealHandler{
	private String ctx;
	
	public BaseController() {
		super();
	}
	
	protected void write(String jsonString){
		PrintWriter out = null;
		try {
			this.getResponse().setContentType("text/json;charset=utf-8");  
			this.getResponse().setCharacterEncoding("utf-8");  
			this.getResponse().setHeader("Charset", "utf-8");  
			this.getResponse().setHeader("Cache-Control", "no-cache"); 
			out = this.getResponse().getWriter();
			out.write(jsonString);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			  if(out != null){
				out.flush(); 
				out.close();
			}
		}
	}
	
	public HttpServletRequest getRequest() {
		return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
	}
	
	public ServletContext getServletContext(){
		return getRequest().getServletContext();
		
	}
	
	public HttpServletResponse getResponse() {
		return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();
	}

	
	public String getCtx() {
		if(StringUtils.isBlank(ctx)){
			ctx = getRequest().getContextPath();
		}
		return ctx;
	}
	
	public HttpSession getSession() {
		return getRequest().getSession();
	}
	
	public <T> T getBean(Class<T> clazz) {
		T cm = ContextLoader.getCurrentWebApplicationContext().getBean(clazz);
		return cm;
	}
}
