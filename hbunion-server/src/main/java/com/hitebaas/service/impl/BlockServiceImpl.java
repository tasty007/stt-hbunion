package com.hitebaas.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hitebaas.data.black.Block;



@Component
public class BlockServiceImpl{
	
	
	private static final Logger logger = Logger.getLogger(BlockServiceImpl.class);
	public static Block getBlockObeject(ZipInputStream zis) {
		ByteArrayOutputStream bos = null;
		try {
			
				bos = new ByteArrayOutputStream();
				byte[] buffer = new byte[512];
				int len = 0;
				while ((len = zis.read(buffer)) != -1) {
					bos.write(buffer, 0, len);
				}
				String blockStr = new String(bos.toByteArray());
				
				Block block = new Gson().fromJson(blockStr, Block.class);
				return block;
			
		} catch (JsonSyntaxException | IOException e) {
			logger.error("CollectionServiceImpl.getBlockObeject() ERROR ："+e.getMessage());

		}finally {
			if(bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					logger.error("CollectionServiceImpl.getBlockObeject() ERROR ："+e.getMessage());

				}
			}
		}
		return null;
	}
	
	public static ByteArrayOutputStream getOutputStream(ZipInputStream zis) {
		ByteArrayOutputStream bos = null;
		try {
			
				bos = new ByteArrayOutputStream();
				byte[] buffer = new byte[512];
				int len = 0;
				while ((len = zis.read(buffer)) != -1) {
					bos.write(buffer, 0, len);
				}
				
				return bos;
			
		} catch (JsonSyntaxException | IOException e) {
			logger.error("CollectionServiceImpl.getBlockObeject() ERROR ："+e.getMessage());

		}finally {
			if(bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					logger.error("CollectionServiceImpl.getBlockObeject() ERROR ："+e.getMessage());

				}
			}
		}
		return null;
	}
	public static String getBlockFileStr(ZipInputStream zis) {
		ByteArrayOutputStream bos = null;
		try {
			
				bos = new ByteArrayOutputStream();
				byte[] buffer = new byte[512];
				int len = 0;
				while ((len = zis.read(buffer)) != -1) {
					bos.write(buffer, 0, len);
				}
				String blockfileStr = new String(bos.toByteArray());
				return blockfileStr;
			
		} catch (JsonSyntaxException | IOException e) {
			logger.error("CollectionServiceImpl.getBlockFileStr() ERROR ："+e.getMessage());

		}finally {
			if(bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					logger.error("CollectionServiceImpl.getBlockFileStr() ERROR ："+e.getMessage());

				}
			}
		}
		return "";
	}
	
	
	
	public static String getContractBlockFileStr(ZipInputStream zis) {
		ByteArrayOutputStream bos = null;
		try {
			
				bos = new ByteArrayOutputStream();
				byte[] buffer = new byte[512];
				int len = 0;
				while ((len = zis.read(buffer)) != -1) {
					bos.write(buffer, 0, len);
				}
				String contractBlockFileStr = new String(bos.toByteArray());
				return contractBlockFileStr;
			
		} catch (JsonSyntaxException | IOException e) {
			logger.error("CollectionServiceImpl.getBlockFileStr() ERROR ："+e.getMessage());

		}finally {
			if(bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					logger.error("CollectionServiceImpl.getBlockFileStr() ERROR ："+e.getMessage());

				}
			}
		}
		return "";
	}
	
	public static String getMaxBlockIndexStr(ZipInputStream zis) {
		ByteArrayOutputStream bos = null;
		try {
			
			
				bos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = zis.read(buffer)) != -1) {
					bos.write(buffer, 0, len);
				}
				String maxBlockIndex = new String(bos.toByteArray());
				return maxBlockIndex;
			
		} catch (JsonSyntaxException | IOException e) {
			logger.error("CollectionServiceImpl.getMaxBlockIndexStr() ERROR ："+e.getMessage());

		}finally {
			if(bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					logger.error("CollectionServiceImpl.getMaxBlockIndexStr() ERROR ："+e.getMessage());

				}
			}
		}
		return "";
	}
}
