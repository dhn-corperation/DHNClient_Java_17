package com.dhn.client.service;

import com.dhn.client.bean.SQLParameter;
import com.dhn.client.dao.OldDataDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OldDataServiceImpl implements OldDataService{

    @Autowired
    private OldDataDAO oldDataDAO;

    @Override
    public int old_data_count(SQLParameter param) throws Exception {
        return oldDataDAO.old_data_count(param);
    }

    @Override
    public void old_data_group_update(SQLParameter param) throws Exception {
        oldDataDAO.old_data_group_update(param);
    }

    @Override
    public void old_data_result(SQLParameter param) throws Exception {
        oldDataDAO.old_data_result(param);
    }
}
