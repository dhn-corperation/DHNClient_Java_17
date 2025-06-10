package com.dhn.client.controller;

import com.dhn.client.bean.SQLParameter;
import com.dhn.client.service.CreateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Order(1)
public class CreateTable implements ApplicationListener<ContextRefreshedEvent> {

    private SQLParameter param = new SQLParameter();

    @Autowired
    private CreateService createService;

    @Autowired
    private ApplicationContext appContext;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        param.setMsg_table(appContext.getEnvironment().getProperty("dhnclient.msg_table"));
        param.setLog_table(appContext.getEnvironment().getProperty("dhnclient.log_table"));
        param.setSequence(appContext.getEnvironment().getProperty("dhnclient.msg_seq"));
        param.setDatabase(appContext.getEnvironment().getProperty("dhnclient.database"));
        param.setLog_back(appContext.getEnvironment().getProperty("dhnclient.log_back","Y"));

        try{
            createService.tableCheck(param);
            log.info("DHN 발송 테이블 체크 및 생성 완료");
        }catch (Exception e){
            log.error(param.getMsg_table() + " 테이블 생성 오류 : " + e.getMessage());
        }

        try{
            createService.logTableCheck(param);
            log.info("DHN 로그 테이블 체크 및 생성 완료");
        }catch (Exception e){
            log.error(param.getLog_table() + " 테이블 생성 오류 : " + e.getMessage());
        }
    }
}
