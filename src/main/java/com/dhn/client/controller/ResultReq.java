package com.dhn.client.controller;

import com.dhn.client.bean.Msg_Log;
import com.dhn.client.service.KAORequestService;
import com.dhn.client.service.MSGRequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Component
@Slf4j
public class ResultReq implements ApplicationListener<ContextRefreshedEvent>{
	
	public static boolean isStart = false;
	private boolean isProc = false;
	//private SQLParameter param = new SQLParameter();
	private String dhnServer;
	private String userid;
	private Map<String, String> _kaoCode = new HashMap<String,String>();
	private static int procCnt = 0;
	private String msg_table = "";
	private String log_table = "";
	private String database = "";
	private String log_back = "";

	private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

	@Autowired
	private KAORequestService kaoRequestService;

	@Autowired
	private MSGRequestService msgRequestService;
	
	@Autowired
	private ApplicationContext appContext;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		msg_table = appContext.getEnvironment().getProperty("dhnclient.msg_table");
		log_table = appContext.getEnvironment().getProperty("dhnclient.log_table");
		database = appContext.getEnvironment().getProperty("dhnclient.database");
		log_back = appContext.getEnvironment().getProperty("dhnclient.log_back","Y");

		dhnServer = appContext.getEnvironment().getProperty("dhnclient.server");
		userid = appContext.getEnvironment().getProperty("dhnclient.userid");
		
