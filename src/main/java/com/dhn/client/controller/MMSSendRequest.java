package com.dhn.client.controller;

import com.dhn.client.bean.ImageBean;
import com.dhn.client.bean.RequestBean;
import com.dhn.client.bean.SQLParameter;
import com.dhn.client.service.MSGRequestService;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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

	private static final ExecutorService executorService = Executors.newFixedThreadPool(3);

	@Autowired
	private MSGRequestService msgRequestService;
	
	@Autowired
	private ApplicationContext appContext;

	@Autowired
	private ScheduledAnnotationBeanPostProcessor posts;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		param.setMsg_table(appContext.getEnvironment().getProperty("dhnclient.msg_table"));
		param.setMsg_use(appContext.getEnvironment().getProperty("dhnclient.msg_use"));
		param.setDatabase(appContext.getEnvironment().getProperty("dhnclient.database"));
		param.setSequence(appContext.getEnvironment().getProperty("dhnclient.msg_seq"));
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

			if(activeThreads < 3){
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
				LocalDateTime now = LocalDateTime.now();
				String group_no = "M" + now.format(formatter);

				if(!group_no.equals(preGroupNo)) {
					try{
						int cnt = msgRequestService.selectMMSReqeustCount(param);

						if(cnt > 0) {
							param.setGroup_no(group_no);
							msgRequestService.updateMMSGroupNo(param);

							executorService.submit(() -> APIProcess(group_no));

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

						// 헤더 설정
						HttpHeaders headers = new HttpHeaders();
						headers.setContentType(MediaType.MULTIPART_FORM_DATA);
						headers.set("userid", userid);

						// MultiValueMap을 사용해 파일 데이터 전송 준비
						MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
						body.add("userid", userid);

						if (mmsImageBean.getFile1() != null && mmsImageBean.getFile1().length() > 0) {
							File file = new File(basepath + mmsImageBean.getFile1());
							body.add("image1", new org.springframework.core.io.FileSystemResource(file));
						}
						if (mmsImageBean.getFile2() != null && mmsImageBean.getFile2().length() > 0) {
							File file = new File(basepath + mmsImageBean.getFile2());
							body.add("image2", new org.springframework.core.io.FileSystemResource(file));
						}
						if (mmsImageBean.getFile3() != null && mmsImageBean.getFile3().length() > 0) {
							File file = new File(basepath + mmsImageBean.getFile3());
							body.add("image3", new org.springframework.core.io.FileSystemResource(file));
						}

						// HttpEntity 생성
						HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

						RestTemplate restTemplate = new RestTemplate();

						LocalDate now = LocalDate.now();
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
						String currentMonth = now.format(formatter);

						try{
							ResponseEntity<String> response = restTemplate.exchange(dhnServer + "mms/image", HttpMethod.POST, requestEntity, String.class);

							if (response.getStatusCode() == HttpStatus.OK) {
								String responseBody = response.getBody();
								ObjectMapper mapper = new ObjectMapper();
								Map<String, String> res = mapper.readValue(responseBody, Map.class);

								log.info("MMS Image Key : " + res.toString());

								if (res.get("image_group") != null && res.get("image_group").length() > 0) {
									param.setMms_key(res.get("image_group"));
									msgRequestService.updateMMSImageGroup(param);
								} else {
									log.info("MMS 이미지 등록 실패 : " + res.toString());
									param.setLog_table(log_table + "_" + currentMonth);
									param.setMsg_image_code("9999");
									msgRequestService.updateMMSImageFail(param);
								}
							} else {
								log.info("MMS 이미지 등록 실패 : " + response.getBody());
								param.setLog_table(log_table + "_" + currentMonth);
								param.setMsg_image_code(String.valueOf(response.getStatusCodeValue()));
								msgRequestService.updateMMSImageFail(param);
							}
						}catch (Exception e){
							log.error("MMS Image Key 등록 오류 : ", e.getMessage());
							param.setLog_table(log_table + "_" + currentMonth);
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

	private void APIProcess(String group_no) {
		try{
			SQLParameter sendParam = new SQLParameter();
			sendParam.setGroup_no(group_no);
			sendParam.setMsg_table(param.getMsg_table());
			sendParam.setDatabase(param.getDatabase());
			sendParam.setSequence(param.getSequence());
			sendParam.setMsg_type(param.getMsg_type());
			sendParam.setSms_kind(param.getSms_kind());

			List<RequestBean> _list = msgRequestService.selectMMSRequests(sendParam);

			StringWriter sw = new StringWriter();
			ObjectMapper om = new ObjectMapper();
			om.writeValue(sw, _list);

//						log.info(sw.toString());

			HttpHeaders header = new HttpHeaders();

			header.setContentType(MediaType.APPLICATION_JSON);
			header.set("userid", userid);

			RestTemplate rt = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<String>(sw.toString(), header);

			try {
				ResponseEntity<String> response = rt.postForEntity(dhnServer + "req", entity, String.class);
				Map<String, String> res = om.readValue(response.getBody().toString(), Map.class);
				if(response.getStatusCode() ==  HttpStatus.OK)
				{
					msgRequestService.updateSMSSendComplete(sendParam);
					log.info("MMS 메세지 전송 완료 : " + group_no + " / " + _list.size() + " 건");
				} else {
					log.info("({}) MMS 메세지 전송오류 : {}",res.get("userid"), res.get("message"));
					msgRequestService.updateSMSSendInit(sendParam);
				}
			}catch (Exception e) {
				log.error("MMS 메세지 전송 오류 : " + e.toString());
				msgRequestService.updateSMSSendInit(sendParam);
			}
		}catch (Exception e){
			log.error("MMS 메세지 전송 오류 : " + e.toString());
		}
	}

}
