package com.dhn.client.dao;

import com.dhn.client.bean.ImageBean;
import com.dhn.client.bean.Msg_Log;
import com.dhn.client.bean.RequestBean;
import com.dhn.client.bean.SQLParameter;

import java.util.List;

public interface MSGRequestDAO {

    public int selectSMSReqeustCount(SQLParameter param) throws Exception;

    public void updateSMSGroupNo(SQLParameter param) throws Exception;

    public List<RequestBean> selectSMSRequests(SQLParameter param) throws Exception;

    public void updateSMSSendComplete(SQLParameter param) throws Exception;

    public void updateSMSSendInit(SQLParameter param) throws Exception;

    public void msgResultInsert(Msg_Log ml) throws Exception;

    public int log_move_count(SQLParameter param) throws Exception;

    public void update_log_move_groupNo(SQLParameter param) throws Exception;

    public void log_move(SQLParameter param) throws Exception;

    public int selectLMSReqeustCount(SQLParameter param) throws Exception;

    public void updateLMSGroupNo(SQLParameter param) throws Exception;

    public List<RequestBean> selectLMSRequests(SQLParameter param) throws Exception;

    public int selectMMSReqeustCount(SQLParameter param) throws Exception;

    public void updateMMSGroupNo(SQLParameter param) throws Exception;

    public List<RequestBean> selectMMSRequests(SQLParameter param) throws Exception;

    public int selectMMSImageCount(SQLParameter param) throws Exception;

    public List<ImageBean> selectMMSImage(SQLParameter param) throws Exception;

    public void updateMMSImageGroup(SQLParameter param) throws Exception;

    public void updateMMSImageFail(SQLParameter param) throws Exception;
}
