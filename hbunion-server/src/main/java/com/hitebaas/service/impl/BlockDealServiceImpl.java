package com.hitebaas.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.hitebaas.data.NoticeParams;
import com.hitebaas.data.black.BlockDownLoad;
import com.hitebaas.data.black.TradeBodyPool;
import com.hitebaas.entity.HiteBassConstant;
import com.hitebaas.entry.IpPort;
import com.hitebaas.exception.ErrorInfo;
import com.hitebaas.exception.SttException;
import com.hitebaas.handler.DealHandler;
import com.hitebaas.service.BlockDealService;
import com.hitebaas.utils.HttpClientHelper;

@Service
public class BlockDealServiceImpl extends DealHandler implements BlockDealService {
	
	private static final Logger logger = Logger.getLogger(BlockDealServiceImpl.class);

	@Value("${block.update.ips}")
	private String ips;
	@Value("${block.update.port}")
	private int port;
	
	public BlockDealServiceImpl() {
	}
	
	private IpPort currentIp;

	
	@Override
	public void updateBlock(BigInteger blockIndex) throws Exception {
		try {
			List<IpPort> ips = getRandomIps();
			BlockDownLoad bdl = null;
			if(currentIp != null) {
				NoticeParams np = new NoticeParams(blockIndex.toString(), currentIp.getIp() + ":" + currentIp.getPort(), "");
				bdl = HttpClientHelper.downLoadBlock(currentIp.getIp(), currentIp.getPort(), np);
			}
			if(bdl == null) {
				for(int i=0;i<ips.size();i++) {
					IpPort ipp = ips.get(i);
					NoticeParams np = new NoticeParams(blockIndex.toString(), ipp.getIp() + ":" + ipp.getPort(), "");
					bdl = HttpClientHelper.downLoadBlock(ipp.getIp(), ipp.getPort(), np);
					
					if(bdl != null){
						currentIp = ipp;
						break;
					}
				}
			}
			if(bdl == null){
				throw new SttException(ErrorInfo.DOWNLOAD_CODE, ErrorInfo.DOWNLOAD_CODE_MSG);
			}
			String currentMaxBlockIndex = bdl.getMaxBlockIndex();
			System.out.println("currentIndex: " + blockIndex + " , maxBlockIndex: " + currentMaxBlockIndex);
			String fileStr=bdl.getFileString(); 
			TradeBodyPool tbp=new Gson().fromJson(fileStr, TradeBodyPool.class);
			
			dealContract(tbp.getTsctbMap(), tbp.getTsftbMap());
		}catch (Exception e) {
			
			throw e;
		}
	}

	private List<IpPort> getRandomIps(){
		List<String> ipList = Arrays.asList(ips.split(","));
		List<Integer> indexs = new ArrayList<>();
		Random random =  new Random();
		for(int i = 0 ; i < ipList.size();i++) {
			int a = random.nextInt(100) % ipList.size();
			if(!indexs.contains(a)) {
				indexs.add(a);
			}
			if(indexs.size() >= 5) {
				break;
			}
		}
		List<IpPort> result = new ArrayList<IpPort>();
		for(int i=0;i<indexs.size();i++) {
			String ipport = ipList.get(indexs.get(i));
			String[] ip = ipport.split(",");
			IpPort ipPort = new IpPort();
			ipPort.setIp(ip[0]);
			ipPort.setPort(port);
			result.add(ipPort);
		}
		return result;
	}
	
	
	public static void main(String[] args) {
		
		String ips2 = "1.1.1.1,2.2.2.2";
		BlockDealServiceImpl bds = new BlockDealServiceImpl();

		
	}
			
	
	
	
	
	
}
