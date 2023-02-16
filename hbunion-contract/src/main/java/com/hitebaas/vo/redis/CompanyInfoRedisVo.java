package com.hitebaas.vo.redis;

import java.text.DecimalFormat;
import java.util.List;

import cn.hutool.core.util.StrUtil;

public class CompanyInfoRedisVo {

	
	private String companyId;
	
	
	private List<String> contractOwnerCompanyId;
	
	
	private String code;
	
	
	private String contractName;
	
	
	private String integral;
	
	
	private String accountBalance ;
	
	
	private String version ;
	
	
	private String remark;

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public List<String> getContractOwnerCompanyId() {
		return contractOwnerCompanyId;
	}

	public void setContractOwnerCompanyId(List<String> contractOwnerCompanyId) {
		this.contractOwnerCompanyId = contractOwnerCompanyId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}


	public String getContractName() {
		return contractName;
	}

	public void setContractName(String contractName) {
		this.contractName = contractName;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	@Override
	public String toString() {
		return "CompanyInfoRedisVo [ "+ "version=" + version + ", companyId=" + companyId
				+ ", contractOwnerCompanyId=" + (contractOwnerCompanyId==null ? "null" : contractOwnerCompanyId.toString())+ ", contractName=" + contractName 
				+ ", code=" + code + ", integral=" + (StrUtil.isBlank(integral) ? "0" : integral)
				+ ", accountBalance=" + (StrUtil.isBlank(accountBalance) ? "0" : new DecimalFormat("0.000").format(accountBalance))  
				+ ", remark=" + remark + "]";
	}


	
	
}
