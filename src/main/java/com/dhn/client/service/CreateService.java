package com.dhn.client.service;


import com.dhn.client.bean.SQLParameter;

public interface CreateService {

    void tableCheck(SQLParameter param) throws Exception;

    void logTableCheck(SQLParameter param) throws Exception;
}
