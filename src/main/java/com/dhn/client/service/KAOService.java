package com.dhn.client.service;

import com.dhn.client.bean.KAORequestBean;
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
public class KAOService {

    @Autowired
    private WebClient webClient;

    @Autowired
    private KAORequestService kaoRequestService;

    public void KAOSendApiProcess(SQLParameter param){
        try{
            List<KAORequestBean> _list = kaoRequestService.selectKAOSendData(param);

            webClient.post()
                    .uri("req")
                    .header("userid",param.getUserid())
                    .bodyValue(_list)
//                    .retrieve()
//                    .bodyToMono(SendResponse.class)
                    .exchangeToMono(response -> {
                        HttpStatus status = (HttpStatus) response.statusCode();
                        return response.bodyToMono(SendResponse.class)
                                .doOnNext(res -> log.info("{} HTTP 상태: {}",param.getMsg_type() , status));
                    })
                    .doOnNext(res ->{
                        try {
                            if ("00".equalsIgnoreCase(res.getCode())) {
                                kaoRequestService.updateKAOSendComplete(param);
                                log.info("{} 메세지 전송 완료 ({}건) : {}",param.getMsg_type(),_list.size(), res);
                            } else {
                                kaoRequestService.updateKAOSendInit(param);
                                log.info("{} 메세지 전송 오류 : {}",param.getMsg_type(), res);
                            }
                        } catch (Exception ex) {
                            log.error("{} 메시지 발송 완료 처리 중 오류: {}",param.getMsg_type(), ex);
                        }
                    })
                    .doOnError(e->{
                        try{
                            log.error("{} 메시지 발송 중 오류 발생 : {}",param.getMsg_type(), e);
                            kaoRequestService.updateKAOSendInit(param);
                        }catch (Exception ex){
                            log.error("{} 메시지 발송 재처리 중 오류 발생 : {}",param.getMsg_type(), ex);
                        }
                    })
                    .block();
        }catch (Exception e){
            log.error("{} 메세지 전송 오류 : {}",param.getMsg_type() ,e.toString());
        }
    }
}
