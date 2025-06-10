package com.dhn.client.service;

import com.dhn.client.controller.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProgramStatus implements CommandLineRunner, ApplicationListener<ContextClosedEvent>, DisposableBean {
    @Override
    public void destroy() throws Exception {
        log.info("프로그램이 정상적으로 종료 되었습니다.");
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("프로그램이 정상적으로 실행 되었습니다.");
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        KAOSendRequest.isStart = false;
        KAOISendRequest.isStart = false;
        FTSendRequest.isStart = false;
        SMSSendRequest.isStart = false;
        LMSSendRequest.isStart = false;
        MMSSendRequest.isStart = false;
        KAOLogMove.isStart = false;
        MSGLogMove.isStart = false;
        ResultReq.isStart = false;
        ResultOldData.isStart = false;
        LogTableCreate.isStart = false;

        log.info("프로그램 종료 처리 중.....");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
