package com.hitebaas;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSONObject;
import com.hitebaas.entity.ParamReqInfo;
import com.hitebaas.utils.RedisUtils;
import com.hitebaas.vo.run.QueryRunReqVo;
import com.hitebaas.vo.run.RunReqVo;
 
 


public class RedisTest {


	private RedisUtils redisUtils;
	



 

	public void getTest() {
		
		String contractCode = redisUtils.get("name2");
		System.out.println(contractCode);
	}
	
	

	public void setTest() {
		

		redisUtils.set("name2", "zhangsan");
		System.out.println("数据存入完成");
		
	}
	
	public static void main(String[] args) {
		RunReqVo runReqVo = new RunReqVo();
		runReqVo.setContractName("Pic4");
		runReqVo.setFuncName("record");
		runReqVo.setGas(new BigDecimal("0.004"));
		runReqVo.setCallAddress("0x1234567489");
		runReqVo.setIntegral("10");
		runReqVo.setOwnerAddress("0x1234567489");
		runReqVo.setCode("import stt.Points;Contract Pic4 {    Points point = new Points();    func record(String p) {      String str1 = CONTENT_OWNER_ADDRESS ;      String str2 = CONTENT_CALL_ADDRESS ;      point.tradePoint(p);      _print(str1);      _print(str2);    }}");
		
		List<ParamReqInfo> list = new ArrayList<ParamReqInfo>();
		ParamReqInfo pri = new ParamReqInfo();
		pri.setName("p");
		pri.setType("String");
		pri.setValue("11");
		




		
		list.add(pri);

		runReqVo.setPris(list);
		 
        String reqStr = JSONObject.toJSONString(runReqVo);
        System.out.println("Java对象转换成JSON字符串\n" + reqStr);

        
        
	}

}