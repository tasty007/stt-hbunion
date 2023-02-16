package com.hitebaas.vo.run;

import java.math.BigDecimal;
import java.util.List;

import com.hitebaas.entity.ParamReqInfo;

public class RunReqVo {
	
	private BigDecimal gas;
	
	private String contractName;
	
	private String funcName;
	
	private List<ParamReqInfo> pris;
	
	private String callAddress;
	
	private String ownerAddress;
	
	private String integral;
	
	
	private String accountBalance ;
	
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
	public String getCallAddress() {
		return callAddress;
	}
	public void setCallAddress(String callAddress) {
		this.callAddress = callAddress;
	}
	public String getOwnerAddress() {
		return ownerAddress;
	}
	public void setOwnerAddress(String ownerAddress) {
		this.ownerAddress = ownerAddress;
	}
	public BigDecimal getGas() {
		return gas;
	}
	public void setGas(BigDecimal gas) {
		this.gas = gas;
	}
	public String getContractName() {
		return contractName;
	}
	public void setContractName(String contractName) {
		this.contractName = contractName;
	}
	public String getIntegral() {
		return integral;
	}
	public void setIntegral(String integral) {
		this.integral = integral;
	}
	public String getAccountBalance() {
		return accountBalance;
	}
	public void setAccountBalance(String accountBalance) {
		this.accountBalance = accountBalance;
	}
}
