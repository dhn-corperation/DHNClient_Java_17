package com.dhn.client.controller;

import com.dhn.client.bean.Msg_Log;
import com.dhn.client.bean.SQLParameter;
import com.dhn.client.service.KAORequestService;
import com.dhn.client.service.MSGRequestService;
import com.dhn.client.service.ResultService;
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
import org.springframework.web.reactive.function.client.WebClient;

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
	private WebClient webClient;

	@Autowired
	private ResultService resultService;
	
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
				
				try {

					String responseBody = webClient.post()
							.uri("result")
							.header("userid", userid)
							.retrieve()
							.bodyToMono(String.class)
							.block();
											
					if(responseBody != null){
						JSONObject jsonObject = new JSONObject(responseBody);

						if (jsonObject.has("data")) {
							JSONObject dataObject = jsonObject.getJSONObject("data");

							if (dataObject.has("detail")) {
								JSONArray jsonArray = dataObject.getJSONArray("detail");

								if (!jsonArray.isEmpty()) {

									LocalDate now = LocalDate.now();
									DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
									String currentMonth = now.format(formatter);

									SQLParameter sendParam = new SQLParameter();
									sendParam.setMsg_table(msg_table);
									if(log_back.equalsIgnoreCase("Y")){
										sendParam.setLog_table(log_table+"_"+currentMonth);
									}else{
										sendParam.setLog_table(log_table);
									}
									sendParam.setDatabase(database);

									executorService.submit(() -> resultService.ResultProcess(jsonArray,sendParam));
								}
							} else {
								log.error("결과 수신 오류 : 결과 배열(detail)이 없습니다.");
							}
						} else {
							log.error("결과 수신 오류 : (data) 필드가 없습니다.");
						}
					} else {
						log.info("결과 수신 오류 (Http Err)");
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
}
