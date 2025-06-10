package com.dhn.client.service;

import com.dhn.client.bean.ImageBean;
import com.dhn.client.bean.KAORequestBean;
import com.dhn.client.bean.Msg_Log;
import com.dhn.client.bean.SQLParameter;

import java.util.List;

public interface KAORequestService {

    public int selectKAORequestCount(SQLParameter param) throws Exception;

    public void updateKAOGroupNo(SQLParameter param) throws Exception;

    public List<KAORequestBean> selectKAORequests(SQLParameter param) throws Exception;

    public void updateKAOSendComplete(SQLParameter param) throws Exception;

    public void updateKAOSendInit(SQLParameter param) throws Exception;


    public void kaoResultInsert(Msg_Log ml) throws Exception;

    public int log_move_count(SQLParameter param) throws Exception;

    public void update_log_move_groupNo(SQLParameter param) throws Exception;

    public void log_move(SQLParameter param) throws Exception;

    public int selectFTRequestCount(SQLParameter param) throws Exception;

    public void updateFTGroupNo(SQLParameter param) throws Exception;

    public List<KAORequestBean> selectFTRequests(SQLParameter param) throws Exception;

    public int selectFtImageCount(SQLParameter param) throws Exception;

    public List<ImageBean> selectFtImage(SQLParameter param) throws Exception;

    public void updateFTImageUrl(SQLParameter param) throws Exception;

    public void updateFTImageFail(SQLParameter param) throws Exception;
}
