package com.dhn.client.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendResponse {
    private String code;
    private String message;
    private Integer atcnt;
    private Integer ftcnt;
    private Integer msgcnt;
    private Integer testcnt;
    private Integer duplcnt;
//    private List<String> duplMsgId;
}
