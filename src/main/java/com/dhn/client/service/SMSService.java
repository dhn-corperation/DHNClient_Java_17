package com.dhn.client.service;

import com.dhn.client.bean.RequestBean;
import com.dhn.client.config.AES256_GCM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;

@Service
@Slf4j
public class SMSService {
	
	@Autowired
	private AES256_GCM aes256;
	
	public RequestBean encryption(RequestBean requestBean, String column) {
		GCMParameterSpec nonce = generateGCMParameterSpec();
		//String noncestd = Base64.getEncoder().encodeToString(nonce.getIV());
		String nonceHex = aes256.toHex(nonce.getIV());
		requestBean.setCrypto(nonceHex+","+column);
		try {
			
			if (requestBean.getPhn() != null && !requestBean.getPhn().isEmpty()) {
				requestBean.setPhn(aes256.encrypt(requestBean.getPhn(), nonce));
			}
			if(column.contains("MSG") && requestBean.getMsg() != null && !requestBean.getMsg().isEmpty()) {
				requestBean.setMsg(aes256.encrypt(requestBean.getMsg(), nonce));
			}
			if(column.contains("MESSAGETYPE") && requestBean.getMessagetype() != null && !requestBean.getMessagetype().isEmpty()) {
				requestBean.setMessagetype(aes256.encrypt(requestBean.getMessagetype(), nonce));
			}
			if(column.contains("MSGSMS") && requestBean.getMsgsms() != null && !requestBean.getMsgsms().isEmpty()) {
				requestBean.setMsgsms(aes256.encrypt(requestBean.getMsgsms(), nonce));
			}
			if(column.contains("SMSSENDER") && requestBean.getSmssender() != null && !requestBean.getSmssender().isEmpty()) {
				requestBean.setSmssender(aes256.encrypt(requestBean.getSmssender(), nonce));
			}
			if(column.contains("SMSLMSTIT") && requestBean.getSmslmstit() != null && !requestBean.getSmslmstit().isEmpty()) {
				requestBean.setSmslmstit(aes256.encrypt(requestBean.getSmslmstit(), nonce));
			}
		}catch (Exception e) {
			log.info("데이터 암호화 오류 : ",e.getMessage());
		}
		return requestBean;
	}
	
	public GCMParameterSpec generateGCMParameterSpec() {
        byte[] iv = new byte[12]; // 12바이트 nonce
        new SecureRandom().nextBytes(iv);
        return new GCMParameterSpec(128, iv);
   }

}
