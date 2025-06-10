package com.dhn.client.controller;

import com.dhn.client.bean.ImageBean;
import com.dhn.client.bean.KAORequestBean;
import com.dhn.client.bean.SQLParameter;
import com.dhn.client.service.KAORequestService;
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
    private ScheduledAnnotationBeanPostProcessor posts;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        param.setMsg_table(appContext.getEnvironment().getProperty("dhnclient.msg_table"));
        log_table = appContext.getEnvironment().getProperty("dhnclient.log_table");
        param.setKakao_use(appContext.getEnvironment().getProperty("dhnclient.kakao_use"));
        param.setDatabase(appContext.getEnvironment().getProperty("dhnclient.database"));
        param.setSequence(appContext.getEnvironment().getProperty("dhnclient.msg_seq"));
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

                            executorService.submit(() -> APIProcess(group_no));
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

                        // 헤더 설정
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                        headers.set("userid", userid);

                        // MultiValueMap을 사용해 파일 데이터 전송 준비
                        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                        body.add("userid", userid);

                        if (ftimage.getFtimagepath() != null && !ftimage.getFtimagepath().isEmpty()) {
                            File file = new File(basepath + ftimage.getFtimagepath());
                            body.add("image", new org.springframework.core.io.FileSystemResource(file));
                        }

                        // HttpEntity 생성
                        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

                        RestTemplate restTemplate = new RestTemplate();
                        try{
                            ResponseEntity<String> response = restTemplate.exchange(dhnServer + "ft/image", HttpMethod.POST, requestEntity, String.class);

                            LocalDate now = LocalDate.now();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
                            String currentMonth = now.format(formatter);

                            if (response.getStatusCode() == HttpStatus.OK) {
                                String responseBody = response.getBody();
                                ObjectMapper mapper = new ObjectMapper();
                                Map<String, String> res = mapper.readValue(responseBody, Map.class);

                                param.setFt_image_code(res.get("code"));
                                param.setMsgid(ftimage.getMsgid());
                                param.setImg_err_msg(res.get("message"));

                                if(param.getFt_image_code().equals("0000")){
                                    log.info("친구톡 이미지 URL : "+res.get("image"));

                                    param.setFt_image_url(res.get("image"));
                                    kaoRequestService.updateFTImageUrl(param);
                                }else{

                                    log.error("친구톡 이미지 등록 실패 : "+res.toString());

                                    param.setLog_table(log_table+"_"+currentMonth);
                                    if(param.getFt_image_code().equals("error")){
                                        param.setFt_image_code("9999");
                                    }
                                    kaoRequestService.updateFTImageFail(param);
                                }
                            } else {
                                log.error("친구톡 이미지 등록 실패 통신오류 : "+response.getBody());
                                param.setLog_table(log_table+"_"+currentMonth);
                                if(param.getFt_image_code().equals("error")){
                                    param.setFt_image_code("9999");
                                }
                                param.setImg_err_msg("KAKAO 통신 오류");
                                kaoRequestService.updateFTImageFail(param);
                            }

                        }catch (Exception e){
                            log.error("FT Image URL 등록 오류: ", e.getMessage());
                        }
                    }
                }

            }catch (Exception e) {
                log.error("FT Image 등록 오류 : " + e.toString());
            }
        }
        isProcImg = false;
    }

    private void APIProcess(String group_no) {
        try{
            SQLParameter sendParam = new SQLParameter();
            sendParam.setGroup_no(group_no);
            sendParam.setMsg_table(param.getMsg_table());
            sendParam.setDatabase(param.getDatabase());
            sendParam.setSequence(param.getSequence());
            sendParam.setMsg_type(param.getMsg_type());


            List<KAORequestBean> _list = kaoRequestService.selectFTRequests(sendParam);

            StringWriter sw = new StringWriter();
            ObjectMapper om = new ObjectMapper();
            om.writeValue(sw, _list); // List를 Json화 하여 문자열 저장

            HttpHeaders header = new HttpHeaders();

            header.setContentType(MediaType.APPLICATION_JSON);
            header.set("userid", userid);

            RestTemplate rt = new RestTemplate();
            HttpEntity<String> entity = new HttpEntity<String>(sw.toString(), header);

            try {
                ResponseEntity<String> response = rt.postForEntity(dhnServer + "req", entity, String.class);
                Map<String, String> res = om.readValue(response.getBody().toString(), Map.class);
                log.info(res.toString());
                if (response.getStatusCode() == HttpStatus.OK) { // 데이터 정상적으로 전달
                    kaoRequestService.updateKAOSendComplete(sendParam);
                    log.info("FT 메세지 전송 완료 : " + response.getStatusCode() + " / " + group_no + " / " + _list.size() + " 건");
                } else { // API 전송 실패시
                    log.info("({}) FT 메세지 전송오류 : {}",res.get("userid"), res.get("message"));
                    kaoRequestService.updateKAOSendInit(sendParam);
                }
            } catch (Exception e) {
                log.error("FT 메세지 전송 오류 : " + e.toString());
                kaoRequestService.updateKAOSendInit(sendParam);
            }
        }catch (Exception e){
            log.error("FT 메세지 전송 오류 : " + e.toString());
        }
    }
}
