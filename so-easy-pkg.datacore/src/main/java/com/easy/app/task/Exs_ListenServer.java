package com.easy.app.task;

import java.util.HashMap;
import java.util.List;

import com.easy.app.interfaces.IGlobalService;
import com.easy.query.SQLExecUtils;
import com.easy.utility.SysUtility;
import com.easy.web.MainServlet;

public class Exs_ListenServer extends MainServlet implements IGlobalService{
	private static final long serialVersionUID = 1L;
	
	public Exs_ListenServer(String param) {
		super();
		SetCheckLogin(false);
	}
	
	public void DoCommand() throws Exception {
		String SQL = "select sysdate - 1/48 moni_date,MODIFY_TIME,moni_name,moni_ip_address,moni_send_address,moni_rev_address from exs_monitor_server where MODIFY_TIME < sysdate - 1/48 and is_enabled = 1";
		List lst = SQLExecUtils.query4List(SQL);
		for (int i = 0; i < lst.size(); i++) {
			HashMap map = (HashMap)lst.get(i);
			String modifyDate = (String)map.get("MODIFY_TIME");
			String moniName = (String)map.get("MONI_NAME");
			String moniIpAddress = (String)map.get("MONI_IP_ADDRESS");
			String moniSendAddress = (String)map.get("MONI_SEND_ADDRESS");
			String moniRevAddress = (String)map.get("MONI_REV_ADDRESS");
			
			String content = "IP："+moniIpAddress+"\n"+"示例名称："+moniName+"\n"+"最后心跳时间："+modifyDate+"\n";
			if(SysUtility.isEmpty(moniSendAddress)){
				moniSendAddress = SysUtility.GetProperty("mail.properties","mail.mailFrom");
			}
			if(SysUtility.isEmpty(moniRevAddress)){
				moniRevAddress = SysUtility.GetProperty("mail.properties","mail.mailTO");
			}
			if(SysUtility.isEmpty(moniSendAddress) || SysUtility.isEmpty(moniRevAddress)){
				return;
			}
			
			SysUtility.SendMail("交换服务报错通知 - "+moniName+"异常",moniSendAddress,moniRevAddress, content, null);
		}
	}
	
}
