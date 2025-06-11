package com.dhn.client.dao;

import com.dhn.client.bean.ImageBean;
import com.dhn.client.bean.Msg_Log;
import com.dhn.client.bean.RequestBean;
import com.dhn.client.bean.SQLParameter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class MSGRequestDAOimpl implements MSGRequestDAO{

    @Autowired
    private SqlSession sqlSession;

    @Override
    public int selectSMSReqeustCount(SQLParameter param) throws Exception {
        return sqlSession.selectOne("com.dhn.client.msg.mapper.SendRequest.req_sms_count",param);
    }

    @Override
    public void updateSMSGroupNo(SQLParameter param) throws Exception {
        sqlSession.update("com.dhn.client.msg.mapper.SendRequest.req_sms_group_update",param);
    }

    @Override
    public void updateSMSSendComplete(SQLParameter param) throws Exception {
        sqlSession.update("com.dhn.client.msg.mapper.SendRequest.req_sent_complete",param);
    }

    @Override
    public void updateSMSSendInit(SQLParameter param) throws Exception {
        sqlSession.update("com.dhn.client.msg.mapper.SendRequest.req_sent_init",param);
    }

    @Override
    public void msgResultInsert(Msg_Log ml) throws Exception {
        sqlSession.update("com.dhn.client.msg.mapper.SendRequest.msgResultUpdate", ml);
        sqlSession.update("com.dhn.client.msg.mapper.SendRequest.msgLogInsert", ml);
        sqlSession.update("com.dhn.client.msg.mapper.SendRequest.msgResultDelete", ml);
    }

    @Override
    public int log_move_count(SQLParameter param) throws Exception {
        return sqlSession.selectOne("com.dhn.client.msg.mapper.SendRequest.msg_log_move_count", param);
    }

    @Override
    public void update_log_move_groupNo(SQLParameter param) throws Exception {
        sqlSession.update("com.dhn.client.msg.mapper.SendRequest.update_log_move_groupNo", param);
    }

    @Override
    public void log_move(SQLParameter param) throws Exception {
        sqlSession.update("com.dhn.client.msg.mapper.SendRequest.log_move_insert", param);
        sqlSession.update("com.dhn.client.msg.mapper.SendRequest.log_move_delete", param);
    }

    @Override
    public int selectLMSReqeustCount(SQLParameter param) throws Exception {
        return sqlSession.selectOne("com.dhn.client.msg.mapper.SendRequest.req_lms_count",param);
    }

    @Override
    public void updateLMSGroupNo(SQLParameter param) throws Exception {
        sqlSession.update("com.dhn.client.msg.mapper.SendRequest.req_lms_group_update",param);
    }

    @Override
    public int selectMMSReqeustCount(SQLParameter param) throws Exception {
        return sqlSession.selectOne("com.dhn.client.msg.mapper.SendRequest.req_mms_count",param);
    }

    @Override
    public void updateMMSGroupNo(SQLParameter param) throws Exception {
        sqlSession.update("com.dhn.client.msg.mapper.SendRequest.req_mms_group_update",param);
    }

    @Override
    public int selectMMSImageCount(SQLParameter param) throws Exception {
        return sqlSession.selectOne("com.dhn.client.msg.mapper.SendRequest.req_mms_image_count",param);
    }

    @Override
    public List<ImageBean> selectMMSImage(SQLParameter param) throws Exception {
        return sqlSession.selectList("com.dhn.client.msg.mapper.SendRequest.req_mms_image", param);
    }

    @Override
    public void updateMMSImageGroup(SQLParameter param) throws Exception {
        sqlSession.update("com.dhn.client.msg.mapper.SendRequest.req_mms_key_update", param);
    }

    @Override
    public void updateMMSImageFail(SQLParameter param) throws Exception {
        sqlSession.update("com.dhn.client.msg.mapper.SendRequest.mms_image_fail_update",param);
        sqlSession.update("com.dhn.client.msg.mapper.SendRequest.mms_image_fail_log_Insert", param);
        sqlSession.update("com.dhn.client.msg.mapper.SendRequest.mms_image_fail_delete", param);
    }

    @Override
    public List<RequestBean> selectMSGSendData(SQLParameter param) throws Exception {
        return sqlSession.selectList("com.dhn.client.msg.mapper.SendRequest.msg_send_data_list", param);
    }
}
