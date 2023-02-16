package com.hitebaas.controller.index;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.hitebaas.config.ContractSessionConfig;
import com.hitebaas.controller.handler.BaseController;
import com.hitebaas.util.ContractConstant;
import com.hitebaas.vo.base.BaseContentRespVo;

@RestController
public class QrController extends BaseController{
	
	private static final Logger logger = Logger.getLogger(QrController.class);

	private static final int QR_WIDTH = 300;
	private static final int QR_HEIGHT = 300;

	private static final int LOGO_WIDTH = 50;
	private static final int LOGO_HEIGHT = 50;
	
	@Value("${qr.domain.url}")
	private String url;
	
	@RequestMapping(value="/dev/qr")
	@ResponseBody
	public void qr() {
		try {
			Map<EncodeHintType,Object> map =new HashMap<EncodeHintType, Object>();
			
			map.put(EncodeHintType.CHARACTER_SET,"UTF-8");
			map.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			map.put(EncodeHintType.MARGIN,"0");
			
			String httpUrl = url + getSession().getId();
			BitMatrix bitMatrix = new MultiFormatWriter().encode(httpUrl, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, map);
			BufferedImage image = toBufferedImage(bitMatrix);
			
			Graphics2D g = image.createGraphics();
			int centerX = image.getWidth() / 2;
			int centerY = image.getHeight() / 2;
			int widthB = LOGO_WIDTH + 4;
			int heightB = LOGO_HEIGHT + 4;
			 
			BufferedImage logo = getHeadImg();
			g.fillRoundRect(centerX - widthB/2, centerY - heightB/2, widthB, heightB, 4, 4);
			g.drawImage(logo, centerX - (LOGO_WIDTH / 2), centerY - (LOGO_HEIGHT / 2), LOGO_WIDTH, LOGO_HEIGHT, null);
			g.dispose();
			
			getResponse().setContentType("image/jpeg");
			ServletOutputStream sos = getResponse().getOutputStream();
			getResponse().setHeader("Pragma", "No-cache");
			getResponse().setHeader("Cache-Control", "no-cache");
			getResponse().setDateHeader("Expires", 0);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ImageIO.write(image, "JPEG", bos);
			byte[] buf = bos.toByteArray();
			getResponse().setContentLength(buf.length);
			sos.write(buf);  
			bos.close();  
			sos.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}  
	}
	
	private BufferedImage getHeadImg() {
		BufferedImage image = null;
		try {
			
			
			String path = "icon/logo.png";
			ClassPathResource classPathResource = new ClassPathResource(path);
			InputStream inputStream = classPathResource.getInputStream();
			image = ImageIO.read(inputStream);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return image;
	}
	private  BufferedImage toBufferedImage(BitMatrix matrix) {
		int[] rec = matrix.getEnclosingRectangle();
        int resWidth = rec[2] + 2;
        int resHeight = rec[3] + 2;

        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        for (int i = 1; i < resWidth; i++) {
            for (int j = 1; j < resHeight; j++) {
                if (matrix.get(i + rec[0], j + rec[1]))
                    resMatrix.set(i, j);
            }
        }
        int width = resMatrix.getWidth();
        int height = resMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, resMatrix.get(x, y) == true ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return image;
    }
	
	@RequestMapping(value="/dev/cc/{sessionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<BaseContentRespVo<String>> createContract(@PathVariable String sessionId) {
		BaseContentRespVo<String> result = new BaseContentRespVo<String>();
		try {
			HttpSession session = ContractSessionConfig.findSession(sessionId);
			if(session == null) {
				throw new Exception("二维码已经过期.");
			}
			String code = (String) session.getAttribute(ContractConstant.CODE);
			result.setCode(0);
			result.setMsg("SUCCESS");
			result.setContent(code);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setCode(-1);
			result.setMsg(e.getMessage());
			result.setContent("");
		}
		return new ResponseEntity<BaseContentRespVo<String>>(result, HttpStatus.OK);
	}
	
}
