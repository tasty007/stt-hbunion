package com.hitebaas.vo.run;

import java.io.File;
import java.util.Map;

import com.google.gson.Gson;
import com.hitebaas.utils.EncryptUtil;

public class DataHelperEntity2{

	
	
	
	private String t;
	
	private String appId;
	
	private String appKey;
	
	private String secretKey;
	
	private Map<String,File> files;



	
	

	public String getT() {
		return t;
	}

	public void setT(String t) {
		this.t = t;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public Map<String, File> getFiles() {
		return files;
	}

	public void setFiles(Map<String, File> files) {
		this.files = files;
	}
	
	

	
	
}
