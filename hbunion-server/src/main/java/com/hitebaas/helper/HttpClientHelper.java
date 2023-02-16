package com.hitebaas.helper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import com.hitebaas.data.NoticeParams;
import com.hitebaas.data.black.Block;
import com.hitebaas.data.black.BlockDownLoad;
import com.hitebaas.entity.HiteBassConstant;
import com.hitebaas.service.impl.BlockServiceImpl;

public class HttpClientHelper {
	private static final Logger logger = Logger.getLogger(HttpClientHelper.class);
	
	
	public static BlockDownLoad downLoadBlock(String ip, int port, NoticeParams np) {
		BlockDownLoad bdl = new BlockDownLoad();
		String url = "";
		if(StringUtils.isNotBlank(ip)) {
			url = "http://" + ip + ":" + port + HiteBassConstant.PROJECT_CONTENT + HiteBassConstant.BLOCK_URI;
		}else{
			url = "http://" + np.getIp() + ":" + port + HiteBassConstant.PROJECT_CONTENT + HiteBassConstant.BLOCK_URI;
		}
        
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        ZipInputStream zis = null;
        HttpPost httpPost = null;
        try {
            httpPost = new HttpPost(url);
            httpPost.setHeader("Content-type", "application/json; charset=utf-8");
            httpPost.setHeader("Connection", "Close");
            httpPost.addHeader("Accept-Encoding", "GZIP");
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setConnectionRequestTimeout(15000).setSocketTimeout(15000).build();
            httpPost.setConfig(requestConfig);
            
            StringEntity entity = new StringEntity(np.toJSONString(), Charset.forName("UTF-8"));
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
           
            response = httpClient.execute(httpPost);
            
            zis = new ZipInputStream(response.getEntity().getContent(), Charset.forName("UTF-8"));
            ZipEntry ze = null;
            Block block = null;
            String blockFileStr = "";
            String contractFileStr = "";
           
            String maxIndex = "";
           
            
            Map<String, String> files =new HashMap<String, String>();
            while((ze = zis.getNextEntry()) != null) {
            	
            		if(HiteBassConstant.ZIPENTRY_BLOCKOBJECT.equals(ze.getName())) {
                		
                		block = BlockServiceImpl.getBlockObeject(zis);
                		
                	}else if(HiteBassConstant.ZIPENTRY_MAXBLOCKINDEX.equals(ze.getName())) {
                		
                		maxIndex = BlockServiceImpl.getMaxBlockIndexStr(zis);
                	}else if(HiteBassConstant.ZIPENTRY_TOK_BLOCKFILE.equals(ze.getName())){
                		
                		blockFileStr = BlockServiceImpl.getBlockFileStr(zis);
                	}else if(ze.getName().equals(np.getTradeName())){
                		
                		blockFileStr = BlockServiceImpl.getBlockFileStr(zis);
                	}else if(np.getTradeName().equals("")) {
                		String fileStr = BlockServiceImpl.getBlockFileStr(zis);
                		files.put(ze.getName(), fileStr);
                	}
            }
            if(block ==null  ) {
            	throw new Exception("down block is not complete. blockIndex=" + np.getBn());
            }
            
            bdl.setBlock(block);
            bdl.setFileString(blockFileStr);
            bdl.setMaxBlockIndex(maxIndex);
            return bdl;
        } catch (Exception e) {
            e.printStackTrace();
        	logger.error(e.getMessage(), e);
        } finally {
            try {
            	if(httpPost != null) {
            		httpPost.releaseConnection();
            	}
            	if(httpClient != null) {
                	httpClient.close();
            	}
            	if(response != null)
            		response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
	}
	
}
