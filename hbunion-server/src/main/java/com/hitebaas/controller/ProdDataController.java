package com.hitebaas.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hitebaas.controller.base.BaseController;
import com.hitebaas.entity.FuncParseInfoEntity;
import com.hitebaas.entity.Gas;
import com.hitebaas.entity.ParamReqInfo;
import com.hitebaas.helper.IContractManager;
import com.hitebaas.tvm.inns.frame.debug.DebugContentManager;
import com.hitebaas.tvm.inns.frame.debug.DebugRunContent;
import com.hitebaas.tvm.inns.frame.executor.FuncExecutor;
import com.hitebaas.tvm.inns.frame.o.SttStruct;
import com.hitebaas.tvm.inns.frame.vm.TsRunTime;
import com.hitebaas.util.ContractConstant;
import com.hitebaas.util.ObjectUtil;
import com.hitebaas.utils.RedisUtils;
import com.hitebaas.vo.base.BaseContentRespVo;
import com.hitebaas.vo.run.QueryRunReqVo;





public class ProdDataController extends BaseController{
	private static final Logger logger = Logger.getLogger(ProdDataController.class);
	@Autowired
	RedisUtils redisUtils;
	
	@Value("${contract.query.gas}")
	private BigDecimal gas;
	
	@Autowired
	private IContractManager contractManager;
	
	
	


	public ResponseEntity<BaseContentRespVo<JSONObject>> query(@RequestBody QueryRunReqVo runReqVo){
		


		
		BaseContentRespVo<JSONObject> result = new BaseContentRespVo<>();
		try {
			String contractName = runReqVo.getContractName();
			String funcName = runReqVo.getFuncName();
			List<ParamReqInfo> funcParamInfos = runReqVo.getPris();
			
			BigDecimal gas0 = gas;
			
			if(StringUtils.isBlank(contractName)) {
				throw new Exception("Contract name cannot be empty.");
			}
			if(StringUtils.isBlank(funcName)) {
				throw new Exception("Function name cannot be empty.");
			}
			if(!funcName.startsWith("query")) {
				throw new Exception("Function name must start with 'query'.");
			}
			for(ParamReqInfo pri : funcParamInfos) {
				if(StringUtils.isBlank(pri.getValue())) {
					throw new Exception("Parameter cannot be empty.");
				}
			}
			
			if(!contractManager.exist(contractName)) {
				throw new Exception("Contract not exist, or unrecorded .");
			}
			
			
			TsRunTime crt = contractManager.getTsRunTime(contractName);
			List<ParamReqInfo> pris = new ArrayList<ParamReqInfo>();
			for(ParamReqInfo funcParamInfo : funcParamInfos) {
				ParamReqInfo pri = new ParamReqInfo();
				pri.setName(funcParamInfo.getName());
				pri.setType(funcParamInfo.getType());
				pri.setValue(funcParamInfo.getValue());
				pris.add(pri);
			}
			List<SttStruct> params = genParams(crt, pris);
			Gas gas = new Gas(gas0);
			SttStruct result0 = new FuncExecutor().executor(crt, funcName, params, gas);
			result.setCode(0);
			result.setMsg("SUCCESS");
			result.setContent(ObjectUtil.converToJSONObject(result0));
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setCode(-1);
			result.setMsg(e.getMessage());
			result.setContent(new JSONObject());
			return new ResponseEntity<BaseContentRespVo<JSONObject>>(result, HttpStatus.OK);
		}
		return new ResponseEntity<BaseContentRespVo<JSONObject>>(result, HttpStatus.OK);
	}
	
	
	
	
	
}
