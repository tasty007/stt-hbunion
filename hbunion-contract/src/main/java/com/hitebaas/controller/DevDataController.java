package com.hitebaas.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import cn.hutool.core.bean.BeanUtil;

import com.hitebaas.constant.Constant;
import com.hitebaas.controller.handler.BaseController;
import com.hitebaas.entity.ContractVo;
import com.hitebaas.entity.FuncParseInfoEntity;
import com.hitebaas.entity.Gas;
import com.hitebaas.entity.ParamReqInfo;
import com.hitebaas.entity.UserVo;
import com.hitebaas.service.ContractService;
import com.hitebaas.tvm.inns.frame.debug.DebugContentManager;
import com.hitebaas.tvm.inns.frame.debug.DebugRunContent;
import com.hitebaas.tvm.inns.frame.executor.FuncExecutor;
import com.hitebaas.tvm.inns.frame.o.SttStruct;
import com.hitebaas.tvm.inns.frame.vm.TsContent;
import com.hitebaas.tvm.inns.frame.vm.TsRunTime;
import com.hitebaas.util.ContractConstant;
import com.hitebaas.util.ObjectUtil;
import com.hitebaas.utils.ContractUtils;
import com.hitebaas.utils.KeysUtils;
import com.hitebaas.vo.base.BaseContentRespVo;
import com.hitebaas.vo.check.CheckReqVo;
import com.hitebaas.vo.redis.CompanyInfoRedisVo;
import com.hitebaas.vo.run.ContractReqVo;
import com.hitebaas.vo.run.RunReqVo;

@RestController
@CrossOrigin
public class DevDataController extends BaseController{
	private static final Logger logger = Logger.getLogger(DevDataController.class);
	
	@Autowired
	DataController dataController;
	@Autowired
	ContractService homeService;
	
	
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
	
	


