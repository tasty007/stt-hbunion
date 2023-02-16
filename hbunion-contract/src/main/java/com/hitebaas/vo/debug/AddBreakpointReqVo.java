package com.hitebaas.vo.debug;

import java.io.Serializable;

public class AddBreakpointReqVo implements Serializable{
	
	private String op;
	
	private int line;

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}
	
}