		isStart = true;
	}


	@Scheduled(fixedDelay = 100)
	private void SendProcess() {
		ThreadPoolExecutor poolExecutor = (ThreadPoolExecutor) executorService;
		int activeThreads = poolExecutor.getActiveCount();

		if(isStart && !isProc && activeThreads < 10) {
			isProc = true;
			try {
				ObjectMapper om = new ObjectMapper();
				HttpHeaders header = new HttpHeaders();
				
				header.setContentType(MediaType.APPLICATION_JSON);
				header.set("userid", userid);
				
				RestTemplate rt = new RestTemplate();
				HttpEntity<String> entity = new HttpEntity<String>(null, header);
				
				try {
					ResponseEntity<String> response = rt.postForEntity(dhnServer + "result", entity, String.class);
											
					if(response.getStatusCode() ==  HttpStatus.OK)
					{
						String responseBody = response.getBody();
						JSONObject jsonObject = new JSONObject(responseBody);

						if (jsonObject.has("data")) {
							JSONObject dataObject = jsonObject.getJSONObject("data");

							if (dataObject.has("detail")) {
								JSONArray jsonArray = dataObject.getJSONArray("detail");

								if (jsonArray.length() > 0) {

									executorService.submit(() -> ResultProc(jsonArray));
								}
							} else {
								log.error("결과 수신 오류 : 결과 배열(detail)이 없습니다.");
							}
						} else {
							log.error("결과 수신 오류 : (data) 필드가 없습니다.");
						}
					} else {
						log.info("결과 수신 오류 (Http Err) : " + response.getStatusCode());
					}
				} catch(Exception ex) {
					log.info("결과 수신 오류 (response Err): " + ex.toString());
					Thread.sleep(10000);
				}
				
			}catch (Exception e) {
				log.error("결과 수신 오류 : " + e.toString());
			}
			isProc = false;
		}
	}


	private void ResultProc(JSONArray json) {
		for(int i=0; i<json.length(); i++) {
			JSONObject ent = json.getJSONObject(i);
			
			Msg_Log kao_ml = new Msg_Log();
			Msg_Log msg_ml = new Msg_Log();

			LocalDate now = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
			String currentMonth = now.format(formatter);

			String result_message = ent.getString("message").isEmpty()?"":ent.getString("message");

			if(ent.getString("message_type").equalsIgnoreCase("AT") || ent.getString("message_type").equalsIgnoreCase("AI") || ent.getString("message_type").equalsIgnoreCase("FI") || ent.getString("message_type").equalsIgnoreCase("FT")){
				// 알림톡
				kao_ml.setMsgid(ent.getString("msgid"));
				kao_ml.setMsg_table(msg_table);
				if(log_back.equalsIgnoreCase("Y")){
					kao_ml.setLog_table(log_table+"_"+currentMonth);
				}else{
					kao_ml.setLog_table(log_table);
				}
				kao_ml.setDatabase(database);

				kao_ml.setResult_dt(ent.getString("res_dt"));
				kao_ml.setS_code(ent.getString("s_code"));
				kao_ml.setResult_message(result_message.equalsIgnoreCase("")?"":result_message);

				if(ent.getString("s_code").equals("0000")){
					kao_ml.setStatus("3");
				}else{
					kao_ml.setStatus("4");
				}

			}else if(ent.getString("message_type").equalsIgnoreCase("PH") && ent.has("s_code") && !ent.isNull("s_code") && ent.getString("s_code").length() > 1){
				// 알림톡 실패 문자
				kao_ml.setMsgid(ent.getString("msgid"));
				kao_ml.setMsg_table(msg_table);
				if(log_back.equalsIgnoreCase("Y")){
					kao_ml.setLog_table(log_table+"_"+currentMonth);
				}else{
					kao_ml.setLog_table(log_table);
				}
				kao_ml.setDatabase(database);

				kao_ml.setS_code(ent.getString("s_code"));
				kao_ml.setCode(ent.getString("code"));

				if(ent.getString("remark1").equalsIgnoreCase("LGT") || ent.getString("remark1").equals("019")){
					kao_ml.setTelecom("LGT");
				}else if(ent.getString("remark1").equalsIgnoreCase("SKT") || ent.getString("remark1").equals("011")){
					kao_ml.setTelecom("SKT");
				}else if(ent.getString("remark1").equalsIgnoreCase("KTF") || ent.getString("remark1").equalsIgnoreCase("KT") || ent.getString("remark1").equals("016")){
					kao_ml.setTelecom("KTF");
				}else{
					kao_ml.setTelecom("ETC");
				}
				kao_ml.setResult_dt(ent.getString("remark2"));
				kao_ml.setResult_message(result_message.equalsIgnoreCase("")?"":result_message);

				if(ent.getString("code").equals("0000")){
					kao_ml.setStatus("3");
				}else{
					kao_ml.setStatus("4");
				}
				kao_ml.setReal_send_type(ent.getString("sms_kind"));

			}else{
				// 문자
				msg_ml.setMsgid(ent.getString("msgid"));
				msg_ml.setMsg_table(msg_table);
				if(log_back.equalsIgnoreCase("Y")){
					msg_ml.setLog_table(log_table+"_"+currentMonth);
				}else{
					msg_ml.setLog_table(log_table);
				}
				msg_ml.setDatabase(database);

				msg_ml.setCode(ent.getString("code"));
				msg_ml.setReal_send_type(ent.getString("sms_kind"));

				if(ent.getString("remark1").equalsIgnoreCase("LGT") || ent.getString("remark1").equals("019")){
					msg_ml.setTelecom("LGT");
				}else if(ent.getString("remark1").equalsIgnoreCase("SKT") || ent.getString("remark1").equals("011")){
					msg_ml.setTelecom("SKT");
				}else if(ent.getString("remark1").equalsIgnoreCase("KTF") || ent.getString("remark1").equalsIgnoreCase("KT") || ent.getString("remark1").equals("016")){
					msg_ml.setTelecom("KTF");
				}else{
					msg_ml.setTelecom("ETC");
				}

				msg_ml.setResult_dt(ent.getString("remark2"));
				msg_ml.setResult_message(result_message.equalsIgnoreCase("")?"":result_message);

				if(ent.getString("code").equals("0000")){
					msg_ml.setStatus("3");
				}else{
					msg_ml.setStatus("4");
				}
			}

			try{
				if (msg_ml.getMsg_table() != null && msg_ml.getLog_table() != null) {
					msgRequestService.msgResultInsert(msg_ml);
				}else if (kao_ml.getMsg_table() != null && kao_ml.getLog_table() != null) {
					kaoRequestService.kaoResultInsert(kao_ml);
				}
			}catch (Exception e){
				log.error("결과 처리 오류 : "+ e.getMessage());
			}

		}
		log.info("결과 수신 완료 : " + json.length() + " 건");		
		procCnt--;
		
	}

}
