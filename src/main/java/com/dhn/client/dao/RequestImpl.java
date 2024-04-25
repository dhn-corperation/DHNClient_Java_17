package com.dhn.client.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dhn.client.bean.KAORequestBean;
import com.dhn.client.bean.MMSImageBean;
import com.dhn.client.bean.Msg_Log;
import com.dhn.client.bean.RequestBean;
import com.dhn.client.bean.SQLParameter;

@Repository
public class RequestImpl implements RequestDAO{

	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public int selectKAORequestCount(SQLParameter param) throws Exception {
		int cnt = 0;
		
		switch(param.getDbtype()) {
		case "db2":
			cnt = sqlSession.selectOne("com.dhn.client.kakao_db2.mapper.SendRequest.req_kao_count",param);
			break;
		case "oracle":
			cnt = sqlSession.selectOne("com.dhn.client.kakao_oracle.mapper.SendRequest.req_kao_count",param);
			break;
		case "mysql":
			cnt = sqlSession.selectOne("com.dhn.client.kakao_mysql.mapper.SendRequest.req_kao_count",param);
			break;
		case "mssql":
			cnt = sqlSession.selectOne("com.dhn.client.kakao_mssql.mapper.SendRequest.req_kao_count",param);
			break;
		case "postgresql":
			cnt = sqlSession.selectOne("com.dhn.client.kakao_postgresql.mapper.SendRequest.req_kao_count",param);
			break;
		default:
			cnt = 0;
		}
		
		return cnt;
	}

	@Override
	public void updateKAOGroupNo(SQLParameter param) throws Exception {

		switch(param.getDbtype()) {
		case "db2":
			sqlSession.update("com.dhn.client.kakao_db2.mapper.SendRequest.req_kao_group_update",param);
			break;
		case "oracle":
			sqlSession.update("com.dhn.client.kakao_oracle.mapper.SendRequest.req_kao_group_update",param);
			break;
		case "mysql":
			sqlSession.update("com.dhn.client.kakao_mysql.mapper.SendRequest.req_kao_group_update",param);
			break;
		case "mssql":
			sqlSession.update("com.dhn.client.kakao_mssql.mapper.SendRequest.req_kao_group_update",param);
			break;
		case "postgresql":
			sqlSession.update("com.dhn.client.kakao_postgresql.mapper.SendRequest.req_kao_group_update",param);
			break;
		}
	}

	@Override
	public List<KAORequestBean> selectKAORequests(SQLParameter param) throws Exception {
		
		switch(param.getDbtype()) {
		case "db2":
			return sqlSession.selectList("com.dhn.client.kakao_db2.mapper.SendRequest.req_kao_select", param);
		case "oracle":
			return sqlSession.selectList("com.dhn.client.kakao_oracle.mapper.SendRequest.req_kao_select", param);
		case "mysql":
			return sqlSession.selectList("com.dhn.client.kakao_mysql.mapper.SendRequest.req_kao_select", param);
		case "mssql":
			return sqlSession.selectList("com.dhn.client.kakao_mssql.mapper.SendRequest.req_kao_select", param);
		case "postgresql":
			return sqlSession.selectList("com.dhn.client.kakao_postgresql.mapper.SendRequest.req_kao_select", param);
		default:
			return null;
		}
	}

	@Override
	public void updateKAOSendComplete(SQLParameter param) throws Exception {
		
		switch(param.getDbtype()) {
		case "db2":
			sqlSession.update("com.dhn.client.kakao_db2.mapper.SendRequest.req_sent_complete", param); 
			break;
		case "oracle":
			sqlSession.update("com.dhn.client.kakao_oracle.mapper.SendRequest.req_sent_complete", param); 
			break;
		case "mysql":
			sqlSession.update("com.dhn.client.kakao_mysql.mapper.SendRequest.req_sent_complete", param); 
			break;
		case "mssql":
			sqlSession.update("com.dhn.client.kakao_mssql.mapper.SendRequest.req_sent_complete", param); 
			break;
		case "postgresql":
			sqlSession.update("com.dhn.client.kakao_postgresql.mapper.SendRequest.req_sent_complete", param); 
			break;
		}
	}

