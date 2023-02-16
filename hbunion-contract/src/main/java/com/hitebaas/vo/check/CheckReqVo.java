package com.hitebaas.vo.check;

import java.math.BigDecimal;


public class CheckReqVo {
	
	private BigDecimal gas;
	
	private String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public BigDecimal getGas() {
		return gas;
	}

	public void setGas(BigDecimal gas) {
		this.gas = gas;
	}
	
}