	@CrossOrigin	
	@PostMapping(value="/dev/check", produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
	public ResponseEntity<BaseContentRespVo<List<FuncParseInfoEntity>>> check(@RequestBody CheckReqVo checkReqVo){ 
		
		BaseContentRespVo<List<FuncParseInfoEntity>> result = new BaseContentRespVo<List<FuncParseInfoEntity>>();
		String code = checkReqVo.getCode();
		try {
			if(StringUtils.isBlank(code)) {
				throw new Exception("The code is empty !");
			}
			
			BigDecimal gas0 = checkReqVo.getGas();
			Gas gas = new Gas(gas0);
			String newCode = preDeal(checkReqVo.getCode());
			TsRunTime crt = dealCheck(newCode, gas);
			List<FuncParseInfoEntity> fpses = genRunTime(crt);
			

			
			getSession().setAttribute(ContractConstant.CRT, crt);
			getSession().setAttribute(ContractConstant.CODE, checkReqVo.getCode());
			token();
			String token = (String) getSession().getAttribute(ContractConstant.TOKEN);
			
			DebugRunContent drc = DebugContentManager.getDebugRunContent(token);
			drc.setRunMode(-1);
			
			long estimatedMemorySize = ObjectSizeCalculator.getObjectSize(crt);
			crt.setEstimatedMemorySize(estimatedMemorySize);
			
			Pattern pattern = Pattern.compile(Constant.CONTRACT_NAME_REGEX); 
	        Matcher matcher = pattern.matcher(code);
	        if(matcher.find()){	
	        	String contractNameInCode = matcher.group(1);
	        	result.setContractName(contractNameInCode);
		    }else {
		    	throw new Exception("Contract keyword not found in code.");
			}			
			
			result.setCode(0);
			result.setMsg("SUCCESS , Memory Size ≈ " + crt.getEstimatedMemorySize() + " byte");
			result.setContent(fpses);
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setCode(-1);
			result.setMsg(e.getMessage());
			return new ResponseEntity<BaseContentRespVo<List<FuncParseInfoEntity>>>(result, HttpStatus.OK);
		}
		return new ResponseEntity<BaseContentRespVo<List<FuncParseInfoEntity>>>(result, HttpStatus.OK);
	}
	


	@CrossOrigin
	@PostMapping(value="/dev/test", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<BaseContentRespVo<String>> testRun(@RequestBody RunReqVo runReqVo){
		
		BaseContentRespVo<String> result = new BaseContentRespVo<String>();
		try {
			String contractName = runReqVo.getContractName();
			String integral = runReqVo.getIntegral(); 
			String accountBalance = runReqVo.getAccountBalance();
			String funcName = runReqVo.getFuncName();
			String callAddress = runReqVo.getCallAddress();
			String ownerAddress = runReqVo.getOwnerAddress();
			BigDecimal gas0 = runReqVo.getGas();
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
			
			
			
			
			
			

			
			TsRunTime crt =  (TsRunTime) getSession().getAttribute(ContractConstant.CRT);
			if(crt == null) {
				throw new Exception("Session expired, Reexecution the button ‘Syntax Check’.");
			}
			crt.getTsContent().setCallAddress("'" + runReqVo.getCallAddress() + "'");
			crt.getTsContent().setOwnerAddress("'" + runReqVo.getOwnerAddress() + "'");
			Gas gas = new Gas(gas0);
			
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
	
	
	
	@CrossOrigin	
	@PostMapping(value="/dev/createContract", produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
	public ResponseEntity<BaseContentRespVo<String>> createContract(@RequestBody ContractReqVo contractReqVo){
		
		BaseContentRespVo<String> result = new BaseContentRespVo<String>();
		try {
			String code = contractReqVo.getCode();
			String contractName = contractReqVo.getContractName();
			String ownerAddress = contractReqVo.getOwnerAddress();
			BigDecimal gas0 = contractReqVo.getGas();
			
			if(StringUtils.isBlank(contractReqVo.getCode())) {
				throw new Exception("The code is empty !");
			}



			if(StringUtils.isBlank(contractName)) {
				throw new Exception("Contract name cannot be empty.");
			}
			if(StringUtils.isBlank(ownerAddress)) {
				throw new Exception("CONTENT_OWNER_ADDRESS cannot be empty.");
			}
			if(TsContent.GASMIN.compareTo(gas0) > 0) {
				throw new Exception("The gas must > " + new DecimalFormat("0.000000").format(TsContent.GASMIN));
			}
			UserVo user = new UserVo();
			user.setUserId(ownerAddress);
			
			ContractVo contract = new ContractVo();
			contract.setCode(code);
			
			Pattern pattern = Pattern.compile(Constant.CONTRACT_NAME_REGEX); 
	        Matcher matcher = pattern.matcher(code);
	        if(matcher.find()){	
	        	String contractNameInCode = matcher.group(1);
	        	
	        	if(contractName.equals(contractNameInCode)){
	        		contract.setContractName(contractNameInCode);
	        	}else{
	        		throw new Exception("Check whether the contract names are the same.");
	        	}
		    }else {
		    	throw new Exception("Contract keyword not found in code.");
			}
	        
	        synchronized (this) {
	        	ResponseEntity<BaseContentRespVo<Boolean>> rbb = hasUser(contractReqVo);
	        	BaseContentRespVo<Boolean> contentRespVo = rbb.getBody();
	        	int codeInt = contentRespVo.getCode();
	        	if(codeInt!=0){
	        		result.setCode(contentRespVo.getCode());
	        		result.setMsg(contentRespVo.getMsg());
	        		return new ResponseEntity<BaseContentRespVo<String>>(result, HttpStatus.OK);
	        	}
	        	Boolean flag1 =false;
	        	Boolean flag2 =false;
	        	if (contentRespVo.getContent()) { 
	        		
	        		Integer contractVoCount = findContractByName(contractReqVo);
	        		if (contractVoCount > 0) {
	        			
	        			result.setCode(-1);
	        			result.setMsg("The contract name already exists .");
	        			return new ResponseEntity<BaseContentRespVo<String>>(result, HttpStatus.OK);
	        		} else {
	        			
	        			flag1 = dataController.saveContract(contractReqVo.getOwnerAddress(),contractReqVo.getContractName(),contractReqVo.getCode());
	        		}
	        	} else { 
	        		flag2 = dataController.createContract(user, contract);
	        	}
			}
	        
			
	        String timeInfo = ContractUtils.formatNowToYMDHMS();
			Map<String, Object> userMap = BeanUtil.beanToMap(contractReqVo);

			String hashCode = dataController.coChainToHitebaas(userMap);

			if (hashCode ==null) {
				result.setCode(-1);
				result.setMsg(timeInfo+" Contract [ "+contractName+" ] cochain transaction failed.");
				throw new Exception("上链交易失败.");
			}else{
				result.setCode(0);
				result.setMsg(timeInfo+" SUCCESS , This contract has been linked successfully.");
				result.setContent(timeInfo + "SUCCESS. ");
			}
			return new ResponseEntity<BaseContentRespVo<String>>(result, HttpStatus.OK);
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setCode(-1);
			result.setMsg(e.getMessage());
			return new ResponseEntity<BaseContentRespVo<String>>(result, HttpStatus.OK);
		}
	}
	
	
	
	@PostMapping(value="/dev/queryContract", produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
	public ResponseEntity<BaseContentRespVo<JSONObject>> queryContract(@RequestBody ContractReqVo contractReqVo){
		
		
		
		
		
		BaseContentRespVo<JSONObject> result = new BaseContentRespVo<JSONObject>();
		try {
			String contractName = contractReqVo.getContractName();
			String ownerAddress = contractReqVo.getOwnerAddress();
			BigDecimal gas0 = contractReqVo.getGas();
			
			if(StringUtils.isBlank(contractName)) {
				throw new Exception("Contract name cannot be empty.");
			}
			if(StringUtils.isBlank(ownerAddress)) {
				throw new Exception("CONTENT_OWNER_ADDRESS cannot be empty.");
			}
			if(TsContent.GASMIN.compareTo(gas0) > 0) {
				throw new Exception("The gas must > " + new DecimalFormat("0.000000").format(TsContent.GASMIN));
			}
			String companyID = ownerAddress + ":" + contractName;
			
			CompanyInfoRedisVo companyInfoRedisVo = dataController.queryContract(companyID);
			
			result.setCode(0);
			result.setMsg("SUCCESS");
			result.setContent(ObjectUtil.converToJSONObject(companyInfoRedisVo));
			
			System.out.println(companyInfoRedisVo);
			
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			result.setCode(-1);
			result.setMsg(e.getMessage());
			result.setContent(new JSONObject());
			return new ResponseEntity<BaseContentRespVo<JSONObject>>(result, HttpStatus.OK);
		}
		return new ResponseEntity<BaseContentRespVo<JSONObject>>(result, HttpStatus.OK);
		
	}
	
	
	
	@PostMapping(value="/dev/queryContract2", produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
	public ResponseEntity<BaseContentRespVo<JSONObject>> queryContract2(@RequestBody ContractReqVo contractReqVo){
		
		
		
		
		
		BaseContentRespVo<JSONObject> result = new BaseContentRespVo<JSONObject>();
		try {
			String contractName = contractReqVo.getContractName();
			String ownerAddress = contractReqVo.getOwnerAddress();
			BigDecimal gas0 = contractReqVo.getGas();
			
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
	
	
	
	
	public Integer findContractByName(ContractReqVo contractReqVo){
		

		Integer contractVoCount = null;
		try {
			String contractName = contractReqVo.getContractName();

			BigDecimal gas0 = contractReqVo.getGas();
			
			if(StringUtils.isBlank(contractName)) {
				throw new Exception("Contract name cannot be empty.");
			}
			if(TsContent.GASMIN.compareTo(gas0) > 0) {
				throw new Exception("The gas must > " + new DecimalFormat("0.000000").format(TsContent.GASMIN));
			}
			
			
			contractVoCount = dataController.findContractCountByName( contractName,Constant.CONTRACT_STATUS_0 );
			
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return contractVoCount;
	}
	
	
	
	
	
	
	
	@PostMapping(value = "/dev/hasUser", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<BaseContentRespVo<Boolean>> hasUser(@RequestBody ContractReqVo contractReqVo) {
		String address = contractReqVo.getOwnerAddress();
		BaseContentRespVo<Boolean> result = new BaseContentRespVo<Boolean>();
		Boolean mark = false;
		try {
			if (StringUtils.isBlank(address)) {
				throw new Exception("CONTENT_OWNER_ADDRESS cannot be empty.");
			}
			mark =  dataController.hasUser(address, Constant.USER_STATUS_0);
			result.setContent(mark);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setCode(-1);
			result.setMsg(e.getMessage());
			return new ResponseEntity<BaseContentRespVo<Boolean>>(result,HttpStatus.OK);
		}
		result.setCode(0);
		result.setMsg("SUCCESS");
		return new ResponseEntity<BaseContentRespVo<Boolean>>(result,HttpStatus.OK);
	}
		
		
	
	@PostMapping(value = "/dev/isCreateContractUser", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<BaseContentRespVo<Boolean>> isCreateContractUser(@RequestBody Map<String, Object> reqMap) {
		String contentOwnerAddress = reqMap.get("contentOwnerAddress")+"";
		String contractName = reqMap.get("contractName")+"";
		BaseContentRespVo<Boolean> result = new BaseContentRespVo<Boolean>();
		Boolean mark = false;
		try {
			if (StringUtils.isBlank(contentOwnerAddress)) {
				throw new Exception("CONTENT_OWNER_ADDRESS cannot be empty.");
			}
			Integer  i =  dataController.isCreateContractUser(contentOwnerAddress,contractName, Constant.USER_STATUS_0);
			if (i>0) {
				mark=true;
			} 
			result.setContent(mark);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setCode(-1);
			result.setMsg(e.getMessage());
			return new ResponseEntity<BaseContentRespVo<Boolean>>(result,HttpStatus.OK);
		}
		result.setCode(0);
		result.setMsg("SUCCESS");
		return new ResponseEntity<BaseContentRespVo<Boolean>>(result,HttpStatus.OK);
	}

	
	
	@PostMapping(value = "/dev/getPointByUserId", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<BaseContentRespVo<String>> getPointByUserId(@RequestBody Map<String, Object> reqMap) {
		String contentOwnerAddress = reqMap.get("userAddress")+"";

		BaseContentRespVo<String> result = new BaseContentRespVo<String>();
		try {
			if (StringUtils.isBlank(contentOwnerAddress)) {
				throw new Exception("CONTENT_OWNER_ADDRESS cannot be empty.");
			}
			
			String  point =  dataController.getPointByUserId(contentOwnerAddress);
			result.setContent(point);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setCode(-1);
			result.setMsg(e.getMessage());
			return new ResponseEntity<BaseContentRespVo<String>>(result,HttpStatus.OK);
		}
		result.setCode(0);
		result.setMsg("SUCCESS");
		return new ResponseEntity<BaseContentRespVo<String>>(result,HttpStatus.OK);
	}
	
	
	
	@PostMapping(value = "/dev/updatePoint", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<BaseContentRespVo<Boolean>> updatePointByUserId(@RequestBody Map<String, Object> reqMap) {
		String userAddress = reqMap.get("userAddress")+"";
		String rewardPoint = reqMap.get("rewardPoint")+"";

		BaseContentRespVo<Boolean> result = new BaseContentRespVo<Boolean>();
		try {
			if (StringUtils.isBlank(userAddress)) {
				throw new Exception("UserAddress cannot be empty.");
			}
			if (StringUtils.isBlank(rewardPoint)) {
				throw new Exception("RewardPoint cannot be empty.");
			}
			
			Boolean  flag =  dataController.updateWarehouseByUserId(userAddress,Constant.POINT,rewardPoint);
			result.setContent(flag);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setCode(-1);
			result.setMsg(e.getMessage());
			return new ResponseEntity<BaseContentRespVo<Boolean>>(result,HttpStatus.OK);
		}
		result.setCode(0);
		result.setMsg("SUCCESS");
		return new ResponseEntity<BaseContentRespVo<Boolean>>(result,HttpStatus.OK);
	}
	
	
	@PostMapping(value = "/dev/getDefineDataByUserId", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<BaseContentRespVo<String>> getDefineDataByUserId(@RequestBody Map<String, Object> reqMap) {
		String contentOwnerAddress = reqMap.get("userAddress")+"";
		String field = reqMap.get("field")+"";
		BaseContentRespVo<String> result = new BaseContentRespVo<String>();
		try {
			if (StringUtils.isBlank(contentOwnerAddress)) {
				throw new Exception("CONTENT_OWNER_ADDRESS cannot be empty.");
			}
			if (StringUtils.isBlank(field)) {
				throw new Exception("field cannot be empty.");
			}
			
			String  value =  dataController.getDefineDataByUserId(contentOwnerAddress,field);
			result.setContent(value);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setCode(-1);
			result.setMsg(e.getMessage());
			return new ResponseEntity<BaseContentRespVo<String>>(result,HttpStatus.OK);
		}
		result.setCode(0);
		result.setMsg("SUCCESS");
		return new ResponseEntity<BaseContentRespVo<String>>(result,HttpStatus.OK);
	}
	
	
	@PostMapping(value = "/dev/updateDefineData", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<BaseContentRespVo<Boolean>> updateDefineData(@RequestBody Map<String, Object> reqMap) {
		String userAddress = reqMap.get("userAddress")+"";
		String field = reqMap.get("field")+"";
		String value = reqMap.get("value")+"";
		BaseContentRespVo<Boolean> result = new BaseContentRespVo<Boolean>();
		try {
			if (StringUtils.isBlank(userAddress)) {
				throw new Exception("UserAddress cannot be empty.");
			}
			if (StringUtils.isBlank(field)) {
				throw new Exception("Field cannot be empty.");
			}
			if (StringUtils.isBlank(value)) {
				throw new Exception("Value cannot be empty.");
			}
			Boolean  flag =  dataController.updateWarehouseByUserId(userAddress,field,value);
			result.setContent(flag);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setCode(-1);
			result.setMsg(e.getMessage());
			return new ResponseEntity<BaseContentRespVo<Boolean>>(result,HttpStatus.OK);
		}
		result.setCode(0);
		result.setMsg("SUCCESS");
		return new ResponseEntity<BaseContentRespVo<Boolean>>(result,HttpStatus.OK);
	}
	
	
	
	
	@CrossOrigin
	@PostMapping(value = "/dev/joinContract", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<BaseContentRespVo<Boolean>> joinContract(@RequestBody ContractReqVo contractReqVo) {

		BaseContentRespVo<Boolean> result = new BaseContentRespVo<Boolean>();
		try {
			String contractName = contractReqVo.getContractName();
			String ownerAddress = contractReqVo.getOwnerAddress();
			String callAddress = contractReqVo.getCallAddress();
			BigDecimal gas0 = contractReqVo.getGas();
			
			
			
			
			if (StringUtils.isBlank(contractName)) {
				throw new Exception("Contract name cannot be empty.");
			}
			if (StringUtils.isBlank(ownerAddress)) {
				throw new Exception("CONTENT_OWNER_ADDRESS cannot be empty.");
			}
			if (StringUtils.isBlank(callAddress)) {
				throw new Exception("CONTENT_CALL_ADDRESS cannot be empty.");
			}
			if (TsContent.GASMIN.compareTo(gas0) > 0) {
				throw new Exception("The gas must > "+ new DecimalFormat("0.000000").format(TsContent.GASMIN));
			}
			UserVo user = new UserVo();
			user.setUserId(ownerAddress);
			Boolean mark = false;
			
			String cId = dataController.queryContractIdByCreateIdAndName(ownerAddress, contractName, Constant.USER_STATUS_0);
			if (cId == null) {
				
				result.setCode(-1);
				result.setMsg("Only the contract creator can do this. Please enter the correct CONTENT_OWNER_ADDRESS.");
				return new ResponseEntity<BaseContentRespVo<Boolean>>(result,HttpStatus.OK);
			}
			
			mark = dataController.hasUser(callAddress, Constant.USER_STATUS_0);
			
			synchronized (this) {
				if (mark) {
					
					Boolean flag = dataController.isJoinContract(callAddress, cId);
					if (flag) {
						
						result.setCode(-1);
						result.setMsg("This Id has joined the contract.");
						return new ResponseEntity<BaseContentRespVo<Boolean>>(result, HttpStatus.OK);
					}
					
					Boolean addFlag = dataController.addUserContractRS(callAddress, cId);
					result.setContent(addFlag);
				} else {
					
					Boolean addFlag2 = dataController.addUserRS(callAddress, cId);
					result.setContent(addFlag2);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setCode(-1);
			result.setMsg(e.getMessage());
			return new ResponseEntity<BaseContentRespVo<Boolean>>(result,HttpStatus.OK);
		}
		result.setCode(0);
		result.setMsg("SUCCESS");
		return new ResponseEntity<BaseContentRespVo<Boolean>>(result,HttpStatus.OK);

	}
	
	
	
	
	
	
	
}