	@Override
	public void updateKAOSendInit(SQLParameter param) throws Exception {
		
		switch(param.getDbtype()) {
		case "db2":
			sqlSession.update("com.dhn.client.kakao_db2.mapper.SendRequest.req_sent_init", param); 
			break;
		case "oracle":
			sqlSession.update("com.dhn.client.kakao_oracle.mapper.SendRequest.req_sent_init", param); 
			break;
		case "mysql":
			sqlSession.update("com.dhn.client.kakao_mysql.mapper.SendRequest.req_sent_init", param);
			break;
		case "mssql":
			sqlSession.update("com.dhn.client.kakao_mssql.mapper.SendRequest.req_sent_init", param);
			break;
		case "postgresql":
			sqlSession.update("com.dhn.client.kakao_postgresql.mapper.SendRequest.req_sent_init", param);
			break;
		}
	}

	@Override
	public int selectSMSReqeustCount(SQLParameter param) throws Exception {
		int cnt = 0;
		
		switch(param.getDbtype()) {
		case "db2":
			cnt = sqlSession.selectOne("com.dhn.client.nkakao_db2.mapper.SendRequest.req_sms_count",param);
			break;
		case "oracle":
			cnt = sqlSession.selectOne("com.dhn.client.nkakao_oracle.mapper.SendRequest.req_sms_count",param);
			break;
		case "mysql":
			cnt = sqlSession.selectOne("com.dhn.client.nkakao_mysql.mapper.SendRequest.req_sms_count",param);
			break;
		case "mssql":
			cnt = sqlSession.selectOne("com.dhn.client.nkakao_mssql.mapper.SendRequest.req_sms_count",param);
			break;
		case "postgresql":
			cnt = sqlSession.selectOne("com.dhn.client.nkakao_postgresql.mapper.SendRequest.req_sms_count",param);
			break;
		default:
			cnt = 0;
		}
		
		return cnt;
	}

	@Override
	public void updateSMSGroupNo(SQLParameter param) throws Exception {
		
		switch(param.getDbtype()) {
		case "db2":
			sqlSession.update("com.dhn.client.nkakao_db2.mapper.SendRequest.req_sms_group_update",param);
			break;
		case "oracle":
			sqlSession.update("com.dhn.client.nkakao_oracle.mapper.SendRequest.req_sms_group_update",param);
			break;
		case "mysql":
			sqlSession.update("com.dhn.client.nkakao_mysql.mapper.SendRequest.req_sms_group_update",param);
			break;
		case "mssql":
			sqlSession.update("com.dhn.client.nkakao_mssql.mapper.SendRequest.req_sms_group_update",param);
			break;
		case "postgresql":
			sqlSession.update("com.dhn.client.nkakao_postgresql.mapper.SendRequest.req_sms_group_update",param);
			break;
		}
	}

	@Override
	public List<RequestBean> selectSMSRequests(SQLParameter param) throws Exception {
		
		switch(param.getDbtype()) {
		case "db2":
			return sqlSession.selectList("com.dhn.client.nkakao_db2.mapper.SendRequest.req_sms_select", param);
		case "oracle":
			return sqlSession.selectList("com.dhn.client.nkakao_oracle.mapper.SendRequest.req_sms_select", param);
		case "mysql":
			return sqlSession.selectList("com.dhn.client.nkakao_mysql.mapper.SendRequest.req_sms_select", param);
		case "mssql":
			return sqlSession.selectList("com.dhn.client.nkakao_mssql.mapper.SendRequest.req_sms_select", param);
		case "postgresql":
			return sqlSession.selectList("com.dhn.client.nkakao_postgresql.mapper.SendRequest.req_sms_select", param);
		default:
			return null;
		}
	}

