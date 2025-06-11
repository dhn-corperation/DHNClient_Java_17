package com.dhn.client.controller;

import com.dhn.client.bean.RequestBean;
import com.dhn.client.bean.SQLParameter;
import com.dhn.client.service.MSGRequestService;
import com.dhn.client.service.MSGService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Component
@Slf4j
public class LMSSendRequest implements ApplicationListener<ContextRefreshedEvent>{
	
	public static boolean isStart = false;
	private boolean isProc = false;
	private SQLParameter param = new SQLParameter();
	private String dhnServer;
	private String userid;
	private String preGroupNo = "";

	private static final ExecutorService executorService = Executors.newFixedThreadPool(2);

	@Autowired
	private MSGRequestService msgRequestService;
	
	@Autowired
	private ApplicationContext appContext;

	@Autowired
	private MSGService msgService;

	@Autowired
	private ScheduledAnnotationBeanPostProcessor posts;


	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		param.setMsg_table( appContext.getEnvironment().getProperty("dhnclient.msg_table"));
		param.setMsg_use(appContext.getEnvironment().getProperty("dhnclient.msg_use"));
		param.setDatabase(appContext.getEnvironment().getProperty("dhnclient.database"));
		param.setSequence(appContext.getEnvironment().getProperty("dhnclient.msg_seq"));
		param.setUserid(appContext.getEnvironment().getProperty("dhnclient.userid"));
		param.setMsg_type("PH");
		param.setSms_kind("L");

		dhnServer = appContext.getEnvironment().getProperty("dhnclient.server");
		userid = appContext.getEnvironment().getProperty("dhnclient.userid");

		if (param.getMsg_use() != null && param.getMsg_use().equalsIgnoreCase("Y")) {
			isStart = true;
			log.info("LMS 초기화 완료");
		} else {
				posts.postProcessBeforeDestruction(this, null);
		}

	}

	@Scheduled(fixedDelay = 100)
	private void SendProcess() {
		if(isStart && !isProc) {
			isProc = true;

			ThreadPoolExecutor poolExecutor = (ThreadPoolExecutor) executorService;
			int activeThreads = poolExecutor.getActiveCount();

			if(activeThreads < 5){
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
				LocalDateTime now = LocalDateTime.now();
				String group_no = "L" + now.format(formatter);

				if(!group_no.equals(preGroupNo)) {
					try{
						int cnt = msgRequestService.selectLMSReqeustCount(param);

						if(cnt > 0) {
							param.setGroup_no(group_no);
							msgRequestService.updateLMSGroupNo(param);

							SQLParameter sendParam = new SQLParameter();
							sendParam.setGroup_no(group_no);
							sendParam.setMsg_table(param.getMsg_table());
							sendParam.setDatabase(param.getDatabase());
							sendParam.setSequence(param.getSequence());
							sendParam.setMsg_type(param.getMsg_type());
							sendParam.setSms_kind(param.getSms_kind());
							sendParam.setUserid(param.getUserid());

							executorService.submit(() -> msgService.MSGSendApiProcess(sendParam));
						}

					}catch (Exception e){
						log.error("LMS 메세지 전송 오류 : " + e.toString());
					}

					preGroupNo = group_no;
				}
			}
			isProc = false;
		}
	}


}
