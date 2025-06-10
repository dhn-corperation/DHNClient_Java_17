package com.dhn.client.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SQLParameter {
	private String msg_table;
	private String log_table;
	private String sequence;
	private String database;
	private String kakao_use;
	private String msg_use;
	private String group_no;
	private String msg_type;
	private String sms_kind;
	private String log_mv_flag;
	private String bktable;
	private String dist_proc_option;
	private String dist_value;
	private String att_file_path;
	private String file1;
	private String file2;
	private String file3;
	private String ft_image_url;
	private String ft_image_code;
	private String mms_key;
	private String msg_image_code;
	private String msgid;
	private String img_err_msg;
	private String time;
	private String log_back;

}