	@Override
	public void updateSMSSendComplete(SQLParameter param) throws Exception {
		
		switch(param.getDbtype()) {
		case "db2":
			sqlSession.update("com.dhn.client.nkakao_db2.mapper.SendRequest.req_sent_complete",param);
			break;
		case "oracle":
			sqlSession.update("com.dhn.client.nkakao_oracle.mapper.SendRequest.req_sent_complete",param);
			break;
		case "mysql":
			sqlSession.update("com.dhn.client.nkakao_mysql.mapper.SendRequest.req_sent_complete",param);
			break;
		case "mssql":
			sqlSession.update("com.dhn.client.nkakao_mssql.mapper.SendRequest.req_sent_complete",param);
			break;
		case "postgresql":
			sqlSession.update("com.dhn.client.nkakao_postgresql.mapper.SendRequest.req_sent_complete",param);
			break;
		}
	}

	@Override
	public void updateSMSSendInit(SQLParameter param) throws Exception {
		
		switch(param.getDbtype()) {
		case "db2":
			sqlSession.update("com.dhn.client.nkakao_db2.mapper.SendRequest.req_sent_init", param); 
			break;
		case "oracle":
			sqlSession.update("com.dhn.client.nkakao_oracle.mapper.SendRequest.req_sent_init", param); 
			break;
		case "mysql":
			sqlSession.update("com.dhn.client.nkakao_mysql.mapper.SendRequest.req_sent_init", param); 
			break;
		case "mssql":
			sqlSession.update("com.dhn.client.nkakao_mssql.mapper.SendRequest.req_sent_init", param); 
			break;
		case "postgresql":
			sqlSession.update("com.dhn.client.nkakao_postgresql.mapper.SendRequest.req_sent_init", param); 
			break;
		}

	}

	@Override
	public int selectLMSReqeustCount(SQLParameter param) throws Exception {
		int cnt = 0;
		
		switch(param.getDbtype()) {
		case "db2":
			cnt = sqlSession.selectOne("com.dhn.client.nkakao_db2.mapper.SendRequest.req_lms_count", param);
			break;
		case "oracle":
			cnt = sqlSession.selectOne("com.dhn.client.nkakao_oracle.mapper.SendRequest.req_lms_count", param);
			break;
		case "mysql":
			cnt = sqlSession.selectOne("com.dhn.client.nkakao_mysql.mapper.SendRequest.req_lms_count", param);
			break;
		case "mssql":
			cnt = sqlSession.selectOne("com.dhn.client.nkakao_mssql.mapper.SendRequest.req_lms_count", param);
			break;
		case "postgresql":
			cnt = sqlSession.selectOne("com.dhn.client.nkakao_postgresql.mapper.SendRequest.req_lms_count", param);
			break;
		default:
			cnt = 0;
		}
		
		
		return cnt;
	}

	@Override
	public void updateLMSGroupNo(SQLParameter param) throws Exception {
		
		switch(param.getDbtype()) {
		case "db2":
			sqlSession.update("com.dhn.client.nkakao_db2.mapper.SendRequest.req_lms_group_update", param); 
			break;
		case "oracle":
			sqlSession.update("com.dhn.client.nkakao_oracle.mapper.SendRequest.req_lms_group_update", param); 
			break;
		case "mysql":
			sqlSession.update("com.dhn.client.nkakao_mysql.mapper.SendRequest.req_lms_group_update", param); 
			break;
		case "mssql":
			sqlSession.update("com.dhn.client.nkakao_mssql.mapper.SendRequest.req_lms_group_update", param); 
			break;
		case "postgresql":
			sqlSession.update("com.dhn.client.nkakao_postgresql.mapper.SendRequest.req_lms_group_update", param); 
			break;
		}
		
	}

