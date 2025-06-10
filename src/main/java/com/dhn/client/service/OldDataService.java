package com.dhn.client.service;

import com.dhn.client.bean.SQLParameter;

public interface OldDataService {

    int old_data_count(SQLParameter param) throws Exception;

    void old_data_group_update(SQLParameter param) throws Exception;

    void old_data_result(SQLParameter param) throws Exception;
}
