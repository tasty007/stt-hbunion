package com.hitebaas.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hitebaas.controller.handler.BaseController;
import com.hitebaas.entity.Gas;
import com.hitebaas.entity.NameValue;
import com.hitebaas.entity.ParamReqInfo;
import com.hitebaas.entity.TypeResp;
import com.hitebaas.tvm.inns.frame.debug.DebugContentManager;
import com.hitebaas.tvm.inns.frame.debug.DebugRunContent;
import com.hitebaas.tvm.inns.frame.debug.DebugRunThread;
import com.hitebaas.tvm.inns.frame.o.SttStruct;
import com.hitebaas.tvm.inns.frame.vm.TsContent;
import com.hitebaas.tvm.inns.frame.vm.TsRunTime;
import com.hitebaas.tvm.inns.frame.vm.TsScope;
import com.hitebaas.tvm.inns.frame.vm.TsStackInfo;
import com.hitebaas.util.ContractConstant;
import com.hitebaas.util.ObjectUtil;
import com.hitebaas.vo.base.BaseContentRespVo;
import com.hitebaas.vo.debug.AddBreakpointReqVo;
import com.hitebaas.vo.debug.DebugReqVo;
import com.hitebaas.vo.debug.UpdateBreakpointReqVo;

import net.sf.json.JSONObject;
import net.sf.json.regexp.RegexpMatcher;
import net.sf.json.regexp.RegexpUtils;


