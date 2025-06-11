package com.dhn.client.controller;

import com.dhn.client.bean.ImageBean;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Component
@Slf4j
public class MMSSendRequest implements ApplicationListener<ContextRefreshedEvent>{
	
	public static boolean isStart = false;
	private boolean isProc = false;
	private boolean isProcMms = false;
	private SQLParameter param = new SQLParameter();
	private String dhnServer;
	private String userid;
	private String basepath;
	private String preGroupNo = "";
	private String log_table;

	private static final ExecutorService executorService = Executors.newFixedThreadPool(2);

	@Autowired
	private MSGRequestService msgRequestService;
	
	@Autowired
	private ApplicationContext appContext;

	@Autowired
	private MSGService msgService;

	@Autowired
	private WebClient webClient;

	@Autowired
	private ScheduledAnnotationBeanPostProcessor posts;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		param.setMsg_table(appContext.getEnvironment().getProperty("dhnclient.msg_table"));
		param.setMsg_use(appContext.getEnvironment().getProperty("dhnclient.msg_use"));
		param.setDatabase(appContext.getEnvironment().getProperty("dhnclient.database"));
		param.setSequence(appContext.getEnvironment().getProperty("dhnclient.msg_seq"));
		param.setUserid(appContext.getEnvironment().getProperty("dhnclient.userid"));
		param.setLog_back(appContext.getEnvironment().getProperty("dhnclient.log_back","Y"));
		log_table = appContext.getEnvironment().getProperty("dhnclient.log_table");
		param.setMsg_type("PH");
		param.setSms_kind("M");
		

		dhnServer = appContext.getEnvironment().getProperty("dhnclient.server");
		userid = appContext.getEnvironment().getProperty("dhnclient.userid");
		
		// 풀 경로를 DB에 담는듯.
		basepath = appContext.getEnvironment().getProperty("dhnclient.file_base_path")==null?"":appContext.getEnvironment().getProperty("dhnclient.file_base_path");

		if (param.getMsg_use() != null && param.getMsg_use().equalsIgnoreCase("Y")) {
			isStart = true;
			log.info("MMS 초기화 완료");
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

			if(activeThreads < 2){
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
				LocalDateTime now = LocalDateTime.now();
				String group_no = "M" + now.format(formatter);

				if(!group_no.equals(preGroupNo)) {
					try{
						int cnt = msgRequestService.selectMMSReqeustCount(param);

						if(cnt > 0) {
							param.setGroup_no(group_no);
							msgRequestService.updateMMSGroupNo(param);

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
						log.error("MMS 메세지 전송 오류 : " + e.toString());
					}

					preGroupNo = group_no;
				}
			}
			isProc = false;
		}
	}

	@Scheduled(fixedDelay = 100)
	private void GETImageKey() {
		if(isStart && !isProcMms) {
			isProcMms = true;

			try {

				int cnt = msgRequestService.selectMMSImageCount(param);

				if(cnt > 0){
					List<ImageBean> imgList = msgRequestService.selectMMSImage(param);

					for (ImageBean mmsImageBean : imgList) {
						param.setMsgid(mmsImageBean.getMsgid());

						LocalDate now = LocalDate.now();
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
						String currentMonth = now.format(formatter);

						MultipartBodyBuilder builder = new MultipartBodyBuilder();
						builder.part("userid",userid);

						if (mmsImageBean.getFile1() != null && !mmsImageBean.getFile1().isEmpty()) {
							builder.part("image1", new FileSystemResource(basepath + mmsImageBean.getFile1()));
						}
						if (mmsImageBean.getFile2() != null && !mmsImageBean.getFile2().isEmpty()) {
							builder.part("image2", new FileSystemResource(basepath + mmsImageBean.getFile2()));
						}
						if (mmsImageBean.getFile3() != null && !mmsImageBean.getFile3().isEmpty()) {
							builder.part("image3", new FileSystemResource(basepath + mmsImageBean.getFile3()));
						}

						try{
							webClient.post()
									.uri("mms/image")
									.header("userid", userid)
									.contentType(MediaType.MULTIPART_FORM_DATA)
									.body(BodyInserters.fromMultipartData(builder.build()))
									.retrieve()
									.bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
									.doOnNext(res -> {
										try{
											log.info("MMS Image Key : {}", res);
											if (res.get("image_group") != null && !res.get("image_group").isEmpty()) {
												param.setMms_key(res.get("image_group"));
												msgRequestService.updateMMSImageGroup(param);
											} else {
												log.info("MMS 이미지 등록 실패 : {}", res);
												if(param.getLog_back() != null && param.getLog_back().equalsIgnoreCase("Y")){
													param.setLog_table(log_table + "_" + currentMonth);
												}else{
													param.setLog_table(log_table);
												}
												param.setMsg_image_code("9999");
												msgRequestService.updateMMSImageFail(param);
											}
										}catch (Exception e){
											log.error("MMS 이미지 처리 중 오류 발생 : {}", e.toString());
										}
									})
									.doOnError(e -> {
										try{
											log.error("MMS Image Key 등록 요청 오류 : {}", e.toString());
											if(param.getLog_back() != null && param.getLog_back().equalsIgnoreCase("Y")){
												param.setLog_table(log_table + "_" + currentMonth);
											}else{
												param.setLog_table(log_table);
											}
											param.setMsg_image_code("9999");
											msgRequestService.updateMMSImageFail(param);
										}catch (Exception ex){
											log.error("MMS 이미지 등록 요청 실패 중 오류 발생 : {}", e.toString());
										}
									})
									.block(); // 반드시 동기 처리 필요 시 block()
						}catch (Exception e){
							log.error("WebClient 처리 오류", e);
							if(param.getLog_back() != null && param.getLog_back().equalsIgnoreCase("Y")){
								param.setLog_table(log_table + "_" + currentMonth);
							}else{
								param.setLog_table(log_table);
							}
							param.setMsg_image_code("9999");
							msgRequestService.updateMMSImageFail(param);
						}
					}

				}

			} catch (Exception e) {
				log.error("MMS Image 등록 오류 : " + e.toString());
			}
		}
		isProcMms = false;
	}

}
