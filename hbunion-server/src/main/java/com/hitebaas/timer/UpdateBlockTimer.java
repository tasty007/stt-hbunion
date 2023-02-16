package com.hitebaas.timer;

import java.math.BigInteger;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hitebaas.exception.SttException;
import com.hitebaas.service.BlockDealService;

@Component
public class UpdateBlockTimer {
	
	private static final Logger logger = Logger.getLogger(UpdateBlockTimer.class);
	@Value("${contract.update.startIndex}")
	public BigInteger currentBlockIndex;
	@Value("${contract.update.startIndex}")
	public BigInteger currentMaxBlockIndex;
	@Autowired
	private BlockDealService dataDealService;
	
	public UpdateBlockTimer() {
		
		
	}
	
	

	public void updateBlock() {
		while(true) {
			try {
				dataDealService.updateBlock(currentBlockIndex);
				currentBlockIndex = currentBlockIndex.add(BigInteger.ONE);
			}catch (Exception e) {
				if(e instanceof SttException) {
					logger.error(e.getMessage());
				}else {
					logger.error(e.getMessage(), e);
				}
				break;
			}
		}
	}
	
}
