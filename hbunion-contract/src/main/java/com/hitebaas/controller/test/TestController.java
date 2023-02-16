package com.hitebaas.controller.test;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hitebaas.controller.handler.BaseController;
import com.hitebaas.entity.trade.SttkScriptContractTradeBody;
import com.hitebaas.helper.ContractApiHelper;
import com.hitebaas.tvm.inns.frame.vm.TsRunTime;
import com.hitebaas.util.ContractConstant;
import com.hitebaas.vo.base.BaseContentRespVo;

@RestController
public class TestController extends BaseController{
	
	private static final Logger logger = Logger.getLogger(TestController.class);
	
	@PostMapping(value="/dev/transfer1", produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
	public ResponseEntity<BaseContentRespVo<String>> aa(AAa aaa){
		BaseContentRespVo<String> result = new BaseContentRespVo<>();
		try {
			
		}catch (Exception e) {
			
		}
		return new ResponseEntity<BaseContentRespVo<String>>(result, HttpStatus.OK);
	}
	
	@PostMapping(value="/dev/transfer", produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
	public ResponseEntity<BaseContentRespVo<String>> transfer(){
		BaseContentRespVo<String> result = new BaseContentRespVo<>();
		try {
			String code = (String) getSession().getAttribute(ContractConstant.CODE);
			TsRunTime crt =  (TsRunTime) getSession().getAttribute(ContractConstant.CRT);
			if(StringUtils.isBlank(code)) {
				throw new Exception("未发现代码或已过期，请重新运行");
			}
			SttkScriptContractTradeBody tsctb = new SttkScriptContractTradeBody();
			String owerAddress = crt.getTsContent().getOwnerAddress();
			if(StringUtils.isBlank(owerAddress)) {
				throw new Exception("合约未运行过，请确保运行无误的合约才提交上链。");
			}
			
			tsctb.setAddress(owerAddress);
			tsctb.setContent(code);
			tsctb.setGas(new BigDecimal(0.006));
			tsctb.setPublicKey("");
			tsctb.setSign("");
			
			
			
			new ContractApiHelper().send(tsctb);
			result.setCode(0);
			result.setMsg("SUCCESS");
			result.setContent("提交至测试链,请等待打包。");
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setCode(-1);
			result.setMsg(e.getMessage());
		}
		return new ResponseEntity<BaseContentRespVo<String>>(result, HttpStatus.OK);
	}
	
}
