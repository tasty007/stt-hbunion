package com.hitebaas.adapter;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class ScriptConfigurerAdapter extends WebMvcConfigurerAdapter{

	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("*").allowedHeaders("*")
		.allowedMethods("GET", "HEAD", "POST","PUT", "DELETE", "OPTIONS","PATCH","TRACE")
		.allowCredentials(true).maxAge(13600);
		super.addCorsMappings(registry);
	}
}
