package com.dhn.client.dao;

import com.dhn.client.bean.SQLParameter;

public interface OldDataDAO {

    int old_data_count(SQLParameter param) throws Exception;

    void old_data_group_update(SQLParameter param) throws Exception;

    void old_data_result(SQLParameter param) throws Exception;
}
