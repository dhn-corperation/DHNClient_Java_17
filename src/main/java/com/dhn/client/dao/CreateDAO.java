package com.dhn.client.dao;

import com.dhn.client.bean.SQLParameter;

public interface CreateDAO {
    int tableCheck(SQLParameter param) throws Exception;

    void tableCreate(SQLParameter param) throws Exception;

    void logTableCheck(SQLParameter param) throws Exception;
}
