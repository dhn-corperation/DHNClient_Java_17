package com.dhn.client.bean;

import lombok.Data;

@Data
public class Msg_Log {
	private String msg_table;
	private String log_table;
	private String msgid;
	private String code;
	private String s_code;
	private String result_message;
	private String result_dt;
	private String telecom;
	private String status;
	private String real_send_type;
	private String database;
}
