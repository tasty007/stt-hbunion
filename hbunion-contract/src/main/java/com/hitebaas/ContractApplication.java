package com.hitebaas;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.alibaba.druid.pool.DruidDataSource;
import com.hitebaas.utils.ContractCacheUtils;


@ComponentScan({"com.hitebaas"})
@SpringBootApplication
@EnableScheduling
@ServletComponentScan
@EnableCaching

@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class ContractApplication extends SpringBootServletInitializer{
	
	public static void main(String[] args) {
		ConfigurableApplicationContext configContext = SpringApplication.run(ContractApplication.class, args);
		
		
        
		
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ContractApplication.class);
    }
	
	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer() {
		return new EmbeddedServletContainerCustomizer() {
			@Override
			public void customize(ConfigurableEmbeddedServletContainer container) {
				
			}
		};
	}
	
	@Bean
	public ContractCacheUtils contractCacheUtils() {
		return new ContractCacheUtils();
	}
	
	@Bean(destroyMethod = "close") 
    @ConfigurationProperties(prefix="spring.datasource") 
    public DataSource dataSource(){
        return new DruidDataSource();
    }

}