	@Override
	public List<RequestBean> selectLMSRequests(SQLParameter param) throws Exception {
		
		switch(param.getDbtype()) {
		case "db2":
			return sqlSession.selectList("com.dhn.client.nkakao_db2.mapper.SendRequest.req_lms_select", param);
		case "oracle":
			return sqlSession.selectList("com.dhn.client.nkakao_oracle.mapper.SendRequest.req_lms_select", param);
		case "mysql":
			return sqlSession.selectList("com.dhn.client.nkakao_mysql.mapper.SendRequest.req_lms_select", param);
		case "mssql":
			return sqlSession.selectList("com.dhn.client.nkakao_mssql.mapper.SendRequest.req_lms_select", param);
		case "postgresql":
			return sqlSession.selectList("com.dhn.client.nkakao_postgresql.mapper.SendRequest.req_lms_select", param);
		default:
			return null;
		}
	}

	@Override
	public int selectMMSReqeustCount(SQLParameter param) throws Exception {
		int cnt ;
		
		switch(param.getDbtype()) {
		case "db2":
			cnt = sqlSession.selectOne("com.dhn.client.nkakao_db2.mapper.SendRequest.req_mms_count", param); 
			break;
		case "oracle":
			cnt = sqlSession.selectOne("com.dhn.client.nkakao_oracle.mapper.SendRequest.req_mms_count", param); 
			break;
		case "mysql":
			cnt = sqlSession.selectOne("com.dhn.client.nkakao_mysql.mapper.SendRequest.req_mms_count", param); 
			break;
		case "mssql":
			cnt = sqlSession.selectOne("com.dhn.client.nkakao_mssql.mapper.SendRequest.req_mms_count", param); 
			break;
		case "postgresql":
			cnt = sqlSession.selectOne("com.dhn.client.nkakao_postgresql.mapper.SendRequest.req_mms_count", param); 
			break;
		default:
			cnt = 0;
		}


		return cnt;
	}

	@Override
	public void updateMMSGroupNo(SQLParameter param) throws Exception {
		
		switch(param.getDbtype()) {
		case "db2":
			sqlSession.update("com.dhn.client.nkakao_db2.mapper.SendRequest.req_mms_group_update", param);
			break;
		case "oracle":
			sqlSession.update("com.dhn.client.nkakao_oracle.mapper.SendRequest.req_mms_group_update", param);
			break;
		case "mysql":
			sqlSession.update("com.dhn.client.nkakao_mysql.mapper.SendRequest.req_mms_group_update", param);
			break;
		case "mssql":
			sqlSession.update("com.dhn.client.nkakao_mssql.mapper.SendRequest.req_mms_group_update", param);
			break;
		case "postgresql":
			sqlSession.update("com.dhn.client.nkakao_postgresql.mapper.SendRequest.req_mms_group_update", param);
			break;
		}
	}

	@Override
	public List<RequestBean> selectMMSRequests(SQLParameter param) throws Exception {
		
		switch(param.getDbtype()) {
		case "db2":
			return sqlSession.selectList("com.dhn.client.nkakao_db2.mapper.SendRequest.req_mms_select", param);
		case "oracle":
			return sqlSession.selectList("com.dhn.client.nkakao_oracle.mapper.SendRequest.req_mms_select", param);
		case "mysql":
			return sqlSession.selectList("com.dhn.client.nkakao_mysql.mapper.SendRequest.req_mms_select", param);
		case "mssql":
			return sqlSession.selectList("com.dhn.client.nkakao_mssql.mapper.SendRequest.req_mms_select", param);
		case "postgresql":
			return sqlSession.selectList("com.dhn.client.nkakao_postgresql.mapper.SendRequest.req_mms_select", param);
		default:
			return null;
		}
	}

