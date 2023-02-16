package com.hitebaas.utils;

import java.net.URL;

import org.apache.jasper.tagplugins.jstl.core.Url;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.hitebaas.tvm.inns.frame.vm.TsRunTime;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class ContractCacheUtils {
	private final String CONTRACTCACHE = "contractCache";
	private final String XMLCONFIG = "/ehcache.xml";
	public CacheManager cacheManager;
	public Cache cache;
	
	
	private Cache getTsRunTimeCache() {
		return getCache(CONTRACTCACHE);
	}

	
	public TsRunTime getTsRunTime(String key) {
		Element element = getTsRunTimeCache().get(key);
		if(element != null) {
			return (TsRunTime) element.getObjectValue();
		}
		return null;
	}
	
	public void putTsRunTime(String key, TsRunTime trt) {
		TsRunTime old = getTsRunTime(key);
		if(old != null) {
			getTsRunTimeCache().remove(key);
		}
		Element element = new Element(key, trt);
		getTsRunTimeCache().put(element);
	}
	
	public boolean exist(String key) {
		return getTsRunTimeCache().isKeyInCache(key);
	}
	
	private Cache getCache(String cacheKey) {
		if(cacheManager == null) {
			try {
				
				
				String path = ContractCacheUtils.class.getResource(XMLCONFIG).getPath();
				
				cacheManager = CacheManager.create(path);
				

				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		cache = cacheManager.getCache(cacheKey);
		return cache;
	}
}
