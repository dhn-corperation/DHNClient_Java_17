package com.dhn.client.service;

import com.dhn.client.bean.Msg_Log;
import com.dhn.client.bean.SQLParameter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ResultService {

    @Autowired
    private KAORequestService kaoRequestService;

    @Autowired
    private MSGRequestService msgRequestService;

    public void ResultProcess(JSONArray json, SQLParameter param){
        log.info("결과처리 시작 {}건", json.length());
        for(int i=0; i<json.length(); i++) {
            JSONObject ent = json.getJSONObject(i);

            Msg_Log kao_ml = new Msg_Log();
            Msg_Log msg_ml = new Msg_Log();

            String result_message = ent.getString("message").isEmpty()?"":ent.getString("message");

            if(ent.getString("message_type").equalsIgnoreCase("AT") || ent.getString("message_type").equalsIgnoreCase("AI") || ent.getString("message_type").equalsIgnoreCase("TT") || ent.getString("message_type").equalsIgnoreCase("FI") || ent.getString("message_type").equalsIgnoreCase("FT") || ent.getString("message_type").equalsIgnoreCase("FW")){
                // 알림톡
                kao_ml.setMsgid(ent.getString("msgid"));
                kao_ml.setMsg_table(param.getMsg_table());
                kao_ml.setLog_table(param.getLog_table());
                kao_ml.setDatabase(param.getDatabase());

                kao_ml.setResult_dt(ent.getString("res_dt"));
                kao_ml.setS_code(ent.getString("s_code"));
                kao_ml.setResult_message(result_message.equalsIgnoreCase("")?"":result_message);

                if(ent.getString("s_code").equals("0000")){
                    kao_ml.setStatus("3");
                }else{
                    kao_ml.setStatus("4");
                }

            }else if(ent.getString("message_type").equalsIgnoreCase("PH") && ent.has("s_code") && !ent.isNull("s_code") && ent.getString("s_code").length() > 1){
                // 알림톡 실패 문자
                kao_ml.setMsgid(ent.getString("msgid"));
                kao_ml.setMsg_table(param.getMsg_table());
                kao_ml.setLog_table(param.getLog_table());
                kao_ml.setDatabase(param.getDatabase());

                kao_ml.setS_code(ent.getString("s_code"));
                kao_ml.setCode(ent.getString("code"));

                if(ent.getString("remark1").equalsIgnoreCase("LGT") || ent.getString("remark1").equals("019")){
                    kao_ml.setTelecom("LGT");
                }else if(ent.getString("remark1").equalsIgnoreCase("SKT") || ent.getString("remark1").equals("011")){
                    kao_ml.setTelecom("SKT");
                }else if(ent.getString("remark1").equalsIgnoreCase("KTF") || ent.getString("remark1").equalsIgnoreCase("KT") || ent.getString("remark1").equals("016")){
                    kao_ml.setTelecom("KTF");
                }else{
                    kao_ml.setTelecom("ETC");
                }
                kao_ml.setResult_dt(ent.getString("remark2"));
                kao_ml.setResult_message(result_message.equalsIgnoreCase("")?"":result_message);

                if(ent.getString("code").equals("0000")){
                    kao_ml.setStatus("3");
                }else{
                    kao_ml.setStatus("4");
                }
                kao_ml.setReal_send_type(ent.getString("sms_kind"));

            }else{
                // 문자
                msg_ml.setMsgid(ent.getString("msgid"));
                msg_ml.setMsg_table(param.getMsg_table());
                msg_ml.setLog_table(param.getLog_table());
                msg_ml.setDatabase(param.getDatabase());

                msg_ml.setCode(ent.getString("code"));
                msg_ml.setReal_send_type(ent.getString("sms_kind"));

                if(ent.getString("remark1").equalsIgnoreCase("LGT") || ent.getString("remark1").equals("019")){
                    msg_ml.setTelecom("LGT");
                }else if(ent.getString("remark1").equalsIgnoreCase("SKT") || ent.getString("remark1").equals("011")){
                    msg_ml.setTelecom("SKT");
                }else if(ent.getString("remark1").equalsIgnoreCase("KTF") || ent.getString("remark1").equalsIgnoreCase("KT") || ent.getString("remark1").equals("016")){
                    msg_ml.setTelecom("KTF");
                }else{
                    msg_ml.setTelecom("ETC");
                }

                msg_ml.setResult_dt(ent.getString("remark2"));
                msg_ml.setResult_message(result_message.equalsIgnoreCase("")?"":result_message);

                if(ent.getString("code").equals("0000")){
                    msg_ml.setStatus("3");
                }else{
                    msg_ml.setStatus("4");
                }
            }

            try{
                if (msg_ml.getMsg_table() != null && msg_ml.getLog_table() != null) {
                    msgRequestService.msgResultInsert(msg_ml);
                }else if (kao_ml.getMsg_table() != null && kao_ml.getLog_table() != null) {
                    kaoRequestService.kaoResultInsert(kao_ml);
                }
            }catch (Exception e){
                log.error("결과 처리 오류 : "+ e.getMessage());
            }

        }
        log.info("결과 처리 완료 {}건" ,json.length());
    }
}