	@Override
	public List<MMSImageBean> selectMMSImage(SQLParameter param) throws Exception {
		
		switch(param.getDbtype()) {
		case "db2":
			return sqlSession.selectList("com.dhn.client.nkakao_db2.mapper.SendRequest.req_mms_image", param);
		case "oracle":
			return sqlSession.selectList("com.dhn.client.nkakao_oracle.mapper.SendRequest.req_mms_image", param);
		case "mysql":
			return sqlSession.selectList("com.dhn.client.nkakao_mysql.mapper.SendRequest.req_mms_image", param);
		case "mssql":
			return sqlSession.selectList("com.dhn.client.nkakao_mssql.mapper.SendRequest.req_mms_image", param);
		case "postgresql":
			return sqlSession.selectList("com.dhn.client.nkakao_postgresql.mapper.SendRequest.req_mms_image", param);
		default:
			return null;
		}
	}

	@Override
	public void updateMMSImageGroup(SQLParameter param) throws Exception {
		
		switch(param.getDbtype()) {
		case "db2":
			sqlSession.update("com.dhn.client.nkakao_db2.mapper.SendRequest.req_mms_key_update", param);
			break;
		case "oracle":
			sqlSession.update("com.dhn.client.nkakao_oracle.mapper.SendRequest.req_mms_key_update", param);
			break;
		case "mysql":
			sqlSession.update("com.dhn.client.nkakao_mysql.mapper.SendRequest.req_mms_key_update", param);
			break;
		case "mssql":
			sqlSession.update("com.dhn.client.nkakao_mssql.mapper.SendRequest.req_mms_key_update", param);
			break;
		case "postgresql":
			sqlSession.update("com.dhn.client.nkakao_postgresql.mapper.SendRequest.req_mms_key_update", param);
			break;
		}
	}

