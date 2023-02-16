package com.hitebaas.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hitebaas.controller.handler.BaseController;
import com.hitebaas.entity.Gas;
import com.hitebaas.entity.ParamReqInfo;
import com.hitebaas.helper.IContractManager;
import com.hitebaas.tvm.inns.KeyWord;
import com.hitebaas.tvm.inns.frame.collection.FuncNode;
import com.hitebaas.tvm.inns.frame.executor.FuncExecutor;
import com.hitebaas.tvm.inns.frame.o.SttStruct;
import com.hitebaas.tvm.inns.frame.vm.TsRunTime;
import com.hitebaas.util.ObjectUtil;
import com.hitebaas.vo.base.BaseContentRespVo;
import com.hitebaas.vo.run.QueryRunReqVo;

import net.sf.json.JSONObject;


@RestController
public class ProdDataController extends BaseController{
	private static final Logger logger = Logger.getLogger(ProdDataController.class);
	
	@Value("${contract.query.gas}")
	private BigDecimal gas;
	
	@Autowired
	private IContractManager contractManager;
	
	@PostMapping(value="/ts/query", produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
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
			checkQueryFunction(crt, funcName);
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
	
	private void checkQueryFunction(TsRunTime crt , String funcName) throws Exception {
		
		Map<String, FuncNode> fnMaps = crt.getRootNode().getContractNode().getFuncIns();
		FuncNode fn = null;
		for(Entry<String, FuncNode> entry : fnMaps.entrySet()) {
			if(entry.getKey().equals(funcName)) {
				fn = entry.getValue();
				break;
			}
		}
		if(fn == null) {
			throw new Exception("合约无此方法。");
		}
		if(!KeyWord.FUNC_QUERY.equals(fn.getFuncType())) {
			throw new Exception("只有@Query标记的方法才能直接接口访问。");
		}
		
		
	}
}
