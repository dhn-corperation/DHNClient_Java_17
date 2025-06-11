package com.dhn.client.service;

import com.dhn.client.bean.RequestBean;
import com.dhn.client.bean.SQLParameter;
import com.dhn.client.bean.SendResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@Slf4j
public class MSGService {

    @Autowired
    private WebClient webClient;

    @Autowired
    private MSGRequestService msgRequestService;

    public void MSGSendApiProcess(SQLParameter param){
        try{
            List<RequestBean> _list = msgRequestService.selectMSGSendData(param);

            webClient.post()
                    .uri("req")
                    .header("userid",param.getUserid())
                    .bodyValue(_list)
//                    .retrieve()
//                    .bodyToMono(SendResponse.class)
                    .exchangeToMono(response -> {
                        HttpStatus status = (HttpStatus) response.statusCode();
                        return response.bodyToMono(SendResponse.class)
                                .doOnNext(res -> log.info("{}({}) HTTP 상태: {}",param.getMsg_type(),param.getSms_kind() , status));
                    })
                    .doOnNext(res ->{
                        try {
                            if ("00".equalsIgnoreCase(res.getCode())) {
                                msgRequestService.updateSMSSendComplete(param);
                                log.info("{}({}) 메세지 전송 완료 ({}건) : {}",param.getMsg_type(),param.getSms_kind(),_list.size(), res);
                            } else {
                                msgRequestService.updateSMSSendInit(param);
                                log.info("{}({}) 메세지 전송 오류 : {}",param.getMsg_type(),param.getSms_kind(), res);
                            }
                        } catch (Exception ex) {
                            log.error("{}({}) 메시지 발송 완료 처리 중 오류: {}",param.getMsg_type(), param.getSms_kind(), ex);
                        }
                    })
                    .doOnError(e->{
                        try{
                            log.error("{}({}) 메시지 발송 중 오류 발생 : {}",param.getMsg_type(), param.getSms_kind(), e);
                            msgRequestService.updateSMSSendInit(param);
                        }catch (Exception ex){
                            log.error("{}({}) 메시지 발송 재처리 중 오류 발생 : {}",param.getMsg_type(), param.getSms_kind(), ex);
                        }
                    })
                    .block();
        }catch (Exception e){
            log.error("{}({}) 메세지 전송 오류 : {}",param.getMsg_type(), param.getSms_kind(), e.toString());
        }

    }
}