	@Override
	public void Insert_msg_log(Msg_Log _ml) throws Exception {
		
		switch(_ml.getDbtype()) {
		case "db2":
			if(_ml.getMsg_type().equals("AT") || _ml.getAgan_code().length()>1) {
				sqlSession.update("com.dhn.client.kakao_db2.mapper.SendRequest.result_log_insert1", _ml);
				sqlSession.update("com.dhn.client.kakao_db2.mapper.SendRequest.result_log_insert2", _ml);
				sqlSession.update("com.dhn.client.kakao_db2.mapper.SendRequest.result_log_insert3", _ml);
			}else {
				sqlSession.update("com.dhn.client.nkakao_db2.mapper.SendRequest.result_log_insert1", _ml);
				sqlSession.update("com.dhn.client.nkakao_db2.mapper.SendRequest.result_log_insert2", _ml);
				sqlSession.update("com.dhn.client.nkakao_db2.mapper.SendRequest.result_log_insert3", _ml);
			}
			break;
		case "oracle":
			if(_ml.getMsg_type().equals("AT") || _ml.getAgan_code().length()>1) {
				sqlSession.update("com.dhn.client.kakao_oracle.mapper.SendRequest.result_log_insert1", _ml);
				sqlSession.update("com.dhn.client.kakao_oracle.mapper.SendRequest.result_log_insert2", _ml);
				sqlSession.update("com.dhn.client.kakao_oracle.mapper.SendRequest.result_log_insert3", _ml);
			}else {
				sqlSession.update("com.dhn.client.nkakao_oracle.mapper.SendRequest.result_log_insert1", _ml);
				sqlSession.update("com.dhn.client.nkakao_oracle.mapper.SendRequest.result_log_insert2", _ml);
				sqlSession.update("com.dhn.client.nkakao_oracle.mapper.SendRequest.result_log_insert3", _ml);
			}
			break;
		case "mysql":
			if(_ml.getMsg_type().equals("AT") || _ml.getAgan_code().length()>1) {
				sqlSession.update("com.dhn.client.kakao_mysql.mapper.SendRequest.result_log_insert1", _ml);
				sqlSession.update("com.dhn.client.kakao_mysql.mapper.SendRequest.result_log_insert2", _ml);
				sqlSession.update("com.dhn.client.kakao_mysql.mapper.SendRequest.result_log_insert3", _ml);
			}else {
				sqlSession.update("com.dhn.client.nkakao_mysql.mapper.SendRequest.result_log_insert1", _ml);
				sqlSession.update("com.dhn.client.nkakao_mysql.mapper.SendRequest.result_log_insert2", _ml);
				sqlSession.update("com.dhn.client.nkakao_mysql.mapper.SendRequest.result_log_insert3", _ml);
			}
			break;
		case "mssql":
			if(_ml.getMsg_type().equals("AT") || _ml.getAgan_code().length()>1) {
				sqlSession.update("com.dhn.client.kakao_mssql.mapper.SendRequest.result_log_insert1", _ml);
				sqlSession.update("com.dhn.client.kakao_mssql.mapper.SendRequest.result_log_insert2", _ml);
				sqlSession.update("com.dhn.client.kakao_mssql.mapper.SendRequest.result_log_insert3", _ml);
			}else {
				sqlSession.update("com.dhn.client.nkakao_mssql.mapper.SendRequest.result_log_insert1", _ml);
				sqlSession.update("com.dhn.client.nkakao_mssql.mapper.SendRequest.result_log_insert2", _ml);
				sqlSession.update("com.dhn.client.nkakao_mssql.mapper.SendRequest.result_log_insert3", _ml);
			}
			break;
		case "postgresql":
			if(_ml.getMsg_type().equals("AT") || _ml.getAgan_code().length()>1) {
				sqlSession.update("com.dhn.client.kakao_postgresql.mapper.SendRequest.result_log_insert1", _ml);
				sqlSession.update("com.dhn.client.kakao_postgresql.mapper.SendRequest.result_log_insert2", _ml);
				sqlSession.update("com.dhn.client.kakao_postgresql.mapper.SendRequest.result_log_insert3", _ml);
			}else {
				sqlSession.update("com.dhn.client.nkakao_postgresql.mapper.SendRequest.result_log_insert1", _ml);
				sqlSession.update("com.dhn.client.nkakao_postgresql.mapper.SendRequest.result_log_insert2", _ml);
				sqlSession.update("com.dhn.client.nkakao_postgresql.mapper.SendRequest.result_log_insert3", _ml);
			}
			break;
		}
		
		
		/*
		 switch(ml.getDBType())
		{
			case "oracle":
				try {
					sqlSession.insert("com.dhn.client.mapper.SendRequest.result_log_insert1", ml);
					sqlSession.delete("com.dhn.client.mapper.SendRequest.result_log_insert2", ml);
					sqlSession.update("com.dhn.client.mapper.SendRequest.result_log_insert3", ml);
				} catch(Exception ex) {
					if(ex.getMessage().contains("ORA-00942"))
					{
						sqlSession.update("com.dhn.client.mapper.SendRequest.create_log_table", ml);
						sqlSession.insert("com.dhn.client.mapper.SendRequest.result_log_insert1", ml);
						sqlSession.delete("com.dhn.client.mapper.SendRequest.result_log_insert2", ml);
						sqlSession.update("com.dhn.client.mapper.SendRequest.result_log_insert3", ml);
					}
				}

				break;
			case "mysql":			
				try {
					sqlSession.insert("com.dhn.client.mapper.SendRequest.result_log_insert1", ml);
					sqlSession.delete("com.dhn.client.mapper.SendRequest.result_log_insert2", ml);
					sqlSession.update("com.dhn.client.mapper.SendRequest.result_log_insert3", ml);
				}catch(Exception ex) {
					//System.out.println(ex.getMessage());
					if(ex.getMessage().contains("doesn't exist"))
					{
						//System.out.println("Create Table : " + ml.getMsg_log());
						sqlSession.update("com.dhn.client.mapper.SendRequest.create_log_table", ml);
						sqlSession.insert("com.dhn.client.mapper.SendRequest.result_log_insert1", ml);
						sqlSession.delete("com.dhn.client.mapper.SendRequest.result_log_insert2", ml);
						sqlSession.update("com.dhn.client.mapper.SendRequest.result_log_insert3", ml);
					}
				}
				break;
		}
		 */

	}

}
