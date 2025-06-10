package com.dhn.client.controller;

import com.dhn.client.bean.SQLParameter;
import com.dhn.client.service.OldDataService;
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
public class ResultOldData implements ApplicationListener<ContextRefreshedEvent> {

    public static boolean isStart = false;
    private boolean isProc = false;
    private SQLParameter param = new SQLParameter();
    private String preGroupNo = "";
    private String log_table;

    @Autowired
    private OldDataService oldDataService;

    @Autowired
    private ApplicationContext appContext;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        param.setMsg_table(appContext.getEnvironment().getProperty("dhnclient.msg_table"));
        param.setDatabase(appContext.getEnvironment().getProperty("dhnclient.database"));
        param.setLog_back(appContext.getEnvironment().getProperty("dhnclient.log_back","Y"));
        log_table = appContext.getEnvironment().getProperty("dhnclient.log_table");

        param.setTime("4");

        isStart = true;
    }

    @Scheduled(fixedDelay = 60000)
    private void LogRemove() {
        if(isStart && !isProc) {
            isProc = true;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
            LocalDateTime now = LocalDateTime.now();
            String group_no = "OD" + now.format(formatter);

            if(!group_no.equals(preGroupNo)){
                try {
                    int cnt = oldDataService.old_data_count(param);
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

                        oldDataService.old_data_group_update(param);
                        oldDataService.old_data_result(param);

                        log.info("과거데이터 결과처리 [ {} ]",param.getGroup_no());
                    }
                }catch (Exception e){
                    log.error("과거데이터 결과처리 오류 발생 : " + e.toString());
                }

                preGroupNo = group_no;
            }
            isProc = false;
        }
    }
}
