package com.hitebaas.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;

import com.hitebaas.controller.base.BaseController;
import com.hitebaas.entity.ContractVo;
import com.hitebaas.entity.FuncParseInfoEntity;
import com.hitebaas.entity.Gas;
import com.hitebaas.entity.ParamReqInfo;
import com.hitebaas.helper.IContractManager;
import com.hitebaas.tvm.inns.frame.executor.FuncExecutor;
import com.hitebaas.tvm.inns.frame.o.SttStruct;
import com.hitebaas.tvm.inns.frame.vm.TsContent;
import com.hitebaas.tvm.inns.frame.vm.TsRunTime;
import com.hitebaas.util.ContractConstant;
import com.hitebaas.util.ObjectUtil;
import com.hitebaas.utils.KeysUtils;
import com.hitebaas.utils.RedisUtils;
import com.hitebaas.vo.base.BaseContentRespVo;
import com.hitebaas.vo.redis.CompanyInfoRedisVo;
import com.hitebaas.vo.run.HandleReqVo;
import com.hitebaas.vo.run.QueryRunReqVo;
import com.hitebaas.vo.run.RunReqVo;




@Component
@RestController		
public class ProdDataInterfaceController extends BaseController{
	private static final Logger logger = Logger.getLogger(ProdDataInterfaceController.class);
	
	@Autowired
	private RedisUtils redisUtils;
	
	@Value("${contract.query.gas}")
	private BigDecimal gas;
	
	@Autowired
	private IContractManager contractManager;
	
	@Autowired
	DataController dataController;
	
	
	

