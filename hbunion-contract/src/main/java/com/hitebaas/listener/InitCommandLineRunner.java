package com.hitebaas.listener;

import java.io.File;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.hitebaas.data.black.TradeBodyPool;
import com.hitebaas.data.trade.SttkScriptContractTradeBody;
import com.hitebaas.data.trade.SttScriptFunctionTradeBody;
import com.hitebaas.data.trade.sub.FuncParamInfo;
import com.hitebaas.handler.DealHandler;
import com.hitebaas.helper.IContractManager;
import com.hitebaas.netty.NettyServer;
import com.hitebaas.tvm.inns.frame.collection.BasicTypes;

@Component
public class InitCommandLineRunner extends DealHandler implements CommandLineRunner {
	private static final Logger logger = Logger.getLogger(InitCommandLineRunner.class);
	
	
	
	@Override
	public void run(String... arg0) throws Exception {
		
		
		
		
		try {
			initNetty();
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
	}
	private void testSttContract() {
		TradeBodyPool tbp = new TradeBodyPool();
		Map<String, SttkScriptContractTradeBody> tsctbMap = tbp.getTsctbMap();
		Map<String, SttScriptFunctionTradeBody> tsftbMap = tbp.getTsftbMap();
		try {
			
			SttkScriptContractTradeBody tsctb = new SttkScriptContractTradeBody();
			String content = FileUtils.readFileToString(new File(System.getProperty("user.dir") + "/src/main/example/SttString"));
			tsctb.setAddress("0x1634896498496486486");
			tsctb.setContent(content);
			tsctb.setPublicKey("");
			tsctb.setSign("");
			tsctb.setGas(new BigDecimal("0.001"));
			tsctbMap.put(tsctb.getAddress(), tsctb);
			dealContract(tbp.getTsctbMap(), tbp.getTsftbMap());
			
			tsctbMap.clear();
			SttScriptFunctionTradeBody tsftb = new SttScriptFunctionTradeBody();
			tsftb.setAddress("0x1634896498496486486");
			tsftb.setFuncName("add");
			tsftb.setContractName("SttString");
			tsftb.setGas(new BigDecimal("0.001"));
			List<FuncParamInfo> funcParamsInfo = new ArrayList<FuncParamInfo>();
			FuncParamInfo fpi = new FuncParamInfo();
			fpi.setName("address");
			fpi.setType(BasicTypes.String.name());
			fpi.setValue("0x1634896498496486487");
			funcParamsInfo.add(fpi);

			FuncParamInfo fpi2 = new FuncParamInfo();
			fpi2.setName("amount");
			fpi2.setType(BasicTypes.Decimal.name());
			fpi2.setValue("3655");
			funcParamsInfo.add(fpi2);
			
			tsftb.setFuncParamsInfo(funcParamsInfo);
			tsftbMap.put(tsctb.getAddress(), tsftb);
			dealContract(tbp.getTsctbMap(), tbp.getTsftbMap());
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void testVoteContract() {
		TradeBodyPool tbp = new TradeBodyPool();
		Map<String, SttkScriptContractTradeBody> tsctbMap = tbp.getTsctbMap();
		Map<String, SttScriptFunctionTradeBody> tsftbMap = tbp.getTsftbMap();
		try {
			
			SttkScriptContractTradeBody tsctb = new SttkScriptContractTradeBody();
			String content = FileUtils.readFileToString(new File(System.getProperty("user.dir") + "/src/main/example/vote"));
			tsctb.setAddress("0x1634896498496486486");
			tsctb.setContent(content);
			tsctb.setPublicKey("");
			tsctb.setSign("");
			tsctb.setGas(new BigDecimal("0.001"));
			tsctbMap.put(tsctb.getAddress(), tsctb);
			dealContract(tbp.getTsctbMap(), tbp.getTsftbMap());
			
			tsctbMap.clear();
			SttScriptFunctionTradeBody tsftb = new SttScriptFunctionTradeBody();
			tsftb.setAddress("0x1634896498496486487");
			tsftb.setFuncName("add");
			tsftb.setContractName("Vote");
			tsftb.setGas(new BigDecimal("0.001"));
			List<FuncParamInfo> funcParamsInfo = new ArrayList<FuncParamInfo>();
			FuncParamInfo fpi = new FuncParamInfo();
			fpi.setName("item");
			fpi.setType(BasicTypes.String.name());
			fpi.setValue("A");
			funcParamsInfo.add(fpi);
			tsftb.setFuncParamsInfo(funcParamsInfo);
			tsftbMap.put(tsctb.getAddress(), tsftb);
			dealContract(tbp.getTsctbMap(), tbp.getTsftbMap());
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	
	private void initNetty() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				NettyServer nettyServer = new NettyServer();
		        nettyServer.start(new InetSocketAddress(8221));
			}
		}).start();
	}
}
