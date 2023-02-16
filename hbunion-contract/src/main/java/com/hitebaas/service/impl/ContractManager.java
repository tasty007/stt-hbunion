package com.hitebaas.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hitebaas.helper.IContractManager;
import com.hitebaas.tvm.inns.frame.vm.TsRunTime;
import com.hitebaas.utils.ContractCacheUtils;


@Component
public class ContractManager extends IContractManager{
	
	@Autowired
	private ContractCacheUtils contractCacheUtils;

	@Override
	public TsRunTime getTsRunTime(String key) {
		return contractCacheUtils.getTsRunTime(key);
	}

	@Override
	public void putTsRunTime(String key, TsRunTime trt) {
		contractCacheUtils.putTsRunTime(key, trt);
	}

	@Override
	public boolean exist(String key) {
		return contractCacheUtils.exist(key);
	}
	
	
}
