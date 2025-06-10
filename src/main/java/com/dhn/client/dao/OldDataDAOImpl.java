package com.dhn.client.dao;

import com.dhn.client.bean.SQLParameter;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OldDataDAOImpl implements OldDataDAO{

    @Autowired
    private SqlSession sqlSession;

    @Override
    public int old_data_count(SQLParameter param) throws Exception {
        int cnt = 0;
        cnt = sqlSession.selectOne("com.dhn.client.olddata.mapper.SendRequest.old_data_count",param);
        return cnt;
    }

    @Override
    public void old_data_group_update(SQLParameter param) throws Exception {
        sqlSession.update("com.dhn.client.olddata.mapper.SendRequest.old_data_group_update",param);

    }

    @Override
    public void old_data_result(SQLParameter param) throws Exception {
        sqlSession.update("com.dhn.client.olddata.mapper.SendRequest.old_data_update",param);
        sqlSession.insert("com.dhn.client.olddata.mapper.SendRequest.old_data_insert",param);
        sqlSession.delete("com.dhn.client.olddata.mapper.SendRequest.old_data_delete",param);
    }
}
