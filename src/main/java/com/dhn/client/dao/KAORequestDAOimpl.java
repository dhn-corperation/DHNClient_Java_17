package com.dhn.client.dao;

import com.dhn.client.bean.ImageBean;
import com.dhn.client.bean.KAORequestBean;
import com.dhn.client.bean.Msg_Log;
import com.dhn.client.bean.SQLParameter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class KAORequestDAOimpl implements KAORequestDAO{

    @Autowired
    private SqlSession sqlSession;

    @Override
    public int selectKAORequestCount(SQLParameter param) throws Exception {
        int cnt = 0;
        cnt = sqlSession.selectOne("com.dhn.client.kakao.mapper.SendRequest.req_kao_count",param);
        return cnt;
    }

    @Override
    public void updateKAOGroupNo(SQLParameter param) throws Exception {
        sqlSession.update("com.dhn.client.kakao.mapper.SendRequest.req_kao_group_update",param);
    }

    @Override
    public List<KAORequestBean> selectKAORequests(SQLParameter param) throws Exception {
        return sqlSession.selectList("com.dhn.client.kakao.mapper.SendRequest.req_kao_select", param);
    }

    @Override
    public void updateKAOSendComplete(SQLParameter param) throws Exception {
        sqlSession.update("com.dhn.client.kakao.mapper.SendRequest.req_sent_complete", param);
    }

    @Override
    public void updateKAOSendInit(SQLParameter param) throws Exception {
        sqlSession.update("com.dhn.client.kakao.mapper.SendRequest.req_sent_init", param);
    }

    @Override
    public void kaoResultInsert(Msg_Log ml) throws Exception {
        sqlSession.update("com.dhn.client.kakao.mapper.SendRequest.kaoResultUpdate", ml);
        sqlSession.update("com.dhn.client.kakao.mapper.SendRequest.kaoLogInsert", ml);
        sqlSession.update("com.dhn.client.kakao.mapper.SendRequest.kaoResultDelete", ml);
    }

    @Override
    public int log_move_count(SQLParameter param) throws Exception {
        return sqlSession.selectOne("com.dhn.client.kakao.mapper.SendRequest.kao_log_move_count", param);
    }

    @Override
    public void update_log_move_groupNo(SQLParameter param) throws Exception {
        sqlSession.update("com.dhn.client.kakao.mapper.SendRequest.update_log_move_groupNo", param);
    }

    @Override
    public void log_move(SQLParameter param) throws Exception {
        sqlSession.update("com.dhn.client.kakao.mapper.SendRequest.log_move_insert", param);
        sqlSession.update("com.dhn.client.kakao.mapper.SendRequest.log_move_delete", param);
    }

    @Override
    public int selectFTRequestCount(SQLParameter param) throws Exception {
        int cnt = 0;
        cnt = sqlSession.selectOne("com.dhn.client.kakao.mapper.SendRequest.req_ft_count",param);
        return cnt;
    }

    @Override
    public void updateFTGroupNo(SQLParameter param) throws Exception {
        sqlSession.update("com.dhn.client.kakao.mapper.SendRequest.req_ft_group_update",param);
    }

    @Override
    public List<KAORequestBean> selectFTRequests(SQLParameter param) throws Exception {
        return sqlSession.selectList("com.dhn.client.kakao.mapper.SendRequest.req_ft_select", param);
    }

    @Override
    public int selectFtImageCount(SQLParameter param) throws Exception {
        return sqlSession.selectOne("com.dhn.client.kakao.mapper.SendRequest.ft_image_count",param);
    }

    @Override
    public List<ImageBean> selectFtImage(SQLParameter param) throws Exception {
        return sqlSession.selectList("com.dhn.client.kakao.mapper.SendRequest.ft_image_list", param);
    }

    @Override
    public void updateFTImageUrl(SQLParameter param) throws Exception {
        sqlSession.update("com.dhn.client.kakao.mapper.SendRequest.ft_image_url_update",param);
    }

    @Override
    public void updateFTImageFail(SQLParameter param) throws Exception {
        sqlSession.update("com.dhn.client.kakao.mapper.SendRequest.ft_image_fail_update",param);
        sqlSession.update("com.dhn.client.kakao.mapper.SendRequest.ft_image_fail_log_Insert", param);
        sqlSession.update("com.dhn.client.kakao.mapper.SendRequest.ft_image_fail_delete", param);
    }

}
