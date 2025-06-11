package com.dhn.client.controller;

import com.dhn.client.bean.ImageBean;
import com.dhn.client.bean.KAORequestBean;
import com.dhn.client.bean.SQLParameter;
import com.dhn.client.service.KAORequestService;
import com.dhn.client.service.KAOService;
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
public class FTSendRequest implements ApplicationListener<ContextRefreshedEvent> {
    public static boolean isStart = false;
    private boolean isProc = false;
    private boolean isProcImg = false;
    private SQLParameter param = new SQLParameter();
    private String dhnServer;
    private String userid;
    private String preGroupNo = "";
    private String basepath = "";
    private String log_table;

    private static final ExecutorService executorService = Executors.newFixedThreadPool(3);

    @Autowired
    private KAORequestService kaoRequestService;

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private KAOService KAOService;

    @Autowired
    private WebClient webClient;

    @Autowired
    private ScheduledAnnotationBeanPostProcessor posts;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        param.setMsg_table(appContext.getEnvironment().getProperty("dhnclient.msg_table"));
        log_table = appContext.getEnvironment().getProperty("dhnclient.log_table");
        param.setKakao_use(appContext.getEnvironment().getProperty("dhnclient.kakao_use"));
        param.setDatabase(appContext.getEnvironment().getProperty("dhnclient.database"));
        param.setSequence(appContext.getEnvironment().getProperty("dhnclient.msg_seq"));
        param.setUserid(appContext.getEnvironment().getProperty("dhnclient.userid"));
        basepath = appContext.getEnvironment().getProperty("dhnclient.file_base_path")==null?"":appContext.getEnvironment().getProperty("dhnclient.file_base_path");
        param.setMsg_type("FT");

        dhnServer = appContext.getEnvironment().getProperty("dhnclient.server");
        userid = appContext.getEnvironment().getProperty("dhnclient.userid");

        if (param.getKakao_use() != null && param.getKakao_use().equalsIgnoreCase("Y")) {
            isStart = true;
            log.info("FT 초기화 완료");
        } else {
            posts.postProcessBeforeDestruction(this, null);
        }
    }

    @Scheduled(fixedDelay = 100)
    public void SendProcess() {
        if(isStart && !isProc) {
            isProc = true;

            ThreadPoolExecutor poolExecutor = (ThreadPoolExecutor) executorService;
            int activeThreads = poolExecutor.getActiveCount();

            if(activeThreads < 3){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
                LocalDateTime now = LocalDateTime.now();
                String group_no = "F" + now.format(formatter);

                if(!group_no.equals(preGroupNo)) {
                    try {
                        int cnt = kaoRequestService.selectFTRequestCount(param);

                        if(cnt > 0){
                            param.setGroup_no(group_no);
                            kaoRequestService.updateFTGroupNo(param);

                            SQLParameter sendParam = new SQLParameter();
                            sendParam.setGroup_no(group_no);
                            sendParam.setMsg_table(param.getMsg_table());
                            sendParam.setDatabase(param.getDatabase());
                            sendParam.setSequence(param.getSequence());
                            sendParam.setMsg_type(param.getMsg_type());
                            sendParam.setUserid(param.getUserid());

                            executorService.submit(() -> KAOService.KAOSendApiProcess(sendParam));
                        }

                    }catch (Exception e){
                        log.error("FT 메세지 전송 오류 : " + e.toString());
                    }
                    preGroupNo = group_no;
                }
            }
            isProc = false;
        }
    }


    @Scheduled(fixedDelay = 100)
    public void GetFTImage(){
        if(isStart && !isProcImg) {
            isProcImg = true;

            try{
                int cnt = kaoRequestService.selectFtImageCount(param);

                if(cnt > 0){
                    List<ImageBean> ftimages = kaoRequestService.selectFtImage(param);

                    for (ImageBean ftimage : ftimages) {

                        param.setMsgid(ftimage.getMsgid());

                        MultipartBodyBuilder builder = new MultipartBodyBuilder();
                        builder.part("userid",userid);

                        if (ftimage.getFtimagepath() != null && !ftimage.getFtimagepath().isEmpty()) {
                            builder.part("image", new FileSystemResource(basepath + ftimage.getFtimagepath()));
                        }

                        try{
                            webClient.post()
                                    .uri("ft/image")
                                    .header("userid", userid)
                                    .contentType(MediaType.MULTIPART_FORM_DATA)
                                    .body(BodyInserters.fromMultipartData(builder.build()))
                                    .retrieve()
                                    .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
                                    .doOnNext(res -> {
                                        try{
                                            log.info("친구톡 이미지 : {}", res);

                                            String code = res.get("code");
                                            String message = res.get("message");
                                            String imageUrl = res.get("image");

                                            param.setFt_image_code(code);
                                            param.setImg_err_msg(message);

                                            if ("0000".equals(code)) {
                                                log.info("친구톡 이미지 URL : {}", imageUrl);
                                                param.setFt_image_url(imageUrl);
                                                kaoRequestService.updateFTImageUrl(param);
                                            } else {
                                                handleFTImageFail(res);
                                            }
                                        }catch (Exception ex){
                                            log.error("FT 이미지 처리 중 오류: {}", ex.toString());
                                        }
                                    })
                                    .doOnError(e -> {
                                        try {
                                            log.error("WebClient 통신 오류 : {}", e.toString());
                                            Map<String, String> errorMap = Map.of("code", "9999", "message", "친구톡 이미지 등록 요청 중 오류 발생");
                                            handleFTImageFail(errorMap);
                                        } catch (Exception ex) {
                                            log.error("FT 이미지 오류 처리 중 예외", ex);
                                        }
                                    })
                                    .block(); // 반드시 동기 처리 필요 시 block()
                        }catch (Exception e){
                            log.error("FT Image 등록 오류 : {}", e.toString());
                            Map<String, String> errorMap = Map.of("code", "9999", "message", "친구톡 이미지 등록 중 오류 발생");
                            handleFTImageFail(errorMap);
                        }
                    }
                }

            }catch (Exception e) {
                log.error("FT Image 등록 오류 : " + e.toString());
            }
        }
        isProcImg = false;
    }

    private void handleFTImageFail(Map<String, String> res) {
        LocalDate now = LocalDate.now();
        String currentMonth = now.format(DateTimeFormatter.ofPattern("yyyyMM"));

        if ("Y".equalsIgnoreCase(param.getLog_back())) {
            param.setLog_table(log_table + "_" + currentMonth);
        } else {
            param.setLog_table(log_table);
        }

        String code = res.getOrDefault("code", "9999");
        if ("error".equals(code)) code = "9999";

        param.setFt_image_code(code);
        param.setImg_err_msg(res.getOrDefault("message", "알 수 없는 오류"));
        try{
            kaoRequestService.updateFTImageFail(param);
        }catch (Exception ex){
            log.error("친구톡 이미지 등록 실패 처리 중 오류 발생 : {}",ex.toString());
        }
    }
}
