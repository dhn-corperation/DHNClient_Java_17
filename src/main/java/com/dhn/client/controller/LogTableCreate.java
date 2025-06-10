package com.dhn.client.controller;

import com.dhn.client.bean.SQLParameter;
import com.dhn.client.service.CreateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LogTableCreate implements ApplicationListener<ContextRefreshedEvent> {

    public static boolean isStart = false;
    private boolean isProc = false;
    private SQLParameter param = new SQLParameter();

    @Autowired
    private CreateService createService;

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private ScheduledAnnotationBeanPostProcessor posts;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        param.setMsg_type(appContext.getEnvironment().getProperty("dhnclient.msg_table"));
        param.setLog_table(appContext.getEnvironment().getProperty("dhnclient.log_table"));
        param.setDatabase(appContext.getEnvironment().getProperty("dhnclient.database"));
        param.setKakao_use(appContext.getEnvironment().getProperty("dhnclient.kakao_use"));
        param.setLog_back(appContext.getEnvironment().getProperty("dhnclient.log_back","Y"));


        if (param.getLog_back() != null && param.getLog_back().equalsIgnoreCase("Y")) {
            isStart = true;
            log.info("LOG테이블 자동생성 초기화 완료");
        } else {
            posts.postProcessBeforeDestruction(this, null);
        }
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void createTable() {
        if(isStart && !isProc){
            log.info("로그 테이블 로그테이블 재확인 및 생성");
            isProc = true;
            try{
                createService.logTableCheck(param);
            }catch (Exception e){
                log.error("log 테이블 생성 오류 : "+e.getMessage());
            }
            isProc = false;
        }
    }

}
