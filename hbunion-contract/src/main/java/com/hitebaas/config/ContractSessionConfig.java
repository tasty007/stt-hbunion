package com.hitebaas.config;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContractSessionConfig  {

	private static final Map<String, HttpSession> hsMap = new HashMap<String, HttpSession>();
	
	@Bean
	public HttpSessionListener httpSessionListener() {
		return new HttpSessionListener() {
			
			@Override
			public void sessionDestroyed(HttpSessionEvent se) {
				String key = se.getSession().getId();
				hsMap.remove(key);
			}
			
			@Override
			public void sessionCreated(HttpSessionEvent se) {
				String key = se.getSession().getId();
				HttpSession value = se.getSession();
				hsMap.put(key, value);
			}
		};
	}
	
	public static HttpSession findSession(String sessionId){
		return hsMap.get(sessionId);
	}

}