	@PostMapping(value="/v1/query", produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
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
	
	
	


	public ResponseEntity<BaseContentRespVo<List<FuncParseInfoEntity>>> createContract(@RequestBody HandleReqVo handleReqVo){
		
		
		return null;
		
	}
	
	
	
	
	@PostMapping(value="/v1/query/contract", produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
	public ResponseEntity<BaseContentRespVo<JSONObject>> queryContract2(@RequestBody RunReqVo runReqVo){
		
		
		
		
		
		BaseContentRespVo<JSONObject> result = new BaseContentRespVo<JSONObject>();
		try {
			String contractName = runReqVo.getContractName();
			String ownerAddress = runReqVo.getOwnerAddress();
			BigDecimal gas0 = runReqVo.getGas();
			
			if(StringUtils.isBlank(contractName)) {
				throw new Exception("Contract name cannot be empty.");
			}
			if(StringUtils.isBlank(ownerAddress)) {
				throw new Exception("CONTENT_OWNER_ADDRESS cannot be empty.");
			}
			if(TsContent.GASMIN.compareTo(gas0) > 0) {
				throw new Exception("The gas must > " + new DecimalFormat("0.000000").format(TsContent.GASMIN));
			}



			
			ContractVo contractVo = dataController.queryContractObj(ownerAddress,contractName);
			
			if(contractVo==null){
				throw new Exception("No Relevant contract found.");
			}
			
			CompanyInfoRedisVo cirv = new CompanyInfoRedisVo();
			cirv.setCode(contractVo.getCode());
			cirv.setContractName(contractVo.getContractName());
			
			result.setCode(0);
			result.setMsg("SUCCESS");
			result.setContent(ObjectUtil.converToJSONObject(cirv));
			
			System.out.println(cirv);
			
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			result.setCode(-1);
			result.setMsg(e.getMessage());
			result.setContent(new JSONObject());
			return new ResponseEntity<BaseContentRespVo<JSONObject>>(result, HttpStatus.OK);
		}
		return new ResponseEntity<BaseContentRespVo<JSONObject>>(result, HttpStatus.OK);
	}
	
	
	
	
	


	@CrossOrigin
	@PostMapping(value="/v1/run/contract", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<BaseContentRespVo<String>> testRun(@RequestBody RunReqVo runReqVo){
		
		BaseContentRespVo<String> result = new BaseContentRespVo<String>();
		try {
			
			String contractName = runReqVo.getContractName();
			String integral = runReqVo.getIntegral(); 
			String accountBalance = runReqVo.getAccountBalance();
			String funcName = runReqVo.getFuncName();
			String callAddress = runReqVo.getCallAddress();
			String ownerAddress = runReqVo.getOwnerAddress();
			String code = runReqVo.getCode();
			BigDecimal gas0 = runReqVo.getGas();
			Gas gas = new Gas(gas0);
			List<ParamReqInfo> pris = runReqVo.getPris(); 
			
			if(StringUtils.isBlank(funcName)) {
				throw new Exception("Function name cannot be empty.");
			}
			if(TsContent.GASMIN.compareTo(gas0) > 0) {
				throw new Exception("The gas must > " + new DecimalFormat("0.000000").format(TsContent.GASMIN));
			}
			if(StringUtils.isBlank(callAddress)) {
				throw new Exception("CONTENT_CALL_ADDRESS cannot be empty.");
			}
			if(StringUtils.isBlank(ownerAddress)) {
				throw new Exception("CONTENT_OWNER_ADDRESS cannot be empty.");
			}
			for(ParamReqInfo pri : pris) {
				if(StringUtils.isBlank(pri.getValue())) {
					throw new Exception("Parameter cannot be empty.");
				}
			}
			
			
			ContractVo contractVo = dataController.queryContractObj(ownerAddress,contractName);
			Boolean isChange = StrUtil.equals(SecureUtil.md5(contractVo.getCode()), SecureUtil.md5(code));
			if(!isChange){
				throw new Exception("Please check whether the contract has been modified.");
			}
			
			String newCode = preDeal(code);
			TsRunTime crt = dealCheck(newCode, gas);
			List<FuncParseInfoEntity> fpses = genRunTime(crt);	
			getSession().setAttribute(ContractConstant.CRT, crt);
			getSession().setAttribute(ContractConstant.CODE, code);
			token();
			String token = (String) getSession().getAttribute(ContractConstant.TOKEN);
			

			
			if(crt == null) {
				throw new Exception("Session expired, Reexecution the button ‘Syntax Check’.");
			}
			crt.getTsContent().setCallAddress("'" + runReqVo.getCallAddress() + "'");
			crt.getTsContent().setOwnerAddress("'" + runReqVo.getOwnerAddress() + "'");
			
			List<SttStruct> params = genParams(crt, pris);
			SttStruct result0 = new FuncExecutor().executor(crt, funcName, params, gas);
			result.setCode(0);
			String timeInfo = "[ " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " ][" + new DecimalFormat("0.00000").format(gas.getGas().subtract(TsContent.GASCOST)) + "]";
			
			long estimatedMemorySize = ObjectSizeCalculator.getObjectSize(crt);
			crt.setEstimatedMemorySize(estimatedMemorySize);

			
			result.setMsg("SUCCESS , Memory Size ≈ " + crt.getEstimatedMemorySize() + " byte");
			
			result.setContent(timeInfo + "SUCCESS " + result0.toString());


		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setCode(-1);
			String timeInfo = "[ " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " ]  ";
			result.setMsg(timeInfo + "ERROR " + e.getMessage());
			return new ResponseEntity<BaseContentRespVo<String>>(result, HttpStatus.OK);
		}
		return new ResponseEntity<BaseContentRespVo<String>>(result, HttpStatus.OK);
	}
	
	
	@PostMapping(value="/dev/token", produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
	public ResponseEntity<BaseContentRespVo<String>> token(){
		BaseContentRespVo<String> result = new BaseContentRespVo<String>();
		try {
			String token = (String) getSession().getAttribute(ContractConstant.TOKEN);
			if(StringUtils.isBlank(token)) {
				token = KeysUtils.get32String();
				getSession().setAttribute(ContractConstant.TOKEN, token);
			}
			result.setMsg("SUCCESS");
			result.setContent(token);
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return new ResponseEntity<BaseContentRespVo<String>>(result, HttpStatus.OK);
	}
	
}
