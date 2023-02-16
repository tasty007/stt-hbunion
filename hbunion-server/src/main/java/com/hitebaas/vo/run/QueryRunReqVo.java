package com.hitebaas.vo.run;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.hitebaas.entity.ParamReqInfo;


public class QueryRunReqVo {
	
	private String contractName;
	
	private String funcName;
	
	private List<ParamReqInfo> pris;
	
	private BigDecimal gas;
	
	public QueryRunReqVo() {
		pris = new ArrayList<ParamReqInfo>();
	}
	
	public String getContractName() {
		return contractName;
	}
	public void setContractName(String contractName) {
		this.contractName = contractName;
	}
	public String getFuncName() {
		return funcName;
	}
	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}
	public List<ParamReqInfo> getPris() {
		return pris;
	}
	public void setPris(List<ParamReqInfo> pris) {
		this.pris = pris;
	}

	public BigDecimal getGas() {
		return gas;
	}

	public void setGas(BigDecimal gas) {
		this.gas = gas;
	}
	
}