@RestController
public class DebugController extends BaseController{
	private static final Logger logger = Logger.getLogger(DebugController.class);
	
	
	@PostMapping(value="/dev/addbp", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<BaseContentRespVo<List<Integer> >> addbp(@RequestBody AddBreakpointReqVo addBreakpointReqVo){
		BaseContentRespVo<List<Integer> > result = new BaseContentRespVo<>();
		try {
			int line = addBreakpointReqVo.getLine();
			String op = addBreakpointReqVo.getOp();
			String token = (String) getSession().getAttribute(ContractConstant.TOKEN);
			System.out.println(token);
			DebugRunContent drc = DebugContentManager.getDebugRunContent(token);
			if("+".equals(op)) {
				if(!drc.getBreakpoints().contains(line)) {
					drc.getBreakpoints().add(line);
				}
			}else if("-".equals(op)) {
				List<Integer> bps = drc.getBreakpoints();
				for(int i=0;i<bps.size();i++) {
					if(line == bps.get(i)) {
						bps.remove(i);
						break;
					}
				}
			}
			result.setCode(0);
			result.setMsg(TypeResp.BREAKPOINTS.ordinal() + "");
			result.setContent(drc.getBreakpoints());
		}catch (Exception e) {
			result.setCode(-1);
			result.setMsg(e.getMessage());
			logger.error(e.getMessage(), e);
		}
		return new ResponseEntity<BaseContentRespVo<List<Integer> >>(result, HttpStatus.OK);
	}
	
	@PostMapping(value="/dev/updatebps", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<BaseContentRespVo<List<Integer>>> updatebps(@RequestBody UpdateBreakpointReqVo updateBreakpointReqVo){
		BaseContentRespVo<List<Integer>> bcr = new BaseContentRespVo<List<Integer>>();
		try {
			String token = (String) getSession().getAttribute(ContractConstant.TOKEN);
			DebugRunContent drc = DebugContentManager.getDebugRunContent(token);
			drc.getBreakpoints().clear();
			drc.getBreakpoints().addAll(updateBreakpointReqVo.getLineNos());
			bcr.setCode(0);
			bcr.setMsg(TypeResp.BREAKPOINTS.ordinal() + "");
			bcr.setContent(drc.getBreakpoints());
		}catch (Exception e) {
			bcr.setCode(-1);
			bcr.setMsg(e.getMessage());
		}
		return new ResponseEntity<BaseContentRespVo<List<Integer>>>(bcr, HttpStatus.OK);
	}
	
	@PostMapping(value="/dev/cleanbps", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<BaseContentRespVo<String>> cleanbps(){
		BaseContentRespVo<String> bcr = new BaseContentRespVo<String>();
		try {
			String token = (String) getSession().getAttribute(ContractConstant.TOKEN);
			DebugRunContent drc = DebugContentManager.getDebugRunContent(token);
			drc.getBreakpoints().clear();
			bcr.setCode(0);
			bcr.setMsg("SUCCESS");
		}catch (Exception e) {
			bcr.setCode(-1);
			bcr.setMsg(e.getMessage());
		}
		return new ResponseEntity<BaseContentRespVo<String>>(bcr, HttpStatus.OK);
	}
	
	
	@PostMapping(value="/dev/debugger", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<BaseContentRespVo<List<NameValue>>> debugger(@RequestBody DebugReqVo debugReqVo){
		BaseContentRespVo<List<NameValue>> result = new BaseContentRespVo<List<NameValue>>();
		try {
			List<NameValue> nvs = new ArrayList<NameValue>();
			result.setContent(nvs);
			
			TsRunTime crt =  (TsRunTime) getSession().getAttribute(ContractConstant.CRT);
			if(crt == null) {
				throw new Exception("Session expired, Reexecution the button ‘Syntax Check’.");
			}
			
			Stack<TsScope> tsStacck = crt.getSnStack();
			for(int i=0;i<tsStacck.size();i++) {
				TsScope ts = tsStacck.get(i);
				
				Map<String, TsStackInfo> tsiMap = ts.getTsas();
				
				for(Entry<String, TsStackInfo> entry : tsiMap.entrySet()) {
					TsStackInfo thi = entry.getValue();
					String idName = thi.getIdName();
					String address = thi.getAddress();
					
					RegexpMatcher rm = RegexpUtils.getMatcher("^[a-z][a-zA-Z0-9_]*$");
					boolean b = rm.matches(idName);
					if(b) {
						System.out.println(idName);
						SttStruct value = crt.findBean(address);
						JSONObject jo = ObjectUtil.converToJSONObject(value);
						nvs.add(new NameValue(idName, value.getTypeName(), jo));
					}
				}
			}
			return new ResponseEntity<BaseContentRespVo<List<NameValue>>>(result, HttpStatus.OK);
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.getContent().clear();
		}
		return new ResponseEntity<BaseContentRespVo<List<NameValue>>>(result, HttpStatus.OK);
	}
	
	@PostMapping(value="/dev/debug", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<BaseContentRespVo<String>> testDebug(@RequestBody DebugReqVo runReqVo){
		BaseContentRespVo<String> result = new BaseContentRespVo<String>();
		try {
			String contractName = runReqVo.getContractName();
			String funcName = runReqVo.getFuncName();
			String callAddress = runReqVo.getCallAddress();
			String ownerAdress = runReqVo.getOwnerAddress();
			BigDecimal gas0 = runReqVo.getGas();
			List<ParamReqInfo> pris = runReqVo.getPris(); 
			
			int runMode = runReqVo.getRunMode();
			
			String token = (String) getSession().getAttribute(ContractConstant.TOKEN);
			System.out.println(token);
			DebugRunContent drc = DebugContentManager.getDebugRunContent(token);
			drc.setDebug(true);
			drc.setToken(token);
			drc.setRunMode(runMode);
			drc.setCurrentLine(-1);
			drc.setTime(new Date().getTime());
			
			if(StringUtils.isBlank(funcName)) {
				throw new Exception("Function name cannot be empty.");
			}
			if(TsContent.GASMIN.compareTo(gas0) > 0) {
				throw new Exception("The gas must > " + new DecimalFormat("0.000000").format(TsContent.GASMIN));
			}
			if(StringUtils.isBlank(callAddress)) {
				throw new Exception("CONTENT_CALL_ADDRESS cannot be empty.");
			}
			if(StringUtils.isBlank(ownerAdress)) {
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
			
			DebugRunThread drt = new DebugRunThread(drc, crt, funcName, params, gas);
			DebugRunThread old = DebugContentManager.getDebugRunThread(token);
			if(old != null) {
				old.interrupt();
			}
			DebugContentManager.putDebugRunThread(token, drt);
			drt.start();
			
			result.setCode(0);
			String timeInfo = "[ " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " ][" + new DecimalFormat("0.00000").format(gas.getGas().subtract(TsContent.GASMIN)) + "]";
			
			result.setMsg("SUCCESS");
			result.setContent(timeInfo + "SUCCESS TO DEBUG MODE.");
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setCode(-1);
			String timeInfo = "[ " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " ]  ";
			result.setMsg(timeInfo + "ERROR " + e.getMessage());
			return new ResponseEntity<BaseContentRespVo<String>>(result, HttpStatus.OK);
		}
		return new ResponseEntity<BaseContentRespVo<String>>(result, HttpStatus.OK);
	}
	
	
	
	@PostMapping(value="/dev/nextstep", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<BaseContentRespVo<String>> nextstep(@RequestBody DebugReqVo runReqVo){
		BaseContentRespVo<String> result = new BaseContentRespVo<String>();
		try {
			int runMode = runReqVo.getRunMode();
			String token = (String) getSession().getAttribute(ContractConstant.TOKEN);
			DebugRunContent drc = DebugContentManager.getDebugRunContent(token);
			drc.setRunMode(runMode);
			drc.setTime(new Date().getTime());
			DebugRunThread drt = DebugContentManager.getDebugRunThread(token);
			if(drt == null || drt.getState() == Thread.State.TERMINATED) {
				throw new Exception("无调试任务");
			}
			result.setCode(0);
			result.setMsg("SUCCESS");
		}catch (Exception e) {
			result.setCode(-1);
			result.setMsg(e.getMessage());
			logger.error(e.getLocalizedMessage(), e);
		}
		return new ResponseEntity<BaseContentRespVo<String>>(result, HttpStatus.OK);
	}
}
