package com.dhn.client.service;

import com.dhn.client.bean.SQLParameter;
import com.dhn.client.dao.CreateDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CreateServiceimpl implements CreateService {

    @Autowired
    private CreateDAO createDAO;

    @Override
    public void tableCheck(SQLParameter param) throws Exception {
        int result = createDAO.tableCheck(param);

        try{
            if(result == 0){
                createDAO.tableCreate(param);
                log.info("{} 테이블 생성 완료",param.getMsg_table());
            }else{
                log.info("{} 테이블이 존재합니다.",param.getMsg_table());
            }
        }catch (Exception e){
            log.error("{} 테이블 생성 중 오류 발생: {}", param.getMsg_table(), e.getMessage());
            throw e;
        }
        createDAO.tableCheck(param);
    }

    @Override
    public void logTableCheck(SQLParameter param) throws Exception {
        createDAO.logTableCheck(param);
    }
}
