package com.dhn.client.controller;

import com.dhn.client.bean.SQLParameter;
import com.dhn.client.service.KAORequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class KAOLogMove implements ApplicationListener<ContextRefreshedEvent> {

    public static boolean isStart = false;
    private boolean isProc = false;
    private SQLParameter param = new SQLParameter();
    private String preGroupNo = "";
    private String log_table;

    @Autowired
    private KAORequestService kaoRequestService;

    @Autowired
    private ApplicationContext appContext;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        param.setMsg_table(appContext.getEnvironment().getProperty("dhnclient.msg_table"));
        param.setDatabase(appContext.getEnvironment().getProperty("dhnclient.database"));
        param.setLog_back(appContext.getEnvironment().getProperty("dhnclient.log_back"));
        log_table = appContext.getEnvironment().getProperty("dhnclient.log_table","Y");
        if(appContext.getEnvironment().getProperty("dhnclient.kakao_use").equalsIgnoreCase("Y")){
            isStart = true;
        }
    }

    @Scheduled(fixedDelay = 2000)
    private void LogRemove() {
        if(isStart && !isProc) {
            isProc = true;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
            LocalDateTime now = LocalDateTime.now();
            String group_no = "KV" + now.format(formatter);

            if(!group_no.equals(preGroupNo)){
                try {
                    int cnt = kaoRequestService.log_move_count(param);
                    if(cnt > 0){
                        if(param.getLog_back().equalsIgnoreCase("Y")){
                            LocalDate logdate = LocalDate.now();
                            DateTimeFormatter log_formatter = DateTimeFormatter.ofPattern("yyyyMM");
                            String currentMonth = logdate.format(log_formatter);
                            param.setLog_table(log_table+"_"+currentMonth);
                        }else{
                            param.setLog_table(log_table);
                        }

                        param.setGroup_no(group_no);

                        kaoRequestService.update_log_move_groupNo(param);

                        kaoRequestService.log_move(param);

                        log.info("Log 테이블 이동 그룹 : {}",param.getGroup_no());
                    }else{
                        Thread.sleep(5000);
                    }
                }catch (Exception e){
                    log.error("Log 테이블로 이동중 오류 발생 : " + e.toString());
                }

                preGroupNo = group_no;
            }
            isProc = false;
        }
    }

}
